package Importer;

import Gameplay.*;
import Gameplay.Entities.*;
import Gameplay.Entities.Characters.Character;
import Gameplay.Entities.Weapons.Weapon;

import Gameplay.Entities.Weapons.WeaponStat;
import Gameplay.Entities.Weapons.WeaponType;
import Util.GradeEnum;
import Util.Print;

import java.io.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;


public class LevelBuilder  extends Application
{

    private static final float ZOOM_MIN = 0.359f;
    private static final float ZOOM_MAX = 1.5f;

    private boolean DEBUG = false;
    private Scene scene;
    private Canvas canvas;
    private RenderThread renderThread;
    private int viewWidth, viewHeight;
    private GraphicsContext gfx;

    private EntityCollection<Entity> entityList = new EntityCollection<Entity>();
    private float lastMouseX, lastMouseY;
    private float mouseDownX, mouseDownY;
    private float mouseDownOffsetWithinBlockX, mouseDownOffsetWithinBlockY;
    private Entity selectedEntity = null;
    private int selectedVertexIdx = -1;
    //private boolean windowWasResized = false;



    private float cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY;
    private float cameraZoom, cameraZoomGoal, cameraZoomLerp = 0.05F;
    private int levelEditorOffsetX=0;
    private int levelEditorOffsetY=0;

    private final int gridPixels = 32;

    private ContextMenu menuEntity, menuMaterial;
    private MenuItem menuItemDeleteEntity, menuItemSword;

    private MenuItem menuItemToFront;


    public static void main(String[] args)
    {
        long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("heapSize = "+ heapSize/1000);

        long max = Runtime.getRuntime().maxMemory();
        System.out.println("max = "+ max/1000);
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #999999");

        cameraPosX = -4.9999995f;
        cameraPosY =  0.57286453f;
        cameraOffsetX = 9.6f;
        cameraOffsetY = 5.4f;
        cameraZoom =100.0f;

        scene = new Scene(root);
        scene.setCursor(Cursor.CROSSHAIR);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();

        viewWidth  = (int)scene.getWidth();
        viewHeight = (int)scene.getHeight();
        System.out.println("************* create canvas (" + viewWidth + ", " + viewHeight + ") *****************");

        canvas = new Canvas(viewWidth, viewHeight);

        gfx = canvas.getGraphicsContext2D();
        gfx.setFill(Color.DARKBLUE);
        gfx.setFont(new Font("Verdana", 20));

        renderThread = new RenderThread(gfx, viewWidth, viewHeight);

        menuEntity = new ContextMenu();
        menuMaterial = new ContextMenu();

        // Add weapon types
        menuItemSword = new MenuItem("LONG SWORD");
        menuEntity.getItems().add(menuItemSword);
        menuItemSword.setOnAction(this::menuEvent);

        // Add block types
        for (Entity.ShapeEnum blockType : Entity.ShapeEnum.values())
        {
            MenuItem item = new MenuItem(blockType.getText());
            menuEntity.getItems().add(item);
            item.setOnAction(this::menuEvent);
        }

        for (Block.BlockMaterial material : Block.BlockMaterial.values())
        {
            MenuItem item = new MenuItem(material.toString());
            menuMaterial.getItems().add(item);
            item.setOnAction(this::menuEvent);
        }
        menuItemToFront = new MenuItem("To Front");
        menuMaterial.getItems().add(menuItemToFront);
        menuItemToFront.setOnAction(this::menuEvent);

        menuMaterial.getItems().add(new SeparatorMenuItem());
        menuItemDeleteEntity = new MenuItem("Delete");
        menuMaterial.getItems().add(menuItemDeleteEntity);
        menuItemDeleteEntity.setOnAction(this::menuEvent);

        //Print.green("Camera: pos(" + cameraPosX + ", " + cameraPosY +")    offset(" + cameraOffsetX + ", " + cameraOffsetY + ")  zoomFactor="+cameraZoom);
        renderAll();
        root.getChildren().add(canvas);

        gfx.clearRect(80, 180, 600, 350);
        gfx.fillText("Shift-right-click to add Entity at mouse location.\n\n" +
                        "Right-click on Entity to delete.\n" +
                        "Right-click on Entity to set its Properties.\n\n" +

                        "Left-click-drag on Entity to Move.\n" +
                        "Left-click-drag on Entity Vertex to Resize.\n\n" +

                        "Middle-click-drag on canvas to scroll canvas.\n\n"+

                        "Mouse-wheel to zoom.\n\n"+

                        "Press S to Save level.\n" +
                        "Press L to Load level.",
                100, 200);

        scene.setOnMousePressed(this::mousePressed);
        scene.setOnMouseMoved(this::mouseMoved);
        scene.setOnMouseDragged(this::mouseDragged);

        scene.setOnKeyPressed(this::keyPressed);
        scene.setOnScroll(this::scrollWheelEvent);

    }


    private void scrollWheelEvent(ScrollEvent event)
    {
        //double deltaY = event.getDeltaY();
        //if (deltaY < 0) levelEditorScale = Math.max(ZOOM_MIN,levelEditorScale - 0.03f);
        //else if (deltaY > 0) levelEditorScale = Math.min(ZOOM_MAX, levelEditorScale + 0.03f);
        //if (Math.abs(levelEditorScale-1)< 0.02) levelEditorScale=1;


        //Print.purple("LevelBuilder: scrollWheelEvent: levelEditorScale="+levelEditorScale);

        //gfx.restore();
        //gfx.setFill(Color.BLACK);
        //gfx.fillRect(0, 0, viewWidth, viewHeight);
        //renderAll();

    }

    private void keyPressed(KeyEvent key)
    {
        if (key.getCode() == KeyCode.ESCAPE) System.exit(0);
        if (key.getCode() == KeyCode.L) loadFile();
        if (key.getCode() == KeyCode.S) saveFile();
        if (key.getCode() == KeyCode.ENTER) Print.blue(selectedEntity);
    }

    private void mouseMoved(MouseEvent event)
    {
        float mouseX = (float) event.getX();
        float mouseY = (float) event.getY();

        float x = (mouseX/cameraZoom) + cameraPosX - cameraOffsetX;
        float y = (mouseY/cameraZoom) + cameraPosY - cameraOffsetY;

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        Entity lastSelectedEntity = selectedEntity;
        int lastSelectedVertexIdx = selectedVertexIdx;
        selectedVertexIdx = -1;
        selectedEntity = null;

        for(int i = entityList.size() - 1; i >= 0; i--)
        {
            Entity entity = entityList.get(i);

            if (entity instanceof Block)
            {
                int vertexIdx = entity.getVertexNear(x, y, 1f / cameraZoom);
                if (vertexIdx >= 0)
                {
                    if (lastSelectedVertexIdx < 0) scene.setCursor(Cursor.NE_RESIZE);
                    selectedVertexIdx = vertexIdx;
                    selectedEntity = entity;
                    break;
                }
            }

            if (entity.isInside(x, y))
            {
                if (entity instanceof Weapon)
                {
                    if (((Weapon)entity).getActor() != null) continue;
                }
                if (lastSelectedEntity == null || lastSelectedVertexIdx > 0) scene.setCursor(Cursor.HAND);
                selectedVertexIdx = -1;
                selectedEntity = entity;
                break;
            }
        }

        if (selectedEntity == null && lastSelectedEntity != null)
        {
            scene.setCursor(Cursor.CROSSHAIR);
        }
        if (selectedEntity != lastSelectedEntity)
        {
            //renderAll();
        }
    }


    private void mouseDragged(MouseEvent event)
    {
        float mouseX = (float)event.getX();
        float mouseY = (float)event.getY();

        if (event.isSecondaryButtonDown()) return;

        if (event.isMiddleButtonDown())
        {
            //Drag world
            cameraPosX -= (mouseX - lastMouseX)/cameraZoom;
            cameraPosY -= (mouseY - lastMouseY)/cameraZoom;

            if (cameraPosX - viewWidth/1.99f/cameraZoom < -31.7f)
            {
                cameraPosX = -31.7f+viewWidth/1.99f/cameraZoom;
            }
            else if (cameraPosX + viewWidth/1.99f/cameraZoom > 20.79f)
            {
                cameraPosX = 20.79f - viewWidth / 1.99f / cameraZoom;
            }
            if (cameraPosY - viewHeight/1.99f/cameraZoom < -19.0f)
            {
                cameraPosY = -19.0f+viewHeight/1.99f/cameraZoom;
            }
            else if (cameraPosY + viewHeight/1.99f/cameraZoom >6f)
            {
                cameraPosY = 6f-viewHeight/1.99f/cameraZoom;
            }

        }
        else if (selectedVertexIdx >= 0)
        {   //Resize block
            if (selectedEntity instanceof Block)
            {
                Entity.ShapeEnum shape = selectedEntity.getShape();
                float x0 = selectedEntity.getX();
                float y0 = selectedEntity.getY();
                float vertexX = selectedEntity.getVertexX(selectedVertexIdx);
                float vertexY = selectedEntity.getVertexY(selectedVertexIdx);

                // dx and dy are signed values representing the distance and direction in world coordinates
                //     that the mouse is from the selected vertex.
                // Depending on which vertex is selected, a positive value could mean an increase or decrease
                //     in block size. In particular, moving the mouse farther form the box center makes it larger
                //     and moving it nearer the center makes it smaller.
                float dx = Math.abs(((mouseX / cameraZoom) + cameraPosX - cameraOffsetX) - x0) - Math.abs(vertexX - x0);
                float dy = Math.abs(((mouseY / cameraZoom) + cameraPosY - cameraOffsetY) - y0) - Math.abs(vertexY - y0);

                float width  = selectedEntity.getWidth()  + dx;
                float height = selectedEntity.getHeight() + dy;
                if (shape == Entity.ShapeEnum.RECTANGLE)
                {
                    width  = (gridPixels/cameraZoom) * Math.max(2, Math.round((width  * cameraZoom) / gridPixels));
                    height = (gridPixels/cameraZoom) * Math.max(2, Math.round((height * cameraZoom) / gridPixels));
                }
                else if (shape == Entity.ShapeEnum.RAMP_RIGHT18 || shape == Entity.ShapeEnum.RAMP_LEFT18)
                {
                    width  = (3*gridPixels/cameraZoom) * Math.max(3, Math.round((width  * cameraZoom) / (3*gridPixels)));
                    height = (gridPixels/cameraZoom) * Math.max(1, Math.round((height * cameraZoom) / gridPixels));
                    if (Math.abs(dx)>Math.abs(dy)) height = width/3.0f;
                    else width = 3.0f*height;
                }
                selectedEntity.setSize(width, height);
            }

        }
        else if (selectedEntity != null)
        {   //Move entity
            float x = ((mouseX/cameraZoom) + cameraPosX - cameraOffsetX )-mouseDownOffsetWithinBlockX;
            float y = ((mouseY/cameraZoom) + cameraPosY - cameraOffsetY )-mouseDownOffsetWithinBlockY;

            x = (Math.round((x*cameraZoom)/(gridPixels/2))*(gridPixels/2))/cameraZoom;
            y = (Math.round((y*cameraZoom)/(gridPixels/2))*(gridPixels/2))/cameraZoom;

            //Print.purple("("+selectedEntity.getX()+", " + selectedEntity.getY()+") -> (" + x+", " + y+")");

            selectedEntity.setPosition(x, y);

            if (selectedEntity instanceof Actor)
            {
                Actor actor = ((Actor)selectedEntity);
                Weapon[] weapons = actor.getWeapons();
                for (int i = 0; i < weapons.length; i++)
                {
                    if (weapons[i] != null && i > 0)
                    {
                        weapons[i].setPosition(actor.getX(), actor.getY());
                    }
                }
            }
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;
        //Print.green("Camera: pos(" + cameraPosX + ", " + cameraPosY +")    offset(" + cameraOffsetX + ", " + cameraOffsetY + ")  zoomFactor="+cameraZoom);
        gfx.setFill(Color.BLACK);
        gfx.fillRect(0, 0, viewWidth, viewHeight);
        renderAll();

    }

    private void mousePressed(MouseEvent event)
    {
        //Print.green("Camera: pos(" + cameraPosX + ", " + cameraPosY +")    offset(" + cameraOffsetX + ", " + cameraOffsetY + ")  zoomFactor="+cameraZoom);

        mouseDownX = (float)event.getX();
        mouseDownY = (float)event.getY();
        lastMouseX = mouseDownX;
        lastMouseY = mouseDownY;

        if (event.isMiddleButtonDown())
        {
            unselect();
            return;
        }

        else if (event.isSecondaryButtonDown())
        {
            if(event.isShiftDown())
            {
                //Show add Entity menu
                unselect();
                menuEntity.show(canvas, event.getScreenX(), event.getScreenY());
            }
            else if (selectedEntity != null)
            {
                menuEntity.hide();
                if (selectedEntity instanceof Block)
                {
                    menuMaterial.show(canvas, event.getScreenX(), event.getScreenY());
                }
            }
        }
        else if (event.isPrimaryButtonDown())
        {
            menuEntity.hide();
            menuMaterial.hide();
            if (selectedEntity != null)
            {
                //Save location within the selected entity that the mouse is clicked so entity can be smoothly moved.
                mouseDownOffsetWithinBlockX = ((mouseDownX/cameraZoom) + cameraPosX - cameraOffsetX) - selectedEntity.getX();
                mouseDownOffsetWithinBlockY = ((mouseDownY/cameraZoom) + cameraPosY - cameraOffsetY) - selectedEntity.getY();
            }
        }
        renderAll();

    }



    private void menuEvent(ActionEvent e)
    {
        float x = mouseDownX/cameraZoom + cameraPosX - cameraOffsetX;
        float y = mouseDownY/cameraZoom + cameraPosY - cameraOffsetY;
        MenuItem item = (MenuItem) e.getSource();
        String text = item.getText();
        if (DEBUG) System.out.println("LevelBuilder::menuEvent("+text+")");

        else if (item == menuItemDeleteEntity)
        {
            if (selectedEntity != null) entityList.remove(selectedEntity);
            unselect();
        }
        else if (item == menuItemToFront)
        {
            if (selectedEntity != null)
            {
                entityList.remove(selectedEntity);
                entityList.add(selectedEntity);
            }
        }

        else //check if selected menu item is add entity
        {
            boolean addedEntity = false;
            for (Entity.ShapeEnum shape : Entity.ShapeEnum.values())
            {
                if (text.equals(shape.getText()))
                {
                    x = (Math.round((x*cameraZoom)/gridPixels)*gridPixels)/cameraZoom;
                    y = (Math.round((y*cameraZoom)/gridPixels)*gridPixels)/cameraZoom;
                    float width = 6*gridPixels/cameraZoom;
                    float height = 2*gridPixels/cameraZoom;
                    Block block = new Block(x, y, width, height, shape, 1.0F, null);
                    entityList.add(block);
                    addedEntity = true;
                    break;
                }
            }
            if (!addedEntity)
            {
                Character character = Character.get(text);
                if (character != Character.character_)
                {
                    Actor actor = new Actor(x, y, character);
                    actor.setSize(actor.getWidth()/Entity.SPRITE_TO_WORLD_SCALE, actor.getHeight()/Entity.SPRITE_TO_WORLD_SCALE);
                    actor.setPosition(x, y);

                    entityList.add(actor);
                    addedEntity = true;
                }
            }
            if (!addedEntity)
            {
                if (text.equals("LONG_SWORD"))
                {
                    WeaponStat weaponStat = new WeaponStat(
                            "C", "C", "C", "C", 1, null, null, "C", "D", "D");
                    Weapon sword = new Weapon(x, y, 0.5F, 0.1F, GradeEnum.F__,
                            WeaponType.SWORD, weaponStat, null);
                    entityList.add(sword);
                    addedEntity = true;
                }
                else if (text.equals("GLAIVE"))
                {
                    WeaponStat weaponStat = new WeaponStat(
                            "C", "C", "C", "C", 1, null, null, "C", "D","D");
                    Weapon polearm = new Weapon(x, y, 0.7F, 0.1F, GradeEnum.F__,
                            null, weaponStat, null); // TODO: put weaponType instead of null
                    entityList.add(polearm);
                    addedEntity = true;
                }
            }
            if (!addedEntity && (selectedEntity != null))
            {
                for (Block.BlockMaterial material : Block.BlockMaterial.values())
                {
                    if (text.equals(material.toString()))
                    {
                        Block block = (Block)selectedEntity;
                        block.setTextureType(material);
                        break;
                    }
                }
            }
        }
        renderAll();

    }


    private void unselect()
    {
        menuMaterial.hide();
        menuEntity.hide();
        selectedEntity = null;
        selectedVertexIdx = -1;
    }




    //============================================================================================
    //                               saveFile()
    //
    // Called when the user presses Ctrl-S button.
    // Convert pixel coordinates to world (20 world = 1000 pixels)
    //    center of scene is (0,0)
    //    -y is up.
    //
    //============================================================================================
    private void saveFile()
    {
        System.out.println("LevelBuilder.saveFile()!");
        BufferedWriter writer = fileChooserWrite();
        if (writer == null) return;

        if (entityList.isEmpty()) return;

        try
        {
            writer.write("Type,Texture,CenterX,CenterY,Width,Height\n");
            for (Entity entity : entityList)
            {
                if (DEBUG) System.out.println("LevelBuilder.saveFile(): "+entity);

                int x = Math.round(entity.getX()/Entity.SPRITE_TO_WORLD_SCALE);
                int y = Math.round(entity.getY()/Entity.SPRITE_TO_WORLD_SCALE);
                int w = Math.round(entity.getWidth()/Entity.SPRITE_TO_WORLD_SCALE);
                int h = Math.round(entity.getHeight()/Entity.SPRITE_TO_WORLD_SCALE);
                String stats = x + "," + y;
                String type = "";

                if (entity instanceof Weapon)
                {
                    if (((Weapon) entity).isIdle())
                    {
                        Weapon weapon = (Weapon) entity;
                        Print.green("save: " + weapon.getName());
                        type = "Weapon";
                        stats += "," + w + "," + h + "," + weapon.getMass() + "," + weapon.getName() + ","
                                + weapon.getStatDataString();
                    }
                    else continue;
                }
                else if (entity instanceof Block)
                {
                    Block block = (Block)entity;
                    String textureStr = "WATER";
                    if (!block.isLiquid()) textureStr = String.valueOf(block.getEdgeBits());
                    type = block.getShape().toString() + "," + textureStr;
                    stats += "," + w + "," + h;
                }
                else if (entity instanceof Actor)
                {
                    type =  "Player,"+((Actor)entity).getName();
                }
                else
                {
                    System.out.println("************ERROR**************");
                    System.out.println("     LevelBuilder attempting to save file with unknown type:");
                    System.out.println("     "+entity);
                }
                Print.green("save: " +type+","+stats);
                writer.write(type+","+stats+"\n");
            }

            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    //============================================================================================
    //============================================================================================
    private void loadFile()
    {
        System.out.println("LevelBuilder.loadFile()");

        String path = fileChooserOpenGetPath();
        if (path == null) return;
        try
        {
            InputStream input = new BufferedInputStream(new FileInputStream(path));
            if (input != null) entityList = loadLevel(input);
            else Print.red("\"" + path + "\" was not imported");
        }
        catch (IOException e)
        {
            Print.red("\"" + path + "\": Could not open file");
        }

        //Print.green("Camera: pos(" + cameraPosX + ", " + cameraPosY +")    offset(" + cameraOffsetX + ", " + cameraOffsetY + ")  zoomFactor="+cameraZoom);
        renderAll();
    }



    //============================================================================================
    //============================================================================================
    public static EntityCollection<Entity> loadLevel(InputStream input)
    {
        System.out.println("LevelBuilder.loadLevel() Enter ======>");
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            EntityCollection<Entity> entityList = new EntityCollection();

            String line = reader.readLine(); //header line
            line = reader.readLine();
            while (line != null) {
                String[] data = line.split(",");

                if (data.length < 4)
                {
                    System.out.println("Error Reading Line: ["+line+"]");
                    throw new IOException("Each record must have at least 4 fields.");
                }

                Entity entity = null;
                float x = Float.parseFloat(data[2])*Entity.SPRITE_TO_WORLD_SCALE;
                float y = Float.parseFloat(data[3])*Entity.SPRITE_TO_WORLD_SCALE;

                if (data[0].equals("Player"))
                {
                    if (data.length != 4)
                    {
                        System.out.println("Error Reading Line: ["+line+"]");
                        throw new IOException("Player record must have 4 fields.");
                    }

                    entity = new Actor(x, y, Character.get(data[1]));
                }
                else if (data[0].equals("Weapon"))
                {
                    if (data.length != 18)
                    {
                        System.out.println("Error Reading Line: ["+line+"]");
                        throw new IOException("Weapon record must have 17 fields.");
                    }
                    float width = Float.parseFloat(data[3])*Entity.SPRITE_TO_WORLD_SCALE;
                    float height = Float.parseFloat(data[4])*Entity.SPRITE_TO_WORLD_SCALE;
                    GradeEnum mass = GradeEnum.parseGrade(data[5]);
                    WeaponType type = WeaponType.NATURAL;
                    if (data[6].equals("Long_Sword")) type = WeaponType.SWORD;
                    WeaponStat stat = new WeaponStat(data[7], data[8], data[9], data[10],
                            Integer.parseInt(data[11]), null, null, data[14], data[15], data[16], null);
                    entity = new Weapon(x, y, width, height, mass, type, stat, null);
                }
                else
                {
                    if (data.length != 6)
                    {
                        System.out.println("Error Reading Line: ["+line+"]");
                        throw new IOException("Block record must have 6 fields.");
                    }

                    Entity.ShapeEnum shape = Entity.ShapeEnum.valueOf(data[0]);
                    float width = Float.valueOf(data[4])*Entity.SPRITE_TO_WORLD_SCALE;
                    float height = Float.valueOf(data[5])*Entity.SPRITE_TO_WORLD_SCALE;
                    Block block = new Block(x, y, width, height, shape, 1.0F, null);
                    if (data[1].equals("WATER")) block.setTextureType(Block.BlockMaterial.WATER);
                    else
                    {
                        block.setTextureType(Integer.parseInt(data[1]));
                    }
                    entity = block;
                }
                entityList.add(entity);
                line = reader.readLine();
            }
            reader.close();

            return entityList;
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    //============================================================================================
    //                               fileChooserWrite()
    //
    // Called when the user presses Ctrl-S button.
    // This method displays a file chooser dialog that allows the user to browse folders
    //    to select a .txt or a .csv file.
    //
    //============================================================================================
    private static BufferedWriter fileChooserWrite()
    {
        BufferedWriter writer = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Level File");
        fileChooser.setInitialDirectory(new File("Resources/Levels"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null)
        {
            try
            {
                writer = new BufferedWriter(new FileWriter(selectedFile));
            } catch (IOException e)
            {
                /*Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Error");
                alert.setHeaderText("IO Exception:");
                alert.setContentText(e.getMessage());
                alert.showAndWait();*/
                return null;
            }
        }

        return writer;
    }

    //============================================================================================
    //                               fileChooserRead()
    //
    // Called when the user presses Ctrl-L button.
    // This method displays a file chooser dialog that allows the user to browse folders
    //    to select a .txt or a .csv file.
    //
    // The selected file is opened in a BufferedReader.
    // The BufferedReader is assigned to the class variable BufferedReader reader.
    // The name of the selected file is assigned to the class variable String filename.
    //
    // Returns true if a file was successfully selected, opened and the BufferedReader
    // successfully created.
    // Otherwise, an error dialog is displayed and the method returns false.
    //============================================================================================
    private static String fileChooserOpenGetPath()
    {
        System.out.println("LevelBuilder.fileChooserRead()");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Level File");
        fileChooser.setInitialDirectory(new File("Resources/Levels"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null)
        {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

    private void renderAll()
    {
        renderThread.renderAll(entityList, cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY, cameraZoom);
        renderThread.renderGrid(gridPixels, cameraPosX, cameraPosY, cameraOffsetX, cameraOffsetY, cameraZoom);
    }
}

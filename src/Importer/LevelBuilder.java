package Importer;
import Gameplay.CameraZone;
import Gameplay.Characters.CharacterStat;
import Gameplay.Entity;
import Gameplay.Block;
import Gameplay.Actor;
import Gameplay.Item;
import Gameplay.EntityCollection;
import Gameplay.Weapons.Natural;
import Gameplay.Weapons.Sword;
import Gameplay.Weapons.Weapon;
import Gameplay.Weapons.WeaponStat;
import Util.Vec2;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class LevelBuilder  extends Application
{

    private static final float ZOOM_MIN = 0.4f;
    private static final float ZOOM_MAX = 2.5f;

    private boolean DEBUG = true;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gtx;
    private ContextMenu menuEntity, menuMaterial,  menuCameraZoom;
    //private ArrayList<Entity> entityList = new ArrayList<>();
    private EntityCollection<Entity> entityList = new EntityCollection();
    private float lastMouseX, lastMouseY;
    private float mouseDownX, mouseDownY;
    private float mouseDownOffsetWithinBlockX, mouseDownOffsetWithinBlockY;
    private Entity selectedEntity = null;
    private int selectedVertexIdx = -1;
    private boolean windowWasResized = false;
    private int offsetX=0;
    private int offsetY=0;
    private float zoomFactor = 1.0f;

    private static float scale = 1f/50f;

    private RadioMenuItem menuItemStone, menuItemWater;
    private MenuItem menuItemDeleteEntity, menuItemDeleteCameraZone;
    private MenuItem menuItemAddCameraZone;

    private static final int[] CAMERA_ZOOM_PRESETS = {100, 90, 75, 60, 50, 40, 25};
    private RadioMenuItem[] menuItemCameraZoom = new RadioMenuItem[CAMERA_ZOOM_PRESETS.length];

    private static final Color lightTranslucentGreen = Color.rgb(135, 169, 107, 0.5);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hermano Level Builder");


        canvas = new Canvas(100, 60);
        gtx = canvas.getGraphicsContext2D();

        menuEntity = new ContextMenu();
        menuMaterial = new ContextMenu();
        menuCameraZoom = new ContextMenu();


        menuItemAddCameraZone = new MenuItem("Add Camera Zone");
        menuItemAddCameraZone.setOnAction(this::menuEvent);
        menuEntity.getItems().add(menuItemAddCameraZone);

        menuEntity.getItems().add(new SeparatorMenuItem());
        for (Entity.ShapeEnum shape : Entity.ShapeEnum.values()) {
            MenuItem item = new MenuItem("Add " + shape.getText());
            menuEntity.getItems().add(item);
            item.setOnAction(this::menuEvent);
        }

        ToggleGroup toggleGroupZoomLevel = new ToggleGroup();
        int idx = 0;
        for (int zoomValue : CAMERA_ZOOM_PRESETS) {
            menuItemCameraZoom[idx] = new RadioMenuItem("Camera Zone zoom " + zoomValue);
            menuCameraZoom.getItems().add(menuItemCameraZoom[idx]);
            menuItemCameraZoom[idx].setToggleGroup(toggleGroupZoomLevel);
            menuItemCameraZoom[idx].setOnAction(this::menuEvent);
            idx++;
        }
        menuCameraZoom.getItems().add(new SeparatorMenuItem());
        menuItemDeleteCameraZone = new MenuItem("Delete");
        menuCameraZoom.getItems().add(menuItemDeleteCameraZone);
        menuItemDeleteCameraZone.setOnAction(this::menuEvent);


        menuItemStone = new RadioMenuItem("Stone");
        menuItemWater = new RadioMenuItem("Water");

        menuMaterial.getItems().add(menuItemStone);
        menuMaterial.getItems().add(menuItemWater);
        menuMaterial.getItems().add(new SeparatorMenuItem());
        menuItemDeleteEntity = new MenuItem("Delete");
        menuMaterial.getItems().add(menuItemDeleteEntity);
        menuItemStone.setOnAction(this::menuEvent);
        menuItemWater.setOnAction(this::menuEvent);
        menuItemDeleteEntity.setOnAction(this::menuEvent);

        ToggleGroup toggleGroupMaterial = new ToggleGroup();
        menuItemStone.setToggleGroup(toggleGroupMaterial);
        menuItemWater.setToggleGroup(toggleGroupMaterial);

        gtx.setFill(Color.DARKBLUE);
        gtx.setFont(new Font("Verdana", 20));

        Pane root = new Pane();
        root.setStyle("-fx-background-color: #999999");

        scene = new Scene(root);
        scene.setCursor(Cursor.CROSSHAIR);
        stage.setScene(scene);
        stage.show();
        createCanvas();
        root.getChildren().add(canvas);

        gtx.clearRect(80, 180, 600, 350);
        gtx.fillText("Shift-right-click to add Entity at mouse location.\n\n" +
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

        scene.widthProperty().addListener(this::windowResize);
        scene.heightProperty().addListener(this::windowResize);

        scene.setOnKeyPressed(this::keyPressed);
        scene.setOnScroll(this::scrollWheelEvent);
    }

    private void scrollWheelEvent(ScrollEvent event) {
        double deltaY = event.getDeltaY();
        float zoom2 = zoomFactor;
        if (deltaY < 0) zoom2 = Math.max(ZOOM_MIN,zoomFactor - 0.05f);
        else if (deltaY > 0) zoom2 = Math.min(ZOOM_MAX, zoomFactor + 0.05f);

        if (Math.abs(zoom2 - 1.0) < 0.001) zoom2= 1.0f;
        offsetX += lastMouseX/zoom2 - lastMouseX/zoomFactor;
        offsetY += lastMouseY/zoom2 - lastMouseY/zoomFactor;
        zoomFactor = zoom2;

        //System.out.println("zoomFactor="+zoomFactor);
        gtx.restore();
        gtx.save();
        gtx.scale(zoomFactor,zoomFactor);
        renderAll();
    }

    private void keyPressed(KeyEvent key)
    {
        if (key.getCode() == KeyCode.L) loadFile();
        if (key.getCode() == KeyCode.S) saveFile();
    }

    //=================================================================================================================
    // When the user is resizing the window, there are many resize width and resize height events generated.
    // This avoids creating a new canvas and imageBaseLayer or every event.
    //=================================================================================================================
    private void windowResize(Observable value) {
        //System.out.println("scene Width: " + scene.getWidth());
        //System.out.println("canvas Width: " + canvas.getWidth());
        windowWasResized = true;
    }

    private void createCanvas() {
        windowWasResized = false;
        int width  = (int)scene.getWidth();
        int height = (int)scene.getHeight();
        System.out.println("************* create canvas (" + width + ", " + height + ") *****************");

        if (canvas == null) {
            canvas = new Canvas(width, height);
        }

        else if ((canvas.getWidth() < width) || (canvas.getHeight() < height))
        {
            canvas.setWidth(width);
            canvas.setHeight(height);
        }
        renderAll();
    }

    private void mouseMoved(MouseEvent event)
    {
        if (windowWasResized)
        {
            createCanvas();
            return;
        }
        float mouseX = (float) event.getX();
        float mouseY = (float) event.getY();
        float x = mouseX/zoomFactor - offsetX;
        float y = mouseY/zoomFactor - offsetY;

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        Entity lastSelectedEntity = selectedEntity;
        int lastSelectedVertexIdx = selectedVertexIdx;
        selectedVertexIdx = -1;
        selectedEntity = null;

        for(int i = entityList.size() - 1; i >= 0; i--)
        {
            Entity entity = entityList.get(i);
            if (entity instanceof Weapon)
            {
                if (((Weapon)entity).getActor() != null) continue;
            }
            int vertexIdx = entity.getVertexNear(x, y);
            if (vertexIdx >= 0)
            {
                if (lastSelectedVertexIdx < 0) scene.setCursor(Cursor.NE_RESIZE);
                selectedVertexIdx = vertexIdx;
                selectedEntity = entity;
                break;
            }

            else if (entity.isInside(x, y))
            {
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
            renderAll();
        }
        return;
    }


    private void mouseDragged(MouseEvent event) {
        float mouseX = (float)event.getX();
        float mouseY = (float)event.getY();

        if (event.isSecondaryButtonDown()) return;

        if (event.isMiddleButtonDown())
        {
            //Drag world
            offsetX += (mouseX - lastMouseX);
            offsetY += (mouseY - lastMouseY);
        }
        else if (selectedVertexIdx >= 0)
        {   //Resize block
            float x0 = selectedEntity.getX();
            float y0 = selectedEntity.getY();
            float px = selectedEntity.getVertexX(selectedVertexIdx);
            float py = selectedEntity.getVertexY(selectedVertexIdx);
            float dx = (((mouseX/zoomFactor)-offsetX) - x0) - (px - x0);
            float dy = (((mouseY/zoomFactor)-offsetY) - y0) - (py - y0);
            dx = Math.round(dx/10)*10;
            dy = Math.round(dy/10)*10;
            selectedEntity.setPosition(x0+dx/2, y0+dy/2);
            float width  = Math.max(20, selectedEntity.getWidth()  + dx*Math.signum(px - x0));
            float height = Math.max(20, selectedEntity.getHeight() + dy*Math.signum(py - y0));
            selectedEntity.setSize(width, height);
        }
        else if (selectedEntity != null)
        {  //Move block
            float x = Math.round(((mouseX-offsetX)/zoomFactor-mouseDownOffsetWithinBlockX)/10)*10;
            float y = Math.round(((mouseY-offsetY)/zoomFactor-mouseDownOffsetWithinBlockY)/10)*10;
            if (selectedEntity.getWidth() % 20 != 0)  x+=5;
            if (selectedEntity.getHeight() % 20 != 0) y+=5;
            selectedEntity.setPosition(x, y);

            if (selectedEntity instanceof Actor)
            {
                Actor actor = ((Actor)selectedEntity);
                for (Weapon weapon : actor.getWeapons())
                {
                    if (weapon != null)
                    {
                        if (weapon instanceof Natural) continue;
                        weapon.moveToParent();
                    }
                }
            }
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;
        renderAll();
    }

    private void mousePressed(MouseEvent event)
    {
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
                    //Show Menu to modify selected block
                    if (((Block) selectedEntity).isLiquid()) menuItemWater.setSelected(true);
                    else menuItemStone.setSelected(true);
                    menuCameraZoom.hide();
                    menuMaterial.show(canvas, event.getScreenX(), event.getScreenY());

                }
                else if (selectedEntity instanceof CameraZone)
                {
                    //Show Menu to modify selected CameraZone
                    int zoom = (int)((CameraZone) selectedEntity).getZoom();
                    for (int i=0; i<CAMERA_ZOOM_PRESETS.length; i++)
                    {   if (zoom == CAMERA_ZOOM_PRESETS[i])
                        {
                            menuItemCameraZoom[i].setSelected(true);
                            break;
                        }
                    }
                    menuMaterial.hide();
                    menuCameraZoom.show(canvas, event.getScreenX(), event.getScreenY());
                }
            }
        }
        else if (event.isPrimaryButtonDown())
        {
            menuEntity.hide();
            menuMaterial.hide();
            menuCameraZoom.hide();
            if (selectedEntity != null)
            {
                //Save location within the selected entity that the mouse is clicked so entity can be smoothly moved.
                mouseDownOffsetWithinBlockX = (mouseDownX - offsetX)/zoomFactor - selectedEntity.getX();
                mouseDownOffsetWithinBlockY = (mouseDownY - offsetY)/zoomFactor - selectedEntity.getY();
            }
        }
        renderAll();
    }



    private void menuEvent(ActionEvent e)
    {
        float x = Math.round(((mouseDownX / zoomFactor) - offsetX) / 10) * 10;
        float y = Math.round(((mouseDownY / zoomFactor) - offsetY) / 10) * 10;
        MenuItem item = (MenuItem) e.getSource();
        String text = item.getText();
        if (DEBUG) System.out.println("LevelBuilder::menuEvent("+text+")");

        if (item == menuItemStone)
        {
            if ((selectedEntity != null) && (selectedEntity instanceof Block))
            {
                ((Block) selectedEntity).setLiquid(false);
                unselect();
            }
        }
        else if (item == menuItemWater)
        {
            if ((selectedEntity != null) && (selectedEntity instanceof Block))
            {
                ((Block) selectedEntity).setLiquid(true);
                unselect();
            }
        }
        else if ((item == menuItemDeleteEntity) || (item == menuItemDeleteCameraZone))
        {
            if (selectedEntity != null) entityList.remove(selectedEntity);
            unselect();
        }
        else if (item == menuItemAddCameraZone)
        {
            CameraZone zone = new CameraZone(x, y, 500, 300, 100);
            entityList.add(zone);
            unselect();
        }

        else //check if selected menu item is add entity or modify camera zone
        {
            boolean addedBlock = false;
            for (Entity.ShapeEnum shape : Entity.ShapeEnum.values())
            {
                if (text.endsWith(shape.getText()))
                {
                    Block block = new Block(x, y, 100, 100, shape, null);
                    entityList.add(block);
                    addedBlock = true;
                    break;
                }
            }
            if ((!addedBlock) && selectedEntity instanceof CameraZone)
            {
                if (text.startsWith("Camera Zone"))
                {
                    int value = Integer.valueOf(text.substring(text.length() - 3).trim());
                    ((CameraZone) selectedEntity).setZoom(value);
                }
            }
        }
        renderAll();
    }


    private void unselect()
    {
        menuMaterial.hide();
        menuCameraZoom.hide();
        menuEntity.hide();
        selectedEntity = null;
        selectedVertexIdx = -1;
    }


    private void renderAll() {
        int width  = (int)(canvas.getWidth()/zoomFactor);
        int height = (int)(canvas.getHeight()/zoomFactor);

        gtx.clearRect(0, 0, width, height);
        gtx.setStroke(Color.BLACK);
        gtx.setLineWidth(1);
        int xStart = offsetX % 50;
        int yStart = offsetY % 10;
        for (int x = xStart; x < width; x += 50)
        {
            for (int y = yStart; y < height; y += 10)
            {
                gtx.strokeLine(x,y,x,y+1);
            }
        }
        xStart = offsetX % 10;
        yStart = offsetY % 50;
        for (int y = yStart; y < height; y += 50)
        {
            for (int x = xStart; x < width; x += 10)
            {
                gtx.strokeLine(x,y,x+1,y);
            }
        }

        for (Entity block : entityList) {
            render(block);
        }
    }
    private void render(Entity block) {
        //System.out.println("    render() "+block.getShape());
        if (block == selectedEntity)
        {
            if (block instanceof CameraZone) gtx.setFill(lightTranslucentGreen);
            else gtx.setFill(Color.DARKGREEN);
        }
        else gtx.setFill(block.getColor());

        if (block.getShape().isTriangle())
        {
            double[] xPos = new double[3];
            double[] yPos = new double[3];

            for (int i = 0; i < 3; i++)
            {
                xPos[i] = block.getVertexX(i)+offsetX;
                yPos[i] = block.getVertexY(i)+offsetY;
            }
            gtx.fillPolygon(xPos, yPos, 3);
        }
        else if (block.getShape() == Entity.ShapeEnum.RECTANGLE)
        {
            Vec2 pos = block.getPosition();
            float x = offsetX + pos.x - block.getWidth() / 2;
            float y = offsetY + pos.y - block.getHeight() / 2;
            gtx.fillRect(x, y, block.getWidth(), block.getHeight());
        }
    }


    private static Actor createPlayer(float x, float y, float width, float height, float mass)
    {
        CharacterStat player1Stat = new CharacterStat(
                "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C");
        WeaponStat player1NaturalStat = new WeaponStat("C",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR");
        Actor player = new Actor(player1Stat, player1NaturalStat,x, y, width, height, mass, null); //SPRITES
        return player;
    }



    private static Sword createSword(float x, float y, float width, float height, float mass)
    {
        WeaponStat swordStat = new WeaponStat("C",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR",
                "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR", "C", "STR");
        Sword sword = new Sword(swordStat,x, y, width, height, mass, null);
        return sword;
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
        System.out.println("LevelBuilder.saveFile()");
        BufferedWriter writer = fileChooserWrite();
        if (writer == null) return;

        if (entityList.isEmpty()) return;

        try
        {
            writer.write("Type,CenterX,CenterY,Width,Height,Liquid / Mass / Zoom,parent\n");
            for (Entity entity : entityList)
            {
                int x = Math.round(entity.getX());
                int y = Math.round(entity.getY());
                int w = Math.round(entity.getWidth());
                int h = Math.round(entity.getHeight());
                String stats = x + "," + y + "," + w + "," + h;
                String type = "";
                if (entity instanceof Block)
                {
                    type = entity.getShape().toString();
                    stats += ","+((Block)entity).isLiquid();
                }
                else if (entity instanceof CameraZone)
                {
                    type =  "CameraZone";
                    stats += ","+((CameraZone)entity).getZoom();
                }
                else if (entity instanceof Actor)
                {
                    type =  "Player";
                    stats += ","+((Actor)entity).getMass();
                }
                else if (entity instanceof Sword)
                {
                    Sword sword = ((Sword)entity);
                    type =  "Sword";
                    Actor actor = sword.getActor();
                    int playerIdx = -1;
                    for (int i=0; i<entityList.getPlayerCount(); i++)
                    {
                        if (actor == entityList.getPlayer(i))
                        {
                            playerIdx = i;
                            break;
                        }
                    }
                    stats += ","+sword.getMass() + ","+playerIdx;
                }
                writer.write(type+","+stats+"\n");
            }

            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    //============================================================================================
    //                               loadFile()
    //
    // Called when the user presses Ctrl-S button.
    // Convert pixel coordinates to world (20 world = 1000 pixels)
    //    center of scene is (0,0)
    //    -y is up.
    //
    //============================================================================================
    private void loadFile()
    {
        System.out.println("LevelBuilder.loadFile()");
        scale = 1;

        String path = fileChooserOpenGetPath();
        entityList = loadLevel(path);

        //Center the view of all entities
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Entity entity : entityList)
        {
            if (entity.getLeftEdge() < minX) minX = (int)entity.getLeftEdge();
            if (entity.getTopEdge()  < minY) minY = (int)entity.getTopEdge();
            if (entity.getRightEdge()  > maxX) maxX = (int)entity.getRightEdge();
            if (entity.getBottomEdge() > maxY) maxY = (int)entity.getBottomEdge();
        }

        int width  = (int)canvas.getWidth();
        int height = (int)canvas.getHeight();
        offsetX=(width+maxX+minX)/2;
        offsetY=(height+maxY+minY)/2;

        System.out.println("     X:["+minX + " -> " + maxX + "]");
        System.out.println("     Y:["+minY + " -> " + maxY + "]");

        System.out.println("     offset: " + offsetX + ", " + offsetY);

        renderAll();

    }



    //============================================================================================
    //============================================================================================
    public static EntityCollection<Entity> loadLevel(String path)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            EntityCollection<Entity> entityList = new EntityCollection();

            String line = reader.readLine(); //header line
            line = reader.readLine();
            while (line != null) {
                String[] data = line.split(",");

                if (data.length < 6)
                {
                    System.out.println("Error Reading Line: ["+line+"]");
                    throw new IOException("each record must have at least 6 fields");
                }

                Entity entity = null;
                float x = (Float.valueOf(data[1]))*scale;
                float y = (Float.valueOf(data[2]))*scale;
                float width = Float.valueOf(data[3])*scale;
                float height = Float.valueOf(data[4])*scale;

                if (data[0].equals("CameraZone"))
                {
                    float zoom = Float.valueOf(data[5]);
                    entity = new CameraZone(x, y, width, height, zoom);
                }
                else if (data[0].equals("Player"))
                {
                    float mass = Float.valueOf(data[5]);
                    entity = createPlayer(x, y, width, height, mass);
                }
                else if (data[0].equals("Sword"))
                {
                    float mass = Float.valueOf(data[5]);
                    int parent = Integer.valueOf(data[6]);
                    entity = createSword(x, y, width, height, mass);
                    if (parent >= 0)
                    {
                        entityList.getPlayer(parent).equip((Weapon)entity);
                    }
                }
                else {
                    boolean isLiquid = Boolean.valueOf(data[1]);
                    Entity.ShapeEnum shape = Entity.ShapeEnum.valueOf(data[0]);
                    entity = new Block(x, y, width, height, shape, null);
                    ((Block)entity).setLiquid(isLiquid);
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
        fileChooser.setTitle("Save Hermano Level File");
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
        fileChooser.setTitle("Open Hermano Level File");
        fileChooser.setInitialDirectory(new File("Resources/Levels"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null)
        {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}

package Importer;
import Gameplay.Entity;
import Gameplay.Block;
import Gameplay.Actor;
import Util.Vec2;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class LevelBuilder  extends Application {

    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gtx;
    private ContextMenu menuBlock, menuMaterial;
    private ArrayList<Block> blockList = new ArrayList<>();
    private float lastMouseX, lastMouseY;
    private float mouseDownX, mouseDownY;
    private float mouseDownOffsetWithinBlockX, mouseDownOffsetWithinBlockY;
    private Block selectedBlock = null;
    private int selectedVertexIdx = -1;
    private boolean windowWasResized = false;
    private int offsetX=0;
    private int offsetY=0;
    private float zoomFactor = 1.0f;

    private Actor player1, player2;

    private RadioMenuItem menuItemStone, menuItemWater;
    private MenuItem menuItemDelete;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hermano Level Builder");


        canvas = new Canvas(100, 60);
        gtx = canvas.getGraphicsContext2D();

        menuBlock = new ContextMenu();
        menuMaterial = new ContextMenu();

        for (Entity.ShapeEnum shape : Entity.ShapeEnum.values()) {
            MenuItem item = new MenuItem("Add " + shape.getText());
            menuBlock.getItems().add(item);
            item.setOnAction(this::menuEvent);
        }

        menuItemStone = new RadioMenuItem("Stone");
        menuItemWater = new RadioMenuItem("Water");
        menuItemDelete = new MenuItem("Delete Block");
        menuMaterial.getItems().add(menuItemStone);
        menuMaterial.getItems().add(menuItemWater);
        menuMaterial.getItems().add(new SeparatorMenuItem());
        menuMaterial.getItems().add(menuItemDelete);
        menuItemStone.setOnAction(this::menuEvent);
        menuItemWater.setOnAction(this::menuEvent);
        menuItemDelete.setOnAction(this::menuEvent);

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

        gtx.clearRect(80, 180, 600, 300);
        gtx.fillText("Right-click on canvas to add Block.\n" +
                        "Right-click on Block to delete.\n" +
                        "Right-click on Block to set liquid (default is solid).\n\n" +

                        "Left-click-drag on Block to move block.\n" +
                        "Left-click-drag on Vertex of Block to resize.\n" +
                        "Left-click-drag on canvas to scroll canvas.\n\n"+

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
        if (deltaY < 0) zoom2 = Math.max(0.1f,zoomFactor - 0.05f);
        else if (deltaY > 0) zoom2 = Math.min(2.5f, zoomFactor + 0.05f);

        if (Math.abs(zoom2 - 1.0) < 0.001) zoom2= 1.0f;
        offsetX += lastMouseX/zoom2 - lastMouseX/zoomFactor;
        offsetY += lastMouseY/zoom2 - lastMouseY/zoomFactor;
        zoomFactor = zoom2;

        System.out.println("zoomFactor="+zoomFactor);
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

            //imageBaseLayer = new WritableImage(width, height);
            //pixelWriter = imageBaseLayer.getPixelWriter();
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
        float x = (float)(mouseX/zoomFactor) - offsetX;
        float y = (float)(mouseY/zoomFactor) - offsetY;

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        for (Block block : blockList)
        {
            int vertexIdx = block.getVertexNear(x, y);
            if (vertexIdx >= 0)
            {
                scene.setCursor(Cursor.NE_RESIZE);
                selectedBlock = block;
                selectedVertexIdx = vertexIdx;
                return;
            }

            if (block.isInside(x, y)) {
                scene.setCursor(Cursor.HAND);
                selectedBlock = block;
                selectedVertexIdx = -1;
                return;
            }
        }
        if (selectedVertexIdx >= 0 || selectedBlock != null)
        {
            scene.setCursor(Cursor.CROSSHAIR);
            selectedVertexIdx = -1;
            selectedBlock = null;
        }
    }


    private void mouseDragged(MouseEvent event) {
        float mouseX = (float)event.getX();
        float mouseY = (float)event.getY();

        if (event.isSecondaryButtonDown()) return;

        if (selectedVertexIdx >= 0)
        {   //Resize block
            float x0 = selectedBlock.getX();
            float y0 = selectedBlock.getY();
            float px = selectedBlock.getVertexX(selectedVertexIdx);
            float py = selectedBlock.getVertexY(selectedVertexIdx);
            //float dx = ((mouseX-offsetX) - x0) - (px - x0);
            //float dy = ((mouseY-offsetY) - y0) - (py - y0);
            float dx = (((mouseX/zoomFactor)-offsetX) - x0) - (px - x0);
            float dy = (((mouseY/zoomFactor)-offsetY) - y0) - (py - y0);
            dx = Math.round(dx/10)*10;
            dy = Math.round(dy/10)*10;
            selectedBlock.setPosition(x0+dx/2, y0+dy/2);
            float width  = Math.max(20,selectedBlock.getWidth()  + dx*Math.signum(px - x0));
            float height = Math.max(20,selectedBlock.getHeight() + dy*Math.signum(py - y0));
            selectedBlock.setSize(width, height);
        }
        else if (selectedBlock != null)
        {  //Move block
            float x = Math.round(((mouseX-offsetX)/zoomFactor-mouseDownOffsetWithinBlockX)/10)*10;
            float y = Math.round(((mouseY-offsetY)/zoomFactor-mouseDownOffsetWithinBlockY)/10)*10;
            if (selectedBlock.getWidth() % 20 != 0)  x+=5;
            if (selectedBlock.getHeight() % 20 != 0) y+=5;
            selectedBlock.setPosition(x, y);
        }
        else
        {   //Drag world
            offsetX += (mouseX - lastMouseX);
            offsetY += (mouseY - lastMouseY);
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;
        renderAll();
    }

    private void mousePressed(MouseEvent event) {
        mouseDownX = (float)event.getX();
        mouseDownY = (float)event.getY();
        lastMouseX = mouseDownX;
        lastMouseY = mouseDownY;

        renderAll();

        if (event.isSecondaryButtonDown()) {
            if (selectedBlock != null)
            {
                menuBlock.hide();
                if (selectedBlock.isLiquid()) menuItemWater.setSelected(true);
                else menuItemStone.setSelected(true);
                menuMaterial.show(canvas, event.getScreenX(), event.getScreenY());
            }
            else
            {
                menuMaterial.hide();
                menuBlock.show(canvas, event.getScreenX(), event.getScreenY());
            }
        }
        else {
            menuBlock.hide();
            menuMaterial.hide();
            if (selectedBlock != null)
            {
                mouseDownOffsetWithinBlockX = (mouseDownX - offsetX)/zoomFactor - selectedBlock.getX();
                mouseDownOffsetWithinBlockY = (mouseDownY - offsetY)/zoomFactor - selectedBlock.getY();
            }
        }
    }



    private void menuEvent(ActionEvent e) {
        //System.out.println("menu event "+e.getSource());
        //float x = Math.round((lastMouseX-offsetX)/10)*10;
        //float y = Math.round((lastMouseY-offsetY)/10)*10;
        float x = Math.round(((mouseDownX/zoomFactor)-offsetX)/10)*10;
        float y = Math.round(((mouseDownY/zoomFactor)-offsetY)/10)*10;
        MenuItem item = (MenuItem)e.getSource();

        if (item == menuItemStone)
        {
            if (selectedBlock != null) selectedBlock.setLiquid(false);
        }
        else if (item == menuItemWater)
        {
            if (selectedBlock != null) selectedBlock.setLiquid(true);
        }
        else if (item == menuItemDelete)
        {
            if (selectedBlock != null) blockList.remove(selectedBlock);
            selectedBlock = null;
            selectedVertexIdx = -1;
        }
        else
        {
            String text = item.getText();
            for (Entity.ShapeEnum shape : Entity.ShapeEnum.values())
            {
                if (text.endsWith(shape.getText()))
                {
                    Block block = new Block(x, y, 100, 100, shape, null);
                    blockList.add(block);
                    break;
                }
            }
        }
        renderAll();
    }

    private void renderAll() {
        int width  = (int)(canvas.getWidth()/zoomFactor);
        int height = (int)(canvas.getHeight()/zoomFactor);

        /*
        int xStart = offsetX % 50;
        if (offsetX < 0) xStart = 50 + xStart;
        for (int x = xStart; x < width; x += 50)
        {
            for (int y = 0; y < height; y += 10)
            {
                pixelWriter.setColor(x, y, Color.BLACK);
            }
        }
        xStart = offsetX % 10;
        if (offsetX < 0) xStart = 10 + xStart;
        for (int y = 0; y < height; y += 50)
        {
            for (int x = xStart; x < width; x += 10)
            {
                pixelWriter.setColor(x, y, Color.BLACK);
            }
        }


        gtx.drawImage(imageBaseLayer, 0, 0);
        */

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

        for (Block block : blockList) {
            render(block);
        }
    }
    private void render(Block block) {
        //System.out.println("    render() "+block.getShape());
        gtx.setFill(block.getColor());

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
            gtx.fillRect(
                    x, y, block.getWidth(), block.getHeight());

        }
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

        if (blockList.isEmpty()) return;
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        for (Block block : blockList) {
            if (block.getLeftEdge() < minX) minX = block.getLeftEdge();
            if (block.getTopEdge()  < minY) minY = block.getTopEdge();
            if (block.getRightEdge()  > maxX) maxX = block.getRightEdge();
            if (block.getBottomEdge() > maxY) maxY = block.getBottomEdge();
        }
        float centerX = (minX + maxX)/2;
        float centerY = (minY + maxY)/2;
        float scale = 1.0f/50.0f;

        try
        {
            writer.write("Center,"+centerX+","+centerY+",  Scale,"+scale+"\n");
            for (Block block : blockList)
            {
                //float x = (block.getX() - centerX) * scale;
                //float y = (block.getY() - centerY) * scale;
                //float width = block.getWidth() * scale;
                writer.write(block.getShape() + ", "+block.getX()+","+block.getY()+","+
                        block.getWidth()+","+ block.getHeight()+","+block.isLiquid()+"\n");

                //Block block = new Block(x, y, 100, 100, shape, null);
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
        BufferedReader reader = fileChooserRead();
        if (reader == null) return;

        blockList.clear();
        offsetX=0;
        offsetY=0;
        try
        {
            reader.readLine();  //do not need center or scale.
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split(",");

                if (data.length != 6) {
                    System.out.println("Error Reading Line: ["+line+"]");
                    throw new IOException("each record must have 6 fields");
                }
                Entity.ShapeEnum shape = Entity.ShapeEnum.valueOf(data[0]);
                float x = Float.valueOf(data[1]);
                float y = Float.valueOf(data[2]);
                float width = Float.valueOf(data[3]);
                float height = Float.valueOf(data[4]);
                boolean isLiquid = Boolean.valueOf(data[5]);

                Block block = new Block(x, y, width, height, shape, null);
                block.setLiquid(isLiquid);
                blockList.add(block);

                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        renderAll();
    }



    //============================================================================================
    //============================================================================================
    public static ArrayList<Entity> loadLevel(String path)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            ArrayList<Entity> entityList = new ArrayList<>();

            String line = reader.readLine();
            String[] data = line.split(",");
            float centerX = Float.valueOf(data[1]);
            float centerY = Float.valueOf(data[2]);
            float scale = Float.valueOf(data[4]);

            line = reader.readLine();
            while (line != null) {
                data = line.split(",");

                if (data.length != 6) {
                    System.out.println("Error Reading Line: ["+line+"]");
                    throw new IOException("each record must have 6 fields");
                }
                Entity.ShapeEnum shape = Entity.ShapeEnum.valueOf(data[0]);
                float x = (Float.valueOf(data[1]) - centerX)*scale;
                float y = (Float.valueOf(data[2]) - centerY)*scale;
                float width = Float.valueOf(data[3])*scale;
                float height = Float.valueOf(data[4])*scale;
                boolean isLiquid = Boolean.valueOf(data[5]);

                Block block = new Block(x, y, width, height, shape, null);
                block.setLiquid(isLiquid);
                entityList.add(block);

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
    private static BufferedReader fileChooserRead()
    {
        System.out.println("LevelBuilder.fileChooserRead()");
        BufferedReader reader = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Hermano Level File");
        fileChooser.setInitialDirectory(new File("Resources/Levels"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null)
        {
            try
            {
                reader = new BufferedReader(new FileReader(selectedFile));
            }
            catch (IOException e)
            {
                /*Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Error");
                alert.setHeaderText("IO Exception:");
                alert.setContentText(e.getMessage());
                alert.showAndWait();*/
                return null;
            }
        }
        return reader;
    }

}

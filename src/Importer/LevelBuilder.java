package Importer;
import Gameplay.Entity;
import Gameplay.Block;
import Gameplay.Actor;
import Util.Vec2;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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


public class LevelBuilder  extends Application {

    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gtx;
    //private WritableImage imageBaseLayer;
    //private PixelWriter pixelWriter;
    private ContextMenu menuBlock, menuMaterial;
    private ArrayList<Block> blockList = new ArrayList<>();
    private float lastMouseX, lastMouseY;
    private Block selectedBlock = null;
    private int selectedVertexIdx = -1;
    private boolean windowWasResized = false;
    private int offsetX=0;
    private int offsetY=0;

    private MenuItem itemPlayer1, itemPlayer2;
    private Actor player1, player2;


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

        itemPlayer1 = new MenuItem("Add Player 1");
        itemPlayer2 = new MenuItem("Add Player 2");
        menuBlock.getItems().add(itemPlayer1);
        menuBlock.getItems().add(itemPlayer2);
        itemPlayer1.setOnAction(this::menuEvent);
        itemPlayer2.setOnAction(this::menuEvent);
        menuBlock.getItems().add(new SeparatorMenuItem());
        itemPlayer1.setDisable(true);
        itemPlayer2.setDisable(true);

        for (Entity.ShapeEnum shape : Entity.ShapeEnum.values()) {
            MenuItem item = new MenuItem("Add " + shape.getText());
            menuBlock.getItems().add(item);
            item.setOnAction(this::menuEvent);
        }

        CheckMenuItem menuItemStone = new CheckMenuItem("Stone");
        CheckMenuItem menuItemWater = new CheckMenuItem("Water");
        menuMaterial.getItems().add(menuItemStone);
        menuMaterial.getItems().add(menuItemWater);
        menuItemStone.setOnAction(this::menuEvent);
        menuItemWater.setOnAction(this::menuEvent);

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

        gtx.clearRect(80, 180, 600, 230);
        gtx.fillText("Right-click on canvas to add Block.\n" +
                        "Right-click on Block to make liquid (default is solid).\n\n" +

                        "Left-click-drag on Block to move.\n" +
                        "Left-click-drag on Block vertex resize.\n" +
                        "Left-click-drag on canvas to extend canvas.\n\n"+

                        "Press S to Save level.\n" +
                        "Press L to Load level.",
                100, 200);

        scene.setOnMousePressed(this::mousePressed);
        scene.setOnMouseMoved(this::mouseMoved);
        scene.setOnMouseDragged(this::mouseDragged);

        scene.widthProperty().addListener(this::windowResize);
        scene.heightProperty().addListener(this::windowResize);

        scene.setOnKeyPressed(this::keyPressed);
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
        float x = mouseX - offsetX;
        float y = mouseY - offsetY;

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

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }


    private void mouseDragged(MouseEvent event) {
        float mouseX = (float)event.getX();
        float mouseY = (float)event.getY();

        if (selectedVertexIdx >= 0)
        {
            float x0 = selectedBlock.getX();
            float y0 = selectedBlock.getY();
            float px = selectedBlock.getVertexX(selectedVertexIdx);
            float py = selectedBlock.getVertexY(selectedVertexIdx);
            float dx = ((mouseX-offsetX) - x0) - (px - x0);
            float dy = ((mouseY-offsetY) - y0) - (py - y0);
            dx = Math.round(dx/20)*20;
            dy = Math.round(dy/20)*20;
            selectedBlock.setPosition(x0+dx/2, y0+dy/2);
            float width  = Math.max(20,selectedBlock.getWidth()  + dx*Math.signum(px - x0));
            float height = Math.max(20,selectedBlock.getHeight() + dy*Math.signum(py - y0));
            selectedBlock.setSize(width, height);
        }
        else if (selectedBlock != null)
        {
            float x = Math.round((mouseX-offsetX)/10)*10;
            float y = Math.round((mouseY-offsetY)/10)*10;
            selectedBlock.setPosition(x, y);
        }
        else
        {
            offsetX += mouseX - lastMouseX;
            offsetY += mouseY - lastMouseY;
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        renderAll();
    }

    private void mousePressed(MouseEvent event) {
        lastMouseX = (float)event.getX();
        lastMouseY = (float)event.getY();

        renderAll();

        if (event.isSecondaryButtonDown()) {
            if (selectedBlock != null)
            {
                menuMaterial.show(canvas, event.getScreenX(), event.getScreenY());
            }
            else
            {
                menuBlock.show(canvas, event.getScreenX(), event.getScreenY());
            }
        }
        else {
            menuBlock.hide();
            menuMaterial.hide();
        }
    }



    private void menuEvent(ActionEvent e) {
        //System.out.println("menu event "+e.getSource());
        float x = Math.round((lastMouseX-offsetX)/10)*10;
        float y = Math.round((lastMouseY-offsetY)/10)*10;
        MenuItem item = (MenuItem)e.getSource();
        if (item == itemPlayer1)
        {
            //player1 = new Actor(x, y, 100, 100, shape, new String[]{});

            itemPlayer1.setDisable(true);
            itemPlayer2.setDisable(false);
            return;
        }

        if (item == itemPlayer2)
        {
            //player1 = new Actor(x, y, 100, 100, shape, new String[]{});
            itemPlayer2.setDisable(true);
            return;
        }

        String text = item.getText();
        for (Entity.ShapeEnum shape : Entity.ShapeEnum.values()) {
            if (text.endsWith(shape.getText())) {
                Block block = new Block(x, y, 100, 100, shape, null);
                blockList.add(block);

                //System.out.println("New block at " + mouseX +", " + mouseY);
                //System.out.println("    center " + block.getX() +", " + block.getY());
                //System.out.println("    vertex 0 " + block.getVertexX(0)+", "+block.getVertexY(0));
                break;
            }
        }
        renderAll();
    }

    private void renderAll() {
        int width  = (int)canvas.getWidth();
        int height = (int)canvas.getHeight();

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
        try
        {
            String line = reader.readLine();  //do not need center or scale.
            line = reader.readLine();
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
            ArrayList<Entity> entityList = new ArrayList();

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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Error");
                alert.setHeaderText("IO Exception:");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Error");
                alert.setHeaderText("IO Exception:");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                return null;
            }
        }
        return reader;
    }

}

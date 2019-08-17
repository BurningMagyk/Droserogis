package Importer;
import Gameplay.Entity;
import Gameplay.Block;
import Util.Vec2;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.util.ArrayList;


public class LevelBuilder  extends Application {

    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gtx;
    //private WritableImage imageBaseLayer;
    //private PixelWriter pixelWriter;
    private ContextMenu contextMenu;
    private ArrayList<Block> blockList = new ArrayList<>();
    private float lastMouseX, lastMouseY;
    private Block selectedBlock = null;
    private int selectedVertexIdx = -1;
    private boolean windowWasResized = false;
    private int offsetX=0;
    private int offsetY=0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hermano Level Builder");


        canvas = new Canvas(100, 60);
        gtx = canvas.getGraphicsContext2D();

        contextMenu = new ContextMenu();

        for (Entity.ShapeEnum shape : Entity.ShapeEnum.values()) {
            MenuItem item = new MenuItem(shape.getText());
            contextMenu.getItems().add(item);
            item.setOnAction(this::menuEvent);

        }

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

        gtx.clearRect(80, 180, 1000, 230);
        gtx.fillText("Right-click on canvas to add Block.\n" +
                        "Right-click on Block to make liquid (default is solid) or to apply texture (not yet implemented).\n\n" +

                        "Left-click-drag on Block to move.\n" +
                        "Left-click-drag on Block vertex resize.\n" +
                        "Left-click-drag on canvas to extend canvas.\n\n"+

                        "Ctrl-S to save (not yet implemented).\n" +
                        "Ctrl-L to Load (not yet implemented).",
                100, 200);

        scene.setOnMousePressed(this::mousePressed);
        scene.setOnMouseMoved(this::mouseMoved);
        scene.setOnMouseDragged(this::mouseDragged);

        scene.widthProperty().addListener(this::windowResize);
        scene.heightProperty().addListener(this::windowResize);
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
            contextMenu.show(canvas, event.getScreenX(), event.getScreenY());
        }
        else {
            contextMenu.hide();
        }
    }



    private void menuEvent(ActionEvent e) {
        //System.out.println("menu event "+e.getSource());
        MenuItem item = (MenuItem)e.getSource();
        String text = item.getText();
        for (Entity.ShapeEnum shape : Entity.ShapeEnum.values()) {
            if (text.equals(shape.getText())) {
                float x = Math.round((lastMouseX-offsetX)/10)*10;
                float y = Math.round((lastMouseY-offsetY)/10)*10;
                Block block = new Block(x, y, 100, 100, shape, new String[]{});
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
        //if (offsetX < 0) xStart = 50 + xStart;
        for (int x = xStart; x < width; x += 50)
        {
            for (int y = yStart; y < height; y += 10)
            {
                gtx.strokeLine(x,y,x,y+1);
            }
        }
        xStart = offsetX % 10;
        yStart = offsetY % 50;
        //if (offsetX < 0) xStart = 50 + xStart;
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

}

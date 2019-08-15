package Importer;
import Gameplay.Entity;
import Gameplay.Block;
import Util.Vec2;

import javafx.application.Application;
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
    private WritableImage imageBaseLayer;
    private ContextMenu contextMenu;
    private ArrayList<Block> blockList = new ArrayList<>();
    private float mouseX, mouseY;
    private Block selectedBlock = null;
    private int selectedVertexIdx = -1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hermano Level Builder");


        canvas = new Canvas(1000, 600);
        gtx = canvas.getGraphicsContext2D();

        imageBaseLayer = new WritableImage(1000, 600);
        PixelWriter pixelWriter = imageBaseLayer.getPixelWriter();

        for (int x=0; x<canvas.getWidth(); x+=50) {
            for (int y=0; y<canvas.getHeight(); y+=10) {
                pixelWriter.setColor(x,y, Color.BLACK);
            }
        }
        for (int y=0; y<canvas.getHeight(); y+=50) {
            for (int x=0; x<canvas.getWidth(); x+=10) {
                pixelWriter.setColor(x,y, Color.BLACK);
            }
        }


        contextMenu = new ContextMenu();

        for (Entity.ShapeEnum shape : Entity.ShapeEnum.values()) {
            MenuItem item = new MenuItem(shape.getText());
            contextMenu.getItems().add(item);
            item.setOnAction(this::menuEvent);

        }

        gtx.setFont(new Font("Verdana", 16));
        gtx.strokeText("Right-click on canvas to add Block.\n" +
                "Right-click on Block to make liquid (default is solid) or to apply texture (not yet implemented).\n\n" +

                "Left-click-drag on Block to move.\n" +
                "Left-click-drag on Block vertex resize.\n" +
                "Left-click-drag on canvas to extend canvas (not yet implemented).\n\n"+

                "Ctrl-S to save (not yet implemented).\n" +
                "Ctrl-L to Load (not yet implemented).",
                100, 200);
        canvas.setOnMousePressed(this::mousePressed);
        canvas.setOnMouseMoved(this::mouseMoved);
        canvas.setOnMouseDragged(this::mouseDragged);

        Pane root = new Pane();
        //root.setStyle("-fx-background-color: #FFF8DC");
        root.setStyle("-fx-background-color: #18cbd6");
        root.getChildren().add(canvas);
        scene = new Scene(root);
        scene.setCursor(Cursor.CROSSHAIR);
        stage.setScene(scene);
        stage.show();
        renderAll();
    }


    private void mouseMoved(MouseEvent event)
    {
        mouseX = (float) event.getX();
        mouseY = (float) event.getY();

        for (Block block : blockList)
        {
            int vertexIdx = block.getVertexNear(mouseX, mouseY);
            if (vertexIdx >= 0)
            {
                scene.setCursor(Cursor.NE_RESIZE);
                selectedBlock = block;
                selectedVertexIdx = vertexIdx;
                return;
            }

            if (block.isInside(mouseX, mouseY)) {
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
        mouseX = (float)event.getX();
        mouseY = (float)event.getY();

        if (selectedVertexIdx >= 0)
        {
            float x0 = selectedBlock.getX();
            float y0 = selectedBlock.getY();
            float px = selectedBlock.getVertexX(selectedVertexIdx);
            float py = selectedBlock.getVertexY(selectedVertexIdx);
            float dx = (mouseX - x0) - (px - x0);
            float dy = (mouseY - y0) - (py - y0);
            dx = Math.round(dx/10)*10;
            dy = Math.round(dy/10)*10;
            selectedBlock.setPosition(x0+dx/2, y0+dy/2);
            float width  = Math.abs(selectedBlock.getWidth()  + dx*Math.signum(px - x0));
            float height = Math.abs(selectedBlock.getHeight() + dy*Math.signum(py - y0));
            selectedBlock.setSize(width, height);
        }
        else if (selectedBlock != null)
        {
            float x = Math.round(mouseX/10)*10;
            float y = Math.round(mouseY/10)*10;
            selectedBlock.setPosition(x, y);
        }
        else return;
        renderAll();
    }

    private void mousePressed(MouseEvent event) {
        mouseX = (float)event.getX();
        mouseY = (float)event.getY();

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
                float x = Math.round(mouseX/10)*10;
                float y = Math.round(mouseY/10)*10;
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
        gtx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gtx.drawImage(imageBaseLayer, 0, 0);
        gtx.drawImage(imageBaseLayer, 0, 0);

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
                xPos[i] = block.getVertexX(i);
                yPos[i] = block.getVertexY(i);
            }
            gtx.fillPolygon(xPos, yPos, 3);
        }
        else if (block.getShape() == Entity.ShapeEnum.RECTANGLE)
        {

            Vec2 pos = block.getPosition();
            gtx.fillRect(
                    pos.x - block.getWidth() / 2, pos.y - block.getHeight() / 2, block.getWidth(), block.getHeight());

        }
    }

}

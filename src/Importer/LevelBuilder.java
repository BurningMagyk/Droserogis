package Importer;
import Gameplay.Entity;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
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

public class LevelBuilder  extends Application {

    private Canvas canvas;
    private GraphicsContext gtx;
    private WritableImage imageBaseLayer;
    private ContextMenu contextMenu;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hermano Level Builder");


        canvas = new Canvas(1000, 600);
        gtx = canvas.getGraphicsContext2D();


        PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

        for (int x=0; x<canvas.getWidth(); x+=50) {
            for (int y=0; y<canvas.getHeight(); y+=5) {
                pixelWriter.setColor(x,y, Color.GREY);
            }
        }
        for (int y=0; y<canvas.getHeight(); y+=50) {
            for (int x=0; x<canvas.getWidth(); x+=5) {
                pixelWriter.setColor(x,y, Color.GREY);
            }
        }
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        imageBaseLayer = canvas.snapshot(params, null);

        contextMenu = new ContextMenu();

        for (Entity.ShapeEnum shape : Entity.ShapeEnum.values()) {
            MenuItem item = new MenuItem(shape.getText());
            contextMenu.getItems().add(item);
        }

        //public Block(float xPos, float yPos, float width, float height, shape)

        gtx.setFont(new Font("Verdana", 16));
        gtx.strokeText("Right-click on canvas to add Block (currently just menu shows).\n" +
                "Right-click on Block to apply texture (not implemented yet).\n" +
                "Right-click on edge of Block to resize (not implemented yet).\n\n" +
                "Left-click-drag on Block to move (not implemented yet).\n" +
                "Left-click-drag on canvas to extend canvas (not implemented yet).\n\n"+
                "Ctrl-S to save (not implemented yet).\n" +
                "Ctrl-L to Load (not implemented yet).",
                200, 200);
        canvas.setOnMousePressed(this::canvasMousePressed);

        Pane root = new Pane();
        // Set the Style-properties of the Pane
        //root.setStyle("-fx-padding: 10;" +
        //        "-fx-border-style: solid inside;" +
        //       "-fx-border-width: 2;" +
        //       "-fx-border-insets: 5;" +
        //        "-fx-border-radius: 5;" +
        //       "-fx-border-color: blue;");
        root.setStyle("-fx-background-color: #FFF8DC");
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


        //Button btn = new Button();
        //btn.setText("Say 'Hello World'");
        //btn.setOnAction(new EventHandler<ActionEvent>() {
//
        //    @Override
        //    public void handle(ActionEvent event) {
        //        System.out.println("Hello World!");
        //    }
        //});

        //StackPane root = new StackPane();
        //root.getChildren().add(btn);
        //primaryStage.setScene(new Scene(root, 1000, 600));
        //primaryStage.show();
    }

    public void canvasMousePressed(MouseEvent event) {
        gtx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.getGraphicsContext2D().drawImage(imageBaseLayer, 0, 0);

        if (event.isSecondaryButtonDown()) {
            contextMenu.show(canvas, event.getScreenX(), event.getScreenY());
        }
        else {
            contextMenu.hide();
        }
    }
}

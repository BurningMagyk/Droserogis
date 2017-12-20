package Menus;

import Util.LanguageEnum;
import Util.Print;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Toolkit;
import java.io.InputStream;
import java.util.Locale;

public class Main extends Application
{
    private static LanguageEnum language = getSystemLanguage();

    private final int[] SCREEN_DIMS = {
            Toolkit.getDefaultToolkit().getScreenSize().width,
            Toolkit.getDefaultToolkit().getScreenSize().height };

    @Override
    public void start(Stage stage)
    {
        final String VERSION = "indev";
        final String NAME = "Droserogis vs Sothli";

        /* Prepare the basics: stage, scene, group, and canvas */
        Group root = new Group();
        final int SCENE_WIDTH = SCREEN_DIMS[0] / 2;
        final int SCENE_HEIGHT = SCREEN_DIMS[1] / 2;
        final Scene SCENE = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT,
                Color.BLACK);
        final Canvas canvas = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();

        /* Sample code to draw on the canvas: */
        drawOnCanvas(context, SCENE_WIDTH, SCENE_HEIGHT);
        //context.setFill(Color.BLUE);
        //context.fillRect(300,250,100,100);

        /* Add the canvas and widgets in order */
        root.getChildren().add(canvas);
        setWidgets(stage, root, SCENE_WIDTH, SCENE_HEIGHT);
        root.getStylesheets().add("CSS/opening.css");

        /* Set the stage at the center of the screen */
        stage.setX(SCREEN_DIMS[0] / 4);
        stage.setY(SCREEN_DIMS[1] / 4);

        /* Set scene, set stage style, and make stage visible */
        stage.setTitle(NAME + " - " + VERSION);
        /* TODO: Put this title on the main game window. */
        stage.setScene(SCENE);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    /**
     * Instantiates widgets and modifies them according to the scene's
     * dimensions. Then adds those widgets to the scene.
     * @param width - width of the scene
     * @param height - height of the scene
     */
    private void setWidgets(Stage stage, Group group,
                            int width, int height)
    {
        /* How much space will go between the widgets and borders */
        final int STUFFING = Math.min(width, height) / 20;

        /* Need to get comboBox dims out of the way */
        int comboBoxWidth = width * 3 / 15;
        int comboBoxHeight = height / 10;

        /* Give widget names */
        Button startButton = new Button("Start Game");
        Button exitButton = new Button("Exit");

        ComboBox<LanguageEnum> langComboBox = new ComboBox<>();
        langComboBox.setValue(language);
        langComboBox.setButtonCell(
                new StringImageCell(comboBoxWidth, comboBoxHeight));

        /* Add every language to the langComboBox */
        for (LanguageEnum lang : LanguageEnum.values())
        {
            langComboBox.getItems().add(lang);
        }

        /* Set cellFactory property and buttonCell property */
        langComboBox.setCellFactory(kys ->
                new StringImageCell(comboBoxWidth, comboBoxHeight));

        /* Set widget sizes */
        startButton.setPrefWidth(width * 3 / 15);
        startButton.setPrefHeight(height / 10);
        exitButton.setPrefWidth(width * 2 / 15);
        exitButton.setPrefHeight(height / 10);

        langComboBox.setPrefWidth(comboBoxWidth);
        langComboBox.setPrefHeight(comboBoxHeight);

        /* Calculate boundary values */
        int boundsX[] = new int[3];
        boundsX[0] = width - (int) exitButton.getPrefWidth() - STUFFING;
        boundsX[1] = width - (int) langComboBox.getPrefWidth() - STUFFING;
        boundsX[2] = boundsX[0] - (int) startButton.getPrefWidth() - STUFFING;
        int boundsY[] = new int[1];
        boundsY[0] = height - (int) startButton.getPrefHeight() - STUFFING;

        /* Set widget locations */
        exitButton.setTranslateX(boundsX[0]);
        startButton.setTranslateX(boundsX[2]);
        exitButton.setTranslateY(boundsY[0]);
        startButton.setTranslateY(boundsY[0]);

        langComboBox.setTranslateX(boundsX[1]);
        langComboBox.setTranslateY(STUFFING);

        /* Set widget actions */
        startButton.setOnAction(event -> startGame(stage, group));
        exitButton.setOnAction(event -> quitGame(stage, group));

        /* Add widgets to the ArrayList */
        group.getChildren().add(startButton);
        group.getChildren().add(exitButton);
        group.getChildren().add(langComboBox);
    }

    private void drawOnCanvas(GraphicsContext context, int width, int height)
    {
        /* How much space will go between the widgets and borders */
        final int STUFFING = Math.min(width, height) / 20;

        InputStream input;
        Image image;
        Font font;

        /* Try importing image file */
        input = getClass()
                .getResourceAsStream("/Images/opening_background.png");
        if (input == null)
        {
            Print.red("\"opening_background.png\" was not imported");
        }
        else
        {
            /* This centers the window onto the image */
            image = new Image(input);
            context.drawImage(image,
                    0,
                    (height - image.getHeight()) / 2,
                    width,
                    image.getHeight());
        }

        /* Try importing the Scurlock font file */
        input = getClass()
                .getResourceAsStream("/Fonts/scurlock.ttf");
        int fontSize = Math.min(width, height) / 7;
        font = Font.loadFont(input, fontSize);
        context.setFont(font);
        int boundary = height - fontSize / 10 - STUFFING;
        context.setFill(Color.DARKBLUE);
        context.fillText("Droserogis",
                STUFFING, boundary);

        /* Try importing the Supernatural Knight font file */
        input = getClass()
                .getResourceAsStream("/Fonts/supernatural_knight.ttf");
        font = Font.loadFont(input, fontSize / 2);
        context.setFont(font);
        boundary = boundary - (int) (fontSize / 1.5) - STUFFING / 2;
        context.setFill(Color.BLACK);
        context.fillText("VS",
                fontSize * 1.7 + STUFFING, boundary);

        /* Try importing the Cardinal font file */
        input = getClass()
                .getResourceAsStream("/Fonts/cardinal.ttf");
        font = Font.loadFont(input, fontSize / 1.2);
        context.setFont(font);
        boundary = boundary - fontSize / 2 - STUFFING / 2;
        context.setFill(Color.PURPLE);
        context.fillText("Sothli",
                fontSize * 1.25 + STUFFING, boundary);

    }

    /* A custom ListCell that displays an image and string */
    private class StringImageCell extends ListCell<LanguageEnum>
    {
        int width, height;
        StringImageCell(int width, int height)
        {
            this.width = width;
            this.height = height;
        }

        private Label label;
        @Override
        protected void updateItem(LanguageEnum item, boolean empty)
        {
            super.updateItem(item, empty);
            if (item == null || empty)
            {
                setItem(null);
                setGraphic(null);
            }
            else
            {
                setText(item.toString());
                ImageView imageView = new ImageView(item.getFlag());
                label = new Label("", imageView);
                setGraphic(label);
                setPrefSize(width, height);
            }
        }
    }

    private static LanguageEnum getSystemLanguage()
    {
        switch (Locale.getDefault().getLanguage())
        {
            case ("en") : { return LanguageEnum.ENGLISH; }
            case ("es") : { return LanguageEnum.SPANISH; }
            case ("it") : { return LanguageEnum.ITALIAN; }
            case ("fr") : { return LanguageEnum.FRENCH; }
            case ("de") : { return LanguageEnum.GERMAN; }
            case ("ja") : { return LanguageEnum.WAPANESE; }
            default : { return LanguageEnum.ENGLISH; }
        }
    }

    /* Starts the game */
    private void startGame(Stage stage, Group root)
    {
        quitGame(stage, root); // temp
    }

    /* Quits the game */
    private void quitGame(Stage stage, Group root)
    {
        root.getChildren().clear();
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args)
    {
        language = getSystemLanguage();
        Application.launch(args);
    }
}

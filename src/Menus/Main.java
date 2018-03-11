package Menus;

import Importer.Importer;
import Importer.ImageResource;
import Importer.FontResource;
import Util.LanguageEnum;
import Util.Print;
import Util.Translator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
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
    public static LanguageEnum language = getSystemLanguage();
    public final static Importer IMPORTER = new Importer();

    private final int SCREEN_WIDTH =
            Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int SCREEN_HEIGHT =
            Toolkit.getDefaultToolkit().getScreenSize().height;

    private Controller MAINGAME;

    @Override
    public void start(Stage stage)
    {
        final String VERSION = "indev";
        final String NAME = "Droserogis vs Sothli";
        stage.setTitle(NAME + " - " + VERSION);

        /* Prepare the basics: stage, scene, group, and canvas */
        Group root = new Group();
        final int SCENE_WIDTH = SCREEN_WIDTH / 2;
        final int SCENE_HEIGHT = SCREEN_HEIGHT / 2;
        final Scene SCENE = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT,
                Color.GREY);
        final Canvas CANVAS = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        GraphicsContext context = CANVAS.getGraphicsContext2D();
        IMPORTER.setContext(context);

        /* Sample code to draw on the canvas: */
        drawOnCanvas(context);

        /* Add the canvas and widgets in order */
        root.getChildren().add(CANVAS);
        setWidgets(stage, root, SCENE_WIDTH, SCENE_HEIGHT);
        root.getStylesheets().add("CSS/opening.css");

        /* Set the stage at the center of the screen */
        stage.setX(SCREEN_WIDTH / 4);
        stage.setY(SCREEN_HEIGHT / 4);

        /* Set scene, set stage style, and make stage visible */
        stage.setScene(SCENE);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        /* Set up main game after the prompt is already set up */
        Stage mainGameStage = new Stage(StageStyle.UNDECORATED);
        mainGameStage.setWidth(SCREEN_WIDTH);
        mainGameStage.setHeight(SCREEN_HEIGHT);
        MAINGAME = new Controller(mainGameStage);
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

        /* Try importing the Supernatural Knight and Kaisho fonts */
        double fontSize = Math.min(width, height) / 25;
        FontResource font = IMPORTER.getFont(
                "/Fonts/planewalker.otf", fontSize,
                "/Fonts/kaisho.ttf", fontSize);

        /* Give widget names, add them to the translator */
        final Translator translator = new Translator();
        String[] startButtonNames =
                {"Start Game", "Empieza Juego", "Inizia Gioco",
                        "Démarrer Jeu", "Spiel Beginnen", "ゲームをスタート"};
        Button startButton = translator.getButton(font.getFonts(), startButtonNames);
        String[] exitButtonNames =
                {"Exit", "Salga", "Uscire", "Quitter", "Beende", "出口"};
        Button exitButton = translator.getButton(font.getFonts(), exitButtonNames);

        /* Set IDs for each button */
        startButton.setId("start-button");
        exitButton.setId("exit-button");

        /* Group the buttons to ease input */
        Button[] buttons = {startButton, exitButton};

        /* Set up Combo Boxes */
        ComboBox<LanguageEnum> langComboBox = new ComboBox<>();
        langComboBox.setValue(language);

        double comboFontSize = Math.min(comboBoxWidth, comboBoxHeight) / 2.5;
        FontResource comboFont = IMPORTER.getFont("/Fonts/augusta.ttf",
                comboFontSize, "/Fonts/kaisho.ttf", comboFontSize);

        langComboBox.setButtonCell(
                new StringImageCell(comboBoxWidth, comboBoxHeight, comboFont));

        /* Add every language to the langComboBox */
        for (LanguageEnum lang : LanguageEnum.values())
        {
            langComboBox.getItems().add(lang);
        }

        /* Set cellFactory property and buttonCell property */
        langComboBox.setCellFactory(kys ->
                new StringImageCell(comboBoxWidth, comboBoxHeight, comboFont));
        langComboBox.setOnAction(kys ->
        {
            updateLanguage(translator, langComboBox.getValue());
            positionButtons(buttons, width, height);
        });

        /* Set the buttons' positions,
         * call every time the language is changed */
        positionButtons(buttons, width, height);

        langComboBox.setPrefWidth(comboBoxWidth);
        langComboBox.setPrefHeight(comboBoxHeight);

        langComboBox.setTranslateX(width
                - (int) langComboBox.getPrefWidth() - STUFFING);
        langComboBox.setTranslateY(STUFFING);

        /* Set widget actions */
        startButton.setOnAction(event -> startGame(stage, group));
        exitButton.setOnAction(event -> quitGame(stage, group));

        /* Add widgets to the ArrayList */
        group.getChildren().add(startButton);
        group.getChildren().add(exitButton);
        group.getChildren().add(langComboBox);
    }

    private void drawOnCanvas(GraphicsContext context)
    {
        /* Get canvas width and height */
        int width = (int) context.getCanvas().getWidth();
        int height = (int) context.getCanvas().getHeight();

        /* How much space will go between the widgets and borders */
        final int STUFFING = Math.min(width, height) / 20;

        /* Try importing image file */
        ImageResource image = IMPORTER.getImage(
                "/Images/opening_background.png", Color.GREY);
        double sizeScale = width / image.getWidth();
        image.draw((width - image.getWidth() * sizeScale) / 2,
                (height - image.getHeight() * sizeScale) / 2,
                image.getWidth() * sizeScale,
                image.getHeight() * sizeScale);

        /* Draw the logo */
        int boundary[] = new int[3];
        int fontSize = Math.min(width, height) / 7;
        boundary[0] = height - fontSize / 10 - STUFFING;
        boundary[1] = boundary[0] - (int) (fontSize / 1.5) - STUFFING / 2;
        boundary[2] = boundary[1] - (int) (fontSize / 2.5) - STUFFING / 2;
        drawLogo(context, fontSize, boundary, STUFFING);
    }

    private void updateLanguage(Translator translator, LanguageEnum language)
    {
        Main.language = language;
        translator.translate();
    }

    private void positionButtons(Button[] buttons,
                                 final int width, final int height)
    {
        final int STUFFING = Math.min(width, height) / 20;

        Button startButton = buttons[0];
        Button exitButton = buttons[1];

        /* Calculate boundary values */
        int boundsX[] = new int[2];
        boundsX[0] = width - (int) exitButton.getPrefWidth() - STUFFING;
        boundsX[1] = boundsX[0] - (int) startButton.getPrefWidth() - STUFFING;
        int boundsY[] = new int[1];
        boundsY[0] = height - (int) (startButton.getPrefHeight() * 1.3) - STUFFING;

        /* Set widget locations */
        exitButton.setTranslateX(boundsX[0]);
        startButton.setTranslateX(boundsX[1]);
        exitButton.setTranslateY(boundsY[0]);
        startButton.setTranslateY(boundsY[0]);
    }

    private void drawLogo(GraphicsContext context, int fontSize,
                          int boundary[], final int STUFFING)
    {
        /* Try importing the Scurlock font file */
        FontResource font = IMPORTER.getFont("/Fonts/scurlock.ttf", fontSize);
        context.setFill(Color.DARKBLUE);
        font.draw(STUFFING, boundary[0], "Droserogis");

        /* Try importing the Supernatural Knight font file */
        font = IMPORTER.getFont("/Fonts/supernatural_knight.ttf", fontSize / 2.5);
        context.setFill(Color.BLACK);
        font.draw(fontSize * 1.75 + STUFFING, boundary[1], "VS");

        /* Try importing the Cardinal font file */
        font = IMPORTER.getFont("/Fonts/cardinal.ttf", fontSize / 1.2);
        context.setFill(Color.PURPLE);
        font.draw(fontSize * 1.25 + STUFFING, boundary[2], "Sothli");
    }

    /* A custom ListCell that displays an image and string */
    private class StringImageCell extends ListCell<LanguageEnum>
    {
        FontResource font;
        int width, height;
        StringImageCell(int width, int height, FontResource font)
        {
            this.width = width;
            this.height = height;
            this.font = font;
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
                if (item == LanguageEnum.WAPANESE) font.switchFont(true);
                else font.switchFont(false);
                setFont(font.getFont());
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
        root.getChildren().clear();
        stage.close();

        MAINGAME.start();
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
        launch(args);
    }
}

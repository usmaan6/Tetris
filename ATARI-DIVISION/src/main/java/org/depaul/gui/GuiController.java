package org.depaul.gui;

import org.depaul.logic.data.ViewData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.BlurType;  
import javafx.scene.effect.DropShadow;  
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.depaul.logic.events.EventSource;
import org.depaul.logic.events.EventType;
import org.depaul.logic.events.InputEventListener;
import org.depaul.logic.events.MoveEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class GuiController implements Initializable {

    // SPEED 1 and 2 are good for testing rotation (blocks stand still for a time), 5-10 for testing gameplay
    private int FALL_SPEED = 5;

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Text scoreValue;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane nextBrick;

    @FXML
    private GridPane brickPanel;

    @FXML
    private ToggleButton pauseButton;

    @FXML
    private ToggleButton newGameMenuButton;

    @FXML
    private ToggleButton newGame_GameOverButton;

    @FXML
    private ToggleButton resumeMenuButton;

    @FXML
    private Group gameOverNotification;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // change font file here
        Font.loadFont(getClass().getClassLoader().getResource("3x5.TTF").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

//        Key bindings for game panel
        gamePanel.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.R) {
                newGame(new ActionEvent());
                keyEvent.consume();
            }
            // CHANGED IF STATEMENTS INTO SWITCH CASE
            if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                EventType returnEventType = switch (keyEvent.getCode()) {
                    case UP -> EventType.UP; 
                    case LEFT -> EventType.LEFT; 
                    case RIGHT -> EventType.RIGHT; 
                    case DOWN -> EventType.DOWN; 
                     default -> throw new IllegalArgumentException("Unexpected value: " + keyEvent.getCode());
                    
                };
                moveOrRotate(new MoveEvent(returnEventType, EventSource.USER));
                keyEvent.consume();

            }
        });

        //  PAUSE MENU BUTTONS
        newGame_GameOverButton.setVisible(false);
        newGameMenuButton.setVisible(false);
        resumeMenuButton.setVisible(false);

//        GAME OVER panel notification
        gameOverNotification.setVisible(false);

//        PAUSE button
        pauseButton.selectedProperty().bindBidirectional(isPause);
        pauseButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                timeLine.pause();
                // pauseButton.setText("Resume");
            } else {
                timeLine.play();
                // pauseButton.setText("Pause");
            }
        });

        // RESUME BUTTON
        resumeMenuButton.selectedProperty().bindBidirectional(isPause);
        resumeMenuButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                timeLine.pause();
            } else {
                timeLine.play();
            }
        });

//        SCORE: Setting the reflection style
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
        scoreValue.setEffect(reflection);
        // scoreValue.setEffect(new Glow(1)); Try setting glow
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
//        displayMatrix is the GUI representation of the current state of the board currentGameMatrix
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }  
//        rectangles is the GUI representation of the current state of brick.
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillGradient(brick.getBrickData()[i][j]));
                rectangle.setEffect(new Glow(1));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(160+gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        generateNextBrickPanel(brick.getNextBrickData());

        timeLine = new Timeline(new KeyFrame(
            Duration.millis(5000/FALL_SPEED), 
            ae -> moveOrRotate(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }


    private LinearGradient getFillGradient(int i) {
        Stop[] stopsClear = new Stop[] { new Stop(0, Color.TRANSPARENT), new Stop(1, Color.TRANSPARENT)};
        Stop[] stopsI = new Stop[] { new Stop(0, Color.web("005f5e", 1)), new Stop(1, Color.web("00ffff", 1))};
        Stop[] stopsS = new Stop[] { new Stop(0, Color.web("005900", 1)), new Stop(1, Color.web("00ff00", 1))};
        Stop[] stopsL = new Stop[] { new Stop(0, Color.web("814000", 1)), new Stop(1, Color.web("ff7f00", 1))};
        Stop[] stopsZ = new Stop[] { new Stop(0, Color.web("530000", 1)), new Stop(1, Color.web("ff0000", 1))};
        Stop[] stopsJ = new Stop[] { new Stop(0, Color.web("000047", 1)), new Stop(1, Color.web("0044ff", 1))};
        Stop[] stopsO = new Stop[] { new Stop(0, Color.web("555500", 1)), new Stop(1, Color.web("ffff00", 1))};
        Stop[] stopsT = new Stop[] { new Stop(0, Color.web("220021", 1)), new Stop(1, Color.web("800080", 1))};

        LinearGradient returnPaint = switch (i) {
            case 0 -> new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stopsClear); //I
            case 1 -> new LinearGradient(0, 0.5, 1, 0, true, CycleMethod.NO_CYCLE, stopsI); //I
            case 2 -> new LinearGradient(0, 0.5, 1, 0, true, CycleMethod.NO_CYCLE, stopsS); //S
            case 3 -> new LinearGradient(0, 0.5, 1, 0, true, CycleMethod.NO_CYCLE, stopsL); //L
            case 4 -> new LinearGradient(0, 0.5, 1, 0, true, CycleMethod.NO_CYCLE, stopsZ); //Z
            case 5 -> new LinearGradient(0, 0.5, 1, 0, true, CycleMethod.NO_CYCLE, stopsJ); //J
            case 6 -> new LinearGradient(0, 0.5, 1, 0, true, CycleMethod.NO_CYCLE, stopsO); //O
            case 7 -> new LinearGradient(0, 0.5, 1, 0, true, CycleMethod.NO_CYCLE, stopsT); //T
   
            default -> new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stopsClear);
        };
        return returnPaint;
    }

    private void generateNextBrickPanel(int[][] nextBrickData) {
        nextBrick.getChildren().clear();
        for (int i = 0; i < nextBrickData.length; i++) {
            for (int j = 0; j < nextBrickData[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(nextBrickData[i][j] , rectangle);
                if (nextBrickData[i][j] != 0) {
                    nextBrick.add(rectangle, j, i);
                }
            }
        }
    }

    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(160+gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j] , rectangles[i][j]);
                }
            }
            generateNextBrickPanel(brick.getNextBrickData());
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j] , displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillGradient(color));
        if(color != 0){
            rectangle.setEffect(new Glow(1));
        } 
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void randomAction(){

        int action = ThreadLocalRandom.current().nextInt(5); // 0 for game over - anything else for bonus

        if (action!=0) {
            //        Bonus notification
            NotificationPanel notificationPanel = new NotificationPanel("+" + ThreadLocalRandom.current().nextInt(100));
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());

        } else {
            //        Game Over
           //gameOver();
        }
    }

    private void moveOrRotate(MoveEvent event) {
        refreshBrick(eventListener.onMoveEvent(event));
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreValue.textProperty().bind(integerProperty.asString());
    }

    public void gameOver() {
        timeLine.stop();
        newGame_GameOverButton.setVisible(true);
        newGameMenuButton.setVisible(false);
        pauseButton.setVisible(false);
        resumeMenuButton.setVisible(false);
        GameOverPanel gameOverPanel = new GameOverPanel("Game Over!");
        gameOverNotification.getChildren().add(gameOverPanel);
        gameOverNotification.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    // ADDED ADDITONAL MENU LOGIC TO newGame and pauseGame, added resumeGame action
    public void newGame(ActionEvent actionEvent) {
        pauseButton.setVisible(false);
        newGameMenuButton.setVisible(false);
        newGame_GameOverButton.setVisible(false);

        resumeMenuButton.setVisible(false);
        pauseButton.setVisible(true);

        timeLine.stop();
        gameOverNotification.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

     // ADDED ADDITONAL MENU LOGIC TO newGame and pauseGame, added resumeGame action
     public void newGame_GameOver(ActionEvent actionEvent) {
        pauseButton.setVisible(false);
        newGameMenuButton.setVisible(false);
        newGame_GameOverButton.setVisible(false);

        resumeMenuButton.setVisible(false);
        pauseButton.setVisible(true);

        timeLine.stop();
        gameOverNotification.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        if (isPause.getValue() == Boolean.TRUE){
            pauseButton.setVisible(false);
            newGameMenuButton.setVisible(true);
            resumeMenuButton.setVisible(true);
        }
        else{
             newGameMenuButton.setVisible(false);
             resumeMenuButton.setVisible(false);
             pauseButton.setVisible(false);
        }
      
        gamePanel.requestFocus();
    }

    public void resumeGame(ActionEvent actionEvent) {
        if (isPause.getValue() == Boolean.TRUE){
            pauseButton.setVisible(false);
            newGameMenuButton.setVisible(true);
            resumeMenuButton.setVisible(true);
        }
        else{
             newGameMenuButton.setVisible(false);
             resumeMenuButton.setVisible(false);
             pauseButton.setVisible(true);
        }
      
        gamePanel.requestFocus();
    }


    public DropShadow getDropShadow(){
        DropShadow drop = new DropShadow();  
        drop.setBlurType(BlurType.GAUSSIAN);  
        drop.setColor(Color.BLACK);  
        drop.setHeight(100);  
        drop.setWidth(100);  
        drop.setOffsetX(2);  
        drop.setOffsetY(3);  
        drop.setSpread(0.5);  
        drop.setRadius(5);  
        return drop;
    }
}

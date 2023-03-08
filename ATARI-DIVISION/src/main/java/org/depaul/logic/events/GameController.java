package org.depaul.logic.events;

import org.depaul.gui.GuiController;
import org.depaul.logic.board.Board;
import org.depaul.logic.board.SimpleBoard;
import org.depaul.logic.data.ViewData;

import java.util.concurrent.ThreadLocalRandom;

public class GameController implements InputEventListener {

    private final Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public ViewData onMoveEvent(MoveEvent moveEvent) {
        switch (moveEvent.getEventType()) {
            case UP:
                //(this handles rotateBrick, so brickRotator.setCurrentShapeIndex() doesn't need to be called twice)
                //if the brick couldn't be rotated
                board.rotateBrick();
                break;
            case DOWN:
                //(this handles moveBrickDown, so p.translate() doesn't need to be called twice)
                //if the brick couldn't move down
                if (!board.moveBrickDown()) {
                    // ANYTHING UNDER THIS IF STATEMENT HAPPENS IF THE BRICK HITs THE BOTTOM or falls onto another brick
                    board.mergeBrickToBackground();
                    if (board.createNewBrick()) {
                        viewGuiController.gameOver();
                    }
                    viewGuiController.refreshGameBackground(board.getBoardMatrix());
                }
                break;
            case LEFT:
                board.moveBrickLeft();
                break;
            case RIGHT:
                board.moveBrickRight();
                break;
            default:
                break;
        }

        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}

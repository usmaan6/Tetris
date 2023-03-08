package org.depaul.logic.board;

import org.depaul.logic.data.Score;
import org.depaul.logic.data.ViewData;

public interface Board {
    boolean rotateBrick();

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    Score getScore();

    void newGame();
}

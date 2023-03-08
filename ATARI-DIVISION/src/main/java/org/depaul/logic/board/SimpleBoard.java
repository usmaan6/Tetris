package org.depaul.logic.board;

import org.depaul.logic.bricks.Brick;
import org.depaul.logic.bricks.BrickGenerator;
import org.depaul.logic.bricks.RandomBrickGenerator;
import org.depaul.logic.data.Score;
import org.depaul.logic.data.ViewData;
import org.depaul.logic.rotator.BrickRotator;
import org.depaul.logic.util.Operations;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleBoard implements Board {
    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean rotateBrick() {
        //current matrix state
        int[][] currentMatrix = Operations.copyMatrix(currentGameMatrix);
        int[][] nextRotateBrickMatrix = brickRotator.getNextShapeMatrix(); //rotated 1 time

        //if we have conflict, return false meaning we can't rotate the brick
        if (Operations.intersectMatrix(currentGameMatrix, nextRotateBrickMatrix, (int) currentOffset.getX(), (int) currentOffset.getY())) {
            return false;
        }
        else {
            brickRotator.setCurrentShapeIndex(brickRotator.getNextShapeIndex());
            return true;
        }
    }

    @Override
    public boolean moveBrickDown() {
        //current location
        Point p = new Point(currentOffset);

        //random move 1 step down
        p.translate(0, 1);

        //do we have conflict with this move?
        boolean conflict = Operations.intersectMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickLeft() {
        //current location
        Point p = new Point(currentOffset);

        //random move 1 step left
        p.translate(-1, 0);

        //do we have conflict with this move?
        boolean conflict = Operations.intersectMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        //current location
        Point p = new Point(currentOffset);

        //random move 1 step right
        p.translate(1, 0);
        
        //do we have conflict with this move?
        boolean conflict = Operations.intersectMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }

    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(3, 0);
        return Operations.intersectMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShapeMatrix(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getBrickMatrixList().get(0));
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = Operations.mergeMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}

package org.depaul.logic.bricks;

import org.depaul.logic.util.Operations;

import java.util.ArrayList;
import java.util.List;

final class JBrick implements Brick {

    private final List<int[][]> brickMatrixList = new ArrayList<>();

    public JBrick() {
        brickMatrixList.add(new int[][]{
            {5, 0, 0, 0},
            {5, 5, 5, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    });
    brickMatrixList.add(new int[][]{
            {5, 5, 0, 0},
            {5, 0, 0, 0},
            {5, 0, 0, 0},
            {0, 0, 0, 0}
    });
    brickMatrixList.add(new int[][]{
            {5, 5, 5, 0},
            {0, 0, 5, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    });
    brickMatrixList.add(new int[][]{
            {0, 5, 0, 0},
            {0, 5, 0, 0},
            {5, 5, 0, 0},
            {0, 0, 0, 0}
    });
        
    }

    @Override
    public List<int[][]> getBrickMatrixList() {
        return Operations.deepCopyList(brickMatrixList);
    }
}



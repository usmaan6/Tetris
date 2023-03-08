package org.depaul.logic.bricks;

import org.depaul.logic.util.Operations;

import java.util.ArrayList;
import java.util.List;

final class ZBrick implements Brick {

    private final List<int[][]> brickMatrixList = new ArrayList<>();

    public ZBrick() {
        brickMatrixList.add(new int[][]{
                {4, 4, 0, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrixList.add(new int[][]{
                {0, 0, 4, 0},
                {0, 4, 4, 0},
                {0, 4, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrixList.add(new int[][]{
                {0, 4, 4, 0},
                {0, 0, 4, 4},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrixList.add(new int[][]{
                {0, 0, 4, 0},
                {0, 4, 4, 0},
                {0, 4, 0, 0},
                {0, 0, 0, 0}
        });
       
    }

    @Override
    public List<int[][]> getBrickMatrixList() {
        return Operations.deepCopyList(brickMatrixList);
    }
}

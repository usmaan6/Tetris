package org.depaul.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
// import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    private int[] randomArrayIndexes = null;

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new LBrick());
        brickList.add(new JBrick());
        brickList.add(new ZBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new OBrick());
        addRandomBlocks();
    }

    public void addRandomBlocks(){
        nextBricks.clear();
        HashSet<Integer> brickListSet = new HashSet<>();
        int[] randomArrayIndexesTemp = new int[brickList.size()];
        Random r = new Random();
        for(int i = 0; i<randomArrayIndexesTemp.length; i++){
            while(true){
                int randomNumber = r.nextInt(randomArrayIndexesTemp.length);
                if(!brickListSet.contains(randomNumber)){
                    randomArrayIndexesTemp[i] = randomNumber;
                    brickListSet.add(randomNumber);
                    break;
                }
            }
        }
        for(int i = 0; i<randomArrayIndexesTemp.length; i++){
            System.out.println(randomArrayIndexesTemp[i]);
            nextBricks.add(brickList.get(randomArrayIndexesTemp[i]));
        }

        if(randomArrayIndexes != null){
            if(randomArrayIndexes[randomArrayIndexesTemp.length-1] == randomArrayIndexesTemp[0]){
                int temp = randomArrayIndexesTemp[0];
                randomArrayIndexesTemp[0] = randomArrayIndexesTemp[randomArrayIndexesTemp.length-1];
                randomArrayIndexesTemp[randomArrayIndexesTemp.length-1] = temp;
            }
        }
        randomArrayIndexes = randomArrayIndexesTemp;
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            addRandomBlocks();
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}

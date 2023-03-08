package org.depaul.logic.data;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ScoreTest {
    @Test
    public void testAdd() {
        Score score = new Score();
        score.add(1);
        score.add(10);
        assertEquals(11, score.scoreProperty().intValue());
    }

    @Test
    public void testReset() {
        Score score = new Score();
        score.add(5);
        score.add(2);
        score.reset();
        assertEquals(0, score.scoreProperty().intValue());
    }

    @Test
    public void testScoreProperty() {
        Score score = new Score();
        score.add(100);
        int result = score.scoreProperty().intValue();
        assertEquals(100, result);

    }
}

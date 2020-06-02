package jfound.test;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.II_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

public class VolatileTest {

    /**
     * 没有出现 0, 0
     * setVolatile 后面 load 不会被重排序
     * getVolatile 前面的 store 不会被重排序
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = ACCEPTABLE)
    @Outcome(id = "1, 0", expect = ACCEPTABLE)
    @Outcome(id = "0, 1", expect = ACCEPTABLE)
    @State
    public static class VolatileTester1 {

        private final TestData testData = new TestData();


        @Actor
        public void actor1(II_Result r) {
            TestData.X.setVolatile(testData, 1);
            r.r2 = (int) TestData.Y.getVolatile(testData);
        }

        @Actor
        public void actor2(II_Result r) {
            TestData.Y.setVolatile(testData, 1);
            r.r1 = (int) TestData.X.getVolatile(testData);
        }
    }


    /**
     * setVolatile 前面的 store 不会被重排序
     * getVolatile 后面的 store 不会被重排序
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = ACCEPTABLE)
    @Outcome(id = "1, 0", expect = FORBIDDEN)
    @Outcome(id = "0, 1", expect = ACCEPTABLE)
    @Outcome(id = "0, 0", expect = ACCEPTABLE)
    @State
    public static class VolatileTester2 {

        private final TestData testData = new TestData();

        @Actor
        public void actor1() {
            testData.y = 1;
            TestData.X.setVolatile(testData, 1);
        }

        @Actor
        public void actor2(II_Result r) {
            r.r1 = (int) TestData.X.getVolatile(testData);
            r.r2 = testData.y;
        }
    }
}

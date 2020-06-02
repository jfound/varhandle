package jfound.test;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

/**
 * release acquire
 * setRelease
 * 缓存一致,按程序顺序执行, 确保前面的load和store不会被重排序到后面。
 * getAcquire
 * 缓存一致,按程序顺序执行,确保后面的load和store不会被重排序到前面
 */
public class RATest {
    /**
     * 确保顺序，不存在 1, 0 这结果， 保证执行顺序
     * setRelease 确保前面的load和store不会被重排序到后面。
     * getAcquire 确保后面的load和store不会被重排序到前面
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = ACCEPTABLE)
    @Outcome(id = "1, 0", expect = FORBIDDEN)
    @Outcome(id = "0, 1", expect = ACCEPTABLE)
    @Outcome(id = "0, 0", expect = ACCEPTABLE)
    @State
    public static class RAOrderTester1 {

        private final TestData testData = new TestData();


        @Actor
        public void actor1() {
            testData.y = 1;
            TestData.X.setRelease(testData, 1);
        }

        @Actor
        public void actor2(II_Result r) {
            r.r1 = (int) TestData.X.getAcquire(testData);
            r.r2 = testData.y;
        }
    }

    /**
     * 不存在1, 1，不存在排序
     * <p>
     * setRelease 确保前面的load和store不会被重排序到后面。
     * getAcquire 确保后面的load和store不会被重排序到前面
     *
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "0, 1", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 1", expect = Expect.FORBIDDEN)
    @State
    public static class RAOrderTester2 {
        private final TestData testData = new TestData();

        @Actor
        public void actor1(II_Result r) {
            r.r2 = (int) TestData.Y.getAcquire(testData);
            TestData.X.setRelease(testData, 1);
        }

        @Actor
        public void actor2(II_Result r) {
            r.r1 = (int) TestData.X.getAcquire(testData);
            TestData.Y.setRelease(testData, 1);
        }
    }

    /**
     * 出现 0, 0 则表示被重排序
     * <p>
     * setRelease 后面的 getRelease 可以被重排序
     * getAcquire 前面的 setRelease 可以被重排序
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "0, 1", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 1", expect = Expect.ACCEPTABLE)
    @State
    public static class RAOrderTester3 {
        private final TestData testData = new TestData();

        @Actor
        public void actor1(II_Result r) {
            TestData.X.setRelease(testData, 1);
            r.r2 = (int) TestData.Y.getAcquire(testData);
        }

        @Actor
        public void actor2(II_Result r) {
            TestData.Y.setRelease(testData, 1);
            r.r1 = (int) TestData.X.getAcquire(testData);
        }
    }


}

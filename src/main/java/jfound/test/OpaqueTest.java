package jfound.test;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

/**
 * opaque 测试
 * 缓存一致,按程序顺序执行,不确保其他线程可见顺序。
 */
public class OpaqueTest {

    /**
     * 不确保顺序，存在 0, 0 这结果
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "0, 1", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 1", expect = Expect.ACCEPTABLE)
    @State
    public static class OpaqueOrderTester1 {

        private final TestData testData = new TestData();

        @Actor
        public void actor1(II_Result r) {
            TestData.X.setOpaque(testData, 1);
            r.r2 = (int) TestData.Y.getOpaque(testData);
        }

        @Actor
        public void actor2(II_Result r) {
            TestData.Y.setOpaque(testData, 1);
            r.r1 = (int) TestData.X.getOpaque(testData);
        }
    }

    /**
     * 不确保顺序，存在 1, 1 这结果
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "0, 1", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 1", expect = Expect.ACCEPTABLE)
    @State
    public static class OpaqueOrderTester2 {

        private final TestData testData = new TestData();

        @Actor
        public void actor1(II_Result r) {
            r.r2 = (int) TestData.Y.getOpaque(testData);
            TestData.X.setOpaque(testData, 1);
        }

        @Actor
        public void actor2(II_Result r) {
            r.r1 = (int) TestData.X.getOpaque(testData);
            TestData.Y.setOpaque(testData, 1);
        }
    }

    /**
     * 不存在 1, 0 ，相对普通变量来说，也是按顺序执行
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = ACCEPTABLE)
    @Outcome(id = "1, 0", expect = FORBIDDEN)
    @Outcome(id = "0, 1", expect = ACCEPTABLE)
    @Outcome(id = "0, 0", expect = ACCEPTABLE)
    @State
    public static class OpaqueOrderTester3 {

        private final TestData testData = new TestData();

        @Actor
        public void actor1() {
            testData.y = 1;
            TestData.X.setOpaque(testData, 1);
        }

        @Actor
        public void actor2(II_Result r) {
            r.r1 = (int) TestData.X.getOpaque(testData);
            r.r2 = testData.y;
        }
    }

    /**
     * 不存在 1, 0 ，说明 setOpaque 和 getOpaque 都是按顺序执行的
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = ACCEPTABLE)
    @Outcome(id = "1, 0", expect = FORBIDDEN)
    @Outcome(id = "0, 1", expect = ACCEPTABLE)
    @Outcome(id = "0, 0", expect = ACCEPTABLE)
    @State
    public static class OpaqueOrderTester4 {

        private final TestData testData = new TestData();

        @Actor
        public void actor1() {
            TestData.Y.setOpaque(testData, 1);
            TestData.X.setOpaque(testData, 1);
        }

        @Actor
        public void actor2(II_Result r) {
            r.r1 = (int) TestData.X.getOpaque(testData);
            r.r2 = (int) TestData.Y.getOpaque(testData);
        }
    }

}

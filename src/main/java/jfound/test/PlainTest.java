package jfound.test;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * 普通变量测试
 *
 * @author beldon
 */
public class PlainTest {

    /**
     * 出现了 0, 0 的情况，说明出现了重排序
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "0, 1", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 1", expect = Expect.ACCEPTABLE)
    @State
    public static class PlainOrderTester {

        private final TestData testData = new TestData();

        @Actor
        public void actor1(II_Result r) {
            testData.x = 1;
            r.r2 = testData.y;
        }

        @Actor
        public void actor2(II_Result r) {
            testData.y = 1;
            r.r1 = testData.x;
        }
    }
}

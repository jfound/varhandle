package jfound.test;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

public class VolatileReorderTest {

    /**
     * volatile get 前面普通变量会被重排序
     * 存在 1, 0 volatile 前面的 get 可以被重排序到后面
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "0, 1", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 1", expect = Expect.ACCEPTABLE)
    @State
    public static class VolatileLoadTester1 {
        private int x;
        private volatile int y;

        @Actor
        public void actor1(II_Result r) {
            r.r1 = x;
            r.r2 = y;
        }

        @Actor
        public void actor2() {
            synchronized (this) {
                y = 1;
            }

            synchronized (this) {
                x = 1;
            }
        }
    }

    /**
     * volatile get 后面的 普通 get 不被重排序的到前面
     * 不存在 0, 1 ，说明volatile get 后面的 普通 get 不被重排序的到前面
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "0, 1", expect = Expect.FORBIDDEN)
    @Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
    @Outcome(id = "1, 1", expect = Expect.ACCEPTABLE)
    @State
    public static class VolatileLoadTester2 {
        private int x;
        private volatile int y;

        @Actor
        public void actor1(II_Result r) {
            r.r2 = y;
            r.r1 = x;
        }

        @Actor
        public void actor2() {
            synchronized (this) {
                x = 1;
            }
            synchronized (this) {
                y = 1;
            }
        }
    }
}

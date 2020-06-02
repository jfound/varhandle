package jfound.test;

import org.openjdk.jcstress.annotations.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * 测试内存可见性
 *
 * @author beldon
 */
public class MemoryVisibilityTest {

    /**
     * 普通变量不确保内存可见性
     */
    @JCStressTest(Mode.Termination)
    @Outcome(id = "STALE", expect = Expect.ACCEPTABLE)
    @Outcome(id = "TERMINATED", expect = Expect.ACCEPTABLE)
    public static class PlainTester {
        private int x = 0;

        @Actor
        public void actor() {
            while (x == 0) {
                //do nothing
            }
        }

        @Signal
        public void signal() {
            x = 1;
        }
    }

    /**
     * volatile 变量确保内存可见性
     */
    @JCStressTest(Mode.Termination)
    @Outcome(id = "STALE", expect = Expect.FORBIDDEN)
    @Outcome(id = "TERMINATED", expect = Expect.ACCEPTABLE)
    public static class VolatileTester {
        private volatile int x = 0;

        @Actor
        public void actor() {
            while (x == 0) {
                //do nothing
            }
        }

        @Signal
        public void signal() {
            x = 1;
        }
    }


    /**
     * volatile 变量确保内存可见性
     */
    @JCStressTest(Mode.Termination)
    @Outcome(id = "STALE", expect = Expect.FORBIDDEN)
    @Outcome(id = "TERMINATED", expect = Expect.ACCEPTABLE)
    public static class Volatile2Tester {
        private int x = 0;

        private static final VarHandle X;

        static {
            try {
                X = MethodHandles.lookup().findVarHandle(Volatile2Tester.class, "x", int.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new Error(e);
            }
        }

        @Actor
        public void actor() {
            while ((int) X.getVolatile(this) == 0) {
                //do nothing
            }
        }

        @Signal
        public void signal() {
            X.setVolatile(this, 1);
        }
    }

    /**
     * Opaque 变量确保内存可见性
     */
    @JCStressTest(Mode.Termination)
    @Outcome(id = "STALE", expect = Expect.FORBIDDEN)
    @Outcome(id = "TERMINATED", expect = Expect.ACCEPTABLE)
    public static class OpaqueTester {
        private volatile int x = 0;

        private static final VarHandle X;

        static {
            try {
                X = MethodHandles.lookup().findVarHandle(OpaqueTester.class, "x", int.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new Error(e);
            }
        }

        @Actor
        public void actor() {
            while ((int) X.getOpaque(this) == 0) {
                //do nothing
            }
        }

        @Signal
        public void signal() {
            X.setOpaque(this, 1);
        }
    }

    /**
     * release & acquire 变量确保内存可见性
     */
    @JCStressTest(Mode.Termination)
    @Outcome(id = "STALE", expect = Expect.FORBIDDEN)
    @Outcome(id = "TERMINATED", expect = Expect.ACCEPTABLE)
    public static class ReleaseAcquireTester {
        private volatile int x = 0;

        private static final VarHandle X;

        static {
            try {
                X = MethodHandles.lookup().findVarHandle(ReleaseAcquireTester.class, "x", int.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new Error(e);
            }
        }

        @Actor
        public void actor() {
            while ((int) X.getAcquire(this) == 0) {
                //do nothing
            }
        }

        @Signal
        public void signal() {
            X.setRelease(this, 1);
        }
    }


}

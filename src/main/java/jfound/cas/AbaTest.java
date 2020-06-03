package jfound.cas;

import jfound.test.TestData;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * @author Beldon
 */
public class AbaTest {

    /**
     * 当 actor1 先执行的时候，链表由 A->B 变成 B, 然后执行 actor2, 链表由B, 变成 B->C-D; 链表长度为 3
     * 当 actor2 先执行的时候，链表由 A->B 变成 A->C->D, 然后执行 actor1, 链表由 A->C-D 变成 C->D; 链表长度为 2
     * <p>
     * 当链表长度变成 1 的时候，ABA的问题就出现了，
     * ABA 问题：
     * 若 actor1 先读取到 A的时候，A.next 为 B,然后actor1被挂起了
     * 到 actor2 执行完，head依然是 A ，但是 A.next为 C了，此时 actor1 被唤醒，然后把 head 设为 B，因为 B 的 next 为null，所以链表长度为1
     */
    @JCStressTest
    @Outcome(id = "1", expect = Expect.ACCEPTABLE)
    @Outcome(id = "2", expect = Expect.ACCEPTABLE)
    @Outcome(id = "3", expect = Expect.ACCEPTABLE)
    @State
    public static class AbaTester {
        private Node head;
        private static VarHandle HEAD_HANDLE;

        static {
            try {
                HEAD_HANDLE = MethodHandles.lookup().findVarHandle(AbaTester.class, "head", Node.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new Error(e);
            }
        }

        public AbaTester() {
            Node nodeB = new Node("B", null);
            head = new Node("A", nodeB);
        }

        @Actor
        public void actor1() {
            HEAD_HANDLE.compareAndSet(this, this.head, this.head.next);
        }

        @Actor
        public void actor2() {
            Node oldHead = this.head;
            this.head = new Node("D", null);
            this.head = new Node("C", this.head);
            oldHead.next = this.head;
            this.head = oldHead;
        }

        @Arbiter
        public void arbiter(I_Result r) {
            int count = 0;
            Node current = this.head;
            do {
                count++;
                current = current.next;
            } while (current != null);
            r.r1 = count;
        }
    }

    private static class Node {
        private final String name;
        private volatile Node next;

        private Node(String name, Node next) {
            this.name = name;
            this.next = next;
        }
    }

}

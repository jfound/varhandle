###

![jfound](./jfound.jpg)

---

- 普通变量是不确保内存可见的，opaque、release/acquire、volatile是可以保证内存可见的
- opaque 确保程序执行顺序，但不保证其它线程的可见顺序
- release/acquire 保证程序执行顺序，setRelease 确保前面的load和store不会被重排序到后面，但不确保后面的load和store重排序到前面；getAcquire 确保后面的load和store不会被重排序到前面，但不确保前面的load和store被重排序。
- volatile确保程序执行顺序，能保证变量之间的不被重排序。

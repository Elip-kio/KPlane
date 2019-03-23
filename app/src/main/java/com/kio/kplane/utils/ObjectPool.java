package com.kio.kplane.utils;

import java.io.InvalidObjectException;
import java.util.ArrayList;

/**
 * <h5>工具类：对象池</h5>
 * <p>针对于频繁创建和销毁的对象，使用<code>对象池</code>进行维护
 * 应当先构建一个使用中的列表(一般称为<code>使用池</code>)，然后作为引用传入该对象池
 * <strong>子元素必须实现<code>ObjectPool.Poolable</code></strong>
 * </p>
 *
 * @see ObjectPool.Poolable
 */
public class ObjectPool<T extends ObjectPool.Poolable> {
    private ArrayList<T> poolItems;
    private int maxSize;
    private final ArrayList<T> activePool;

    /**
     * 无限的池长度
     */
    public static final int INFINITY = -1;

    /**
     * 可池化对象必须实现回收前和使用前的具体操作
     */
    public interface Poolable {
        /**
         * 使用前将会被调用
         */
        void beforeReuse();

        /**
         * 回收前将会被调用
         */
        void beforeRecycle();
    }

    /**
     * <p>构造一个<code>对象池</code></p>
     *
     * @param maxSize    最大池长度
     * @param activePool 使用池
     */
    public ObjectPool(int maxSize, ArrayList<T> activePool) {
        this.poolItems = new ArrayList<>();
        this.maxSize = maxSize;
        this.activePool = activePool;
    }

    /**
     * <p>同步的回收对象的方法</p>
     *
     * @throws InvalidObjectException 当<code>使用池</code>中无该对象时，抛出对象非法错误
     */
    public void recycle(T t) throws InvalidObjectException {
        if (!activePool.contains(t))
            throw new InvalidObjectException("no such object " + t + " in using list");
        if (this.poolItems.size() < maxSize || maxSize == INFINITY) {
            t.beforeRecycle();
            this.poolItems.add(t);
            this.activePool.remove(t);
        }
    }

    /**
     * 从对象池中获取对象
     *
     * @return 返回池中的对象，当池中没有对象时返回<code>null</code>
     */
    public T getInstance() {
        if (poolItems.size() > 0) {
            activePool.add(poolItems.get(0));
            poolItems.get(0).beforeReuse();
            return poolItems.remove(0);
        }
        return null;
    }
}

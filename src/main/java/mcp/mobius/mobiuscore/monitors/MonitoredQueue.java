package mcp.mobius.mobiuscore.monitors;

import kcauldron.wrapper.ProcessingQueue;

import java.util.Collection;
import java.util.Queue;

public abstract class MonitoredQueue<E> extends ProcessingQueue<E> implements Monitored<E> {
    public MonitoredQueue(Queue<E> collection) {
        super(collection);
    }

    @Override
    public boolean add(E e) {
        this.addCount(e);
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c)
            this.addCount(e);
        return super.addAll(c);
    }

    @Override
    public void clear() {
        this.clearCount();
        super.clear();
    }

    @Override
    public boolean remove(Object o) {
        this.removeCount(o);
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c)
            this.removeCount(o);
        return super.removeAll(c);
    }
}

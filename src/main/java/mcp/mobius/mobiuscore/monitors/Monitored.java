package mcp.mobius.mobiuscore.monitors;

public interface Monitored<E> {
    void addCount(E e);
    void removeCount(Object o);
    void printCount();
    void clearCount();
}

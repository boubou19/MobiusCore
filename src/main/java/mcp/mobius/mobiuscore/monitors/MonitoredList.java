package mcp.mobius.mobiuscore.monitors;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MonitoredList<E> extends ArrayList<E> implements Monitored<E> {
	@Override
	public boolean add(E e){
		this.addCount(e);
		return super.add(e);
	}
	
	@Override
	public void add(int index, E e){
		this.addCount(e);
		super.add(index, e);
	}	
	
	@Override
	public boolean 	addAll(Collection<? extends E> c){
		for (E e : c)
			this.addCount(e);
		
		return super.addAll(c);
	}
	
	@Override
	public boolean 	addAll(int index, Collection<? extends E> c){
		for (E e : c)
			this.addCount(e);		
		
		return super.addAll(index, c);
	}
	
	@Override
	public void clear(){
		this.clearCount();
		super.clear();
	}
	
	@Override
	public E remove(int index){
        E oldValue = super.remove(index);
        this.removeCount(oldValue);
        return oldValue;
	}
	
	@Override
	public boolean 	remove(Object o){
		this.removeCount(o);
		return super.remove(o);
	}
	
	@Override
	public boolean 	removeAll(Collection<?> c){
		for (Object o : c)
			this.removeCount(o);
		
		return super.removeAll(c);
	}
}


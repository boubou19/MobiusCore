package mcp.mobius.mobiuscore.profiler_v2;

import net.minecraft.world.World;

public interface IProfilerBase {

	public void reset();
	
	public void start();
	public void stop();

	public void start(Object key);
	public void stop(Object key);	

	public void start(Object key1, Object key2);
	public void stop(Object key1, Object key2);		
	
}

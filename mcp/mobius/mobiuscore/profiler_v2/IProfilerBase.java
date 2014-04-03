package mcp.mobius.mobiuscore.profiler_v2;

import net.minecraft.world.World;

public interface IProfilerBase {

	public void start();
	public void stop();

	public void start(Integer key);
	public void stop(Integer key);	

	public void start(World key);
	public void stop(World key);		
	
}

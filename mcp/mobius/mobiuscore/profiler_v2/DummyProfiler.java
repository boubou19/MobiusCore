package mcp.mobius.mobiuscore.profiler_v2;

import net.minecraft.world.World;

public class DummyProfiler implements IProfilerBase{

	@Override
	public void start() {}

	@Override
	public void stop() {}

	@Override
	public void start(Integer key) {}

	@Override
	public void stop(Integer key) {}

	@Override
	public void start(World key) {}

	@Override
	public void stop(World key) {}

}

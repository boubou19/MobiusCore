package mcp.mobius.mobiuscore.profiler;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class DummyProfiler implements IProfilerTileEntity, IProfilerEntity {
	@Override
	public void FullTEStart() {}

	@Override
	public void FullTEStop() {}

	@Override
	public void Start(TileEntity te) {}

	@Override
	public void Stop(TileEntity te) {}

	@Override
	public void FullEntityStart() {}

	@Override
	public void FullEntityStop() {}

	@Override
	public void Start(Entity ent) {}

	@Override
	public void Stop(Entity ent) {}

}

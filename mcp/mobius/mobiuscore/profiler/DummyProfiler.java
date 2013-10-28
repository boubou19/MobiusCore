package mcp.mobius.mobiuscore.profiler;

import net.minecraft.tileentity.TileEntity;

public class DummyProfiler implements IProfilerTileEntity {
	@Override
	public void GlobalStart() {
	}

	@Override
	public void GlobalStop() {
	}

	@Override
	public void Start(TileEntity te) {
	}

	@Override
	public void Stop(TileEntity te) {
	}

}

package mcp.mobius.mobiuscore.profiler;

import net.minecraft.tileentity.TileEntity;

public interface IProfilerTileEntity {
	public void GlobalStart();
	public void GlobalStop();
	
	public void Start(TileEntity te);
	public void Stop (TileEntity te);
}

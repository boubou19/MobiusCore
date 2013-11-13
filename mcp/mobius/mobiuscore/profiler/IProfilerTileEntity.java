package mcp.mobius.mobiuscore.profiler;

import net.minecraft.tileentity.TileEntity;

public interface IProfilerTileEntity {
	public void FullTEStart();
	public void FullTEStop();
	
	public void Start(TileEntity te);
	public void Stop (TileEntity te);
}

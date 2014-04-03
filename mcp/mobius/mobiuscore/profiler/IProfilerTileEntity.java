package mcp.mobius.mobiuscore.profiler;

import net.minecraft.tileentity.TileEntity;

public interface IProfilerTileEntity extends IProfilerBase {
	public void FullTileEntityStart();
	public void FullTileEntityStop();
	
	public void Start(TileEntity te);
	public void Stop (TileEntity te);
}

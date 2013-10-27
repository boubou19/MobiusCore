package mcp.mobius.mobiuscore.profiler;

import net.minecraft.tileentity.TileEntity;

public interface IProfiler {
	public void TileEntityUpdate_Start(TileEntity te);
	public void TileEntityUpdate_Stop (TileEntity te);
}

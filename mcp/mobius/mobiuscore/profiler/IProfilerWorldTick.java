package mcp.mobius.mobiuscore.profiler;

import net.minecraft.world.ChunkCoordIntPair;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;

public interface IProfilerWorldTick extends IProfilerBase{
	public void WorldTickStart(int dim);
	public void WorldTickEnd(int dim);	
}

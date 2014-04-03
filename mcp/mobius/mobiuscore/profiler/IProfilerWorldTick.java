package mcp.mobius.mobiuscore.profiler;

import net.minecraft.world.ChunkCoordIntPair;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;

public interface IProfilerWorldTick extends IProfilerBase{
	public void WorldTickStart(int dim);
	public void WorldTickEnd(int dim);	
	
	public void startDim(int dim, String subsection);
	public void  stopDim(int dim, String subsection);
	
	public void startChunk(int dim, ChunkCoordIntPair chunk, String subsection);
	public void  stopChunk(int dim, ChunkCoordIntPair chunk, String subsection);
}

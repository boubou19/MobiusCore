package mcp.mobius.mobiuscore.profiler;

import java.util.EnumSet;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;

public class DummyProfiler implements IProfilerTileEntity, IProfilerHandler, 
                                      IProfilerEntUpdate,
                                      IProfilerNetwork {
	@Override
	public void FullTileEntityStart() {}

	@Override
	public void FullTileEntityStop() {}

	@Override
	public void Start(TileEntity te) {
		//System.out.printf("%s [%s %s %s]\n", te.getClass().getName(), te.xCoord, te.yCoord, te.zCoord);
	}

	@Override
	public void Stop(TileEntity te) {}

	@Override
	public void StartTickStart(IScheduledTickHandler ticker, EnumSet<TickType> ticksToRun) {}

	@Override
	public void StopTickStart(IScheduledTickHandler ticker, EnumSet<TickType> ticksToRun) {}

	@Override
	public void StartTickEnd(IScheduledTickHandler ticker, EnumSet<TickType> ticksToRun) {}

	@Override
	public void StopTickEnd(IScheduledTickHandler ticker, EnumSet<TickType> ticksToRun) {}

	@Override
	public void EntUpdateStart(int id) {}

	@Override
	public void EntUpdateEnd(int id) {}

	@Override
	public void addPacketOut(Packet packet) {}

	@Override
	public void addPacketIn(Packet packet) {}
	
	@Override
	public void startNetwork(String subprofile) {}

	@Override
	public void stopNetwork(String subprofile) {}		
}

package mcp.mobius.mobiuscore.profiler;

import net.minecraft.network.packet.Packet;

public interface IProfilerPacket {
	void addPacketOut(Packet packet);
	void addPacketIn(Packet packet);
}

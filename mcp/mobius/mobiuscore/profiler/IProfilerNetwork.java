package mcp.mobius.mobiuscore.profiler;

import net.minecraft.network.packet.Packet;

public interface IProfilerNetwork extends IProfilerBase {
	void addPacketOut(Packet packet);
	void addPacketIn(Packet packet);
	
	void startNetwork(String subprofile);
	void stopNetwork(String subprofile);
}

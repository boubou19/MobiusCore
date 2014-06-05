package mcp.mobius.mobiuscore.asm;

import java.util.HashMap;

public enum TargetVersion {
	NOTSET,
	ECLIPSE,
	FORGE,
	MCPC;
	
	private static HashMap<String, TargetVersion> checksums = new HashMap<String, TargetVersion>();
	private static TargetVersion version = TargetVersion.NOTSET;
	
	static {
		checksums.put("11B8C6D6620D782E8BAF798743C35179", FORGE);	//MinecraftServer
		checksums.put("D948375D466A3BE9FE1D39E2D576450C", MCPC);	//MinecraftServer
		checksums.put("6555654E81266AC7EAFE7A35B1E89B71", ECLIPSE); //MinecraftServer
		
		checksums.put("AECCA57A66EB8925C1E097A773557156", FORGE);	//MinecraftClient
		checksums.put("C567A42F708A58A62374D9B323A0592F", ECLIPSE); //MinecraftClient		
	}
	
	public static TargetVersion getVersion(String checksum){
		if (checksums.containsKey(checksum))
			version = checksums.get(checksum);
		return version;
	}
}

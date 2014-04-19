package mcp.mobius.mobiuscore.asm;

public enum TargetVersion {
	UNKNOWN  ("00000000000000000000000000000000"),
	ECLIPSE  ("692647CD444C16C2ADB9DC910E7075AF"),
	FORGE_965("66EEDF6E5E980628FE43F39F53AF9064"),
	MCPC_B250("D81A8F118691C19722544C02FD2CFBE4");
	
	private String checksum;
	
	private TargetVersion(String checksum){
		this.checksum = checksum;
	}
	
	private String getChecksum(){
		return this.checksum;
	}
	
	public static TargetVersion getVersion(String checksum){
		for (TargetVersion testVersion : TargetVersion.values()){
			if (testVersion.getChecksum().equals(checksum))
				return testVersion;
		}
		return TargetVersion.UNKNOWN;
	}
}

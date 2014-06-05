package mcp.mobius.mobiuscore.asm.transformers.common;

import mcp.mobius.mobiuscore.asm.TargetVersion;
import mcp.mobius.mobiuscore.asm.CoreTransformer;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;

public class TransformerMinecraftServer extends TransformerBase {

	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		CoreTransformer.version = TargetVersion.getVersion(this.dumpChecksum(bytes, name, srgname));
		System.out.printf("[MobiusCore] Switching injection mode to %s\n", CoreTransformer.version);
		return bytes;
	}

}


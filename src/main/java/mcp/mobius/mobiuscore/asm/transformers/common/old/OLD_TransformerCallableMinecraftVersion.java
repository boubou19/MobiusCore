package mcp.mobius.mobiuscore.asm.transformers.common.old;

import mcp.mobius.mobiuscore.asm.CoreTransformer;
import mcp.mobius.mobiuscore.asm.TargetVersion;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;

public class OLD_TransformerCallableMinecraftVersion extends TransformerBase {

	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		CoreTransformer.version = TargetVersion.getVersion(this.dumpChecksum(bytes, name, srgname));
		System.out.printf("[MobiusCore] Switching injection mode to %s\n", CoreTransformer.version);
		return bytes;
	}

}

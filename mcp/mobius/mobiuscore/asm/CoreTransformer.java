package mcp.mobius.mobiuscore.asm;

import net.minecraft.launchwrapper.IClassTransformer;

public class CoreTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		System.out.printf("%s %s\n", name, srgname);
		return bytes;
	}

}

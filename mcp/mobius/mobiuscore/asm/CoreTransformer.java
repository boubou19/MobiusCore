package mcp.mobius.mobiuscore.asm;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class CoreTransformer implements IClassTransformer {

	// TileEntityHopper  => asi
	// TileEntityFurnace => asg

	TransformerWorld            transWorld = new TransformerWorld();
	TransformerFMLCommonHandler transFML   = new TransformerFMLCommonHandler(); 
	TransformerMinecraftServer  transMCS   = new TransformerMinecraftServer();
	TransformerWorldServer      transWorldServer   = new TransformerWorldServer();
	
	public CoreTransformer(){
		super();
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		if (srgname.equals("net.minecraft.world.World"))
			return transWorld.transform(name, srgname, bytes);
		
		if (srgname.equals("cpw.mods.fml.common.FMLCommonHandler")){
			System.out.printf("++++++++++++++++++++++++++++++++++++++++++++++++++\n");
			
			/*
			System.out.printf("FMLCH : %s\n", bytes.length);
			for (byte b : bytes)
				System.out.printf("%02X ", b);
			System.out.printf("\n");			
			*/
			
			return transFML.transform(name, srgname, bytes);		
		}

		//if (srgname.equals("net.minecraft.server.MinecraftServer")){
		//	return transMCS.transform(name, srgname, bytes);
		//}
		
		if (srgname.equals("net.minecraft.world.WorldServer")){
			return transWorldServer.transform(name, srgname, bytes);
		}		
		
		return bytes;
	}


}

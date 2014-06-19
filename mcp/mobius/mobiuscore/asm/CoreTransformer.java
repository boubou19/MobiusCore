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

import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.asm.transformers.cauldron.TransformerWorldCauldron;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerASMEventHandler;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerFMLCommonHandler;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerMemoryConnection;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerNetworkListenThread;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerRenderManager;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerTERenderer;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerTcpConnection;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerWorldServer;
import mcp.mobius.mobiuscore.asm.transformers.forge.TransformerWorld;
import net.minecraft.launchwrapper.IClassTransformer;

public class CoreTransformer implements IClassTransformer {
	
	public CoreTransformer(){
		super();
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		//TransformerBase.dumpChecksum(bytes, name, srgname); 
		
		if (srgname.equals("net.minecraft.world.World")){
			return new TransformerWorld().transform(name, srgname, bytes);
		}
		
		if (srgname.equals("cpw.mods.fml.common.FMLCommonHandler")){
			return new TransformerFMLCommonHandler().transform(name, srgname, bytes);		
		}
		
		if (srgname.equals("net.minecraft.world.WorldServer")){
			return new TransformerWorldServer().transform(name, srgname, bytes);
		}		
		
		if (srgname.equals("net.minecraft.network.TcpConnection")){
			return new TransformerTcpConnection().transform(name, srgname, bytes);
		}			
		
		if (srgname.equals("net.minecraft.client.renderer.entity.RenderManager")){
			return new TransformerRenderManager().transform(name, srgname, bytes);
		}	
		
		if (srgname.equals("net.minecraft.client.renderer.tileentity.TileEntityRenderer")){
			return new TransformerTERenderer().transform(name, srgname, bytes);
		}			
		
		if (srgname.equals("net.minecraft.server.integrated.IntegratedServerListenThread")){
			return new TransformerNetworkListenThread().transform(name, srgname, bytes);
		}		
		
		if (srgname.equals("net.minecraft.server.dedicated.DedicatedServerListenThread")){
			return new TransformerNetworkListenThread().transform(name, srgname, bytes);
		}		
	
		if (srgname.equals("net.minecraftforge.event.ASMEventHandler")){
			return new TransformerASMEventHandler().transform(name, srgname, bytes);
		}			
		
		if (srgname.equals("net.minecraft.network.MemoryConnection")){
			return new TransformerMemoryConnection().transform(name, srgname, bytes);			
		}		
		
		return bytes;
	}


}

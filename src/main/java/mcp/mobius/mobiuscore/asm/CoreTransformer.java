package mcp.mobius.mobiuscore.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
import org.objectweb.asm.util.TraceClassVisitor;

import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerASMEventHandler;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerFMLCommonHandler;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerFMLOutboundHandler;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerFMLProxyPacket;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerTERenderer;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerRenderManager;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerMessageDeserializer;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerMessageSerializer;
import mcp.mobius.mobiuscore.asm.transformers.common.TransformerWorldServer;
import mcp.mobius.mobiuscore.asm.transformers.forge.TransformerWorld;
import mcp.mobius.mobiuscore.asm.transformers.mcpc.TransformerWorldCauldron;
import net.minecraft.launchwrapper.IClassTransformer;

public class CoreTransformer implements IClassTransformer {
	
	public CoreTransformer(){
		super();
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		try{
		
			//TransformerBase.dumpChecksum(bytes, name, srgname);
			
			if (srgname.equals("net.minecraft.world.World") && !ObfTable.isCauldron()){
				bytes = new TransformerWorld().transform(name, srgname, bytes);
			}

			if (srgname.equals("net.minecraft.world.World") && ObfTable.isCauldron()){
				bytes = new TransformerWorldCauldron().transform(name, srgname, bytes);
			}			
			
			if (srgname.equals("net.minecraft.world.WorldServer")){
				bytes = new TransformerWorldServer().transform(name, srgname, bytes);
			}

			if (srgname.equals("net.minecraft.util.MessageSerializer")){
				bytes = new TransformerMessageSerializer().transform(name, srgname, bytes);
			}
	
			if (srgname.equals("net.minecraft.util.MessageDeserializer")){
				bytes = new TransformerMessageDeserializer().transform(name, srgname, bytes);
			}		
			
			if (srgname.equals("net.minecraft.client.renderer.entity.RenderManager")){
				bytes = new TransformerRenderManager().transform(name, srgname, bytes);
			}		
	
			if (srgname.equals("net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher")){
				bytes = new TransformerTERenderer().transform(name, srgname, bytes);
			}				
			
			if (srgname.equals("cpw.mods.fml.common.FMLCommonHandler")){
				bytes = new TransformerFMLCommonHandler().transform(name, srgname, bytes);
			}			
	
			if (srgname.equals("cpw.mods.fml.common.network.FMLOutboundHandler")){
				bytes = new TransformerFMLOutboundHandler().transform(name, srgname, bytes);
			}					
			
			//if (srgname.equals("cpw.mods.fml.common.network.internal.FMLProxyPacket")){
			//	bytes = new TransformerFMLProxyPacket().transform(name, srgname, bytes);
			//}			
	
			if (srgname.equals("cpw.mods.fml.common.eventhandler.ASMEventHandler")){
				bytes = new TransformerASMEventHandler().transform(name, srgname, bytes);
			}					
		
		} catch (Exception e){
			ClassNode   classNode   = new ClassNode();
	        ClassReader classReader = new ClassReader(bytes);		
	        classReader.accept(classNode, 0);			
        	try{
        		PrintWriter pw = new PrintWriter(new File(String.format("%s.asm", srgname.replaceAll("/", "."))));
        		TraceClassVisitor cv = new TraceClassVisitor(pw);
        		classReader.accept(cv, 0);
        		pw.flush();
        		throw new RuntimeException(e);
        	} catch (FileNotFoundException f){
        		throw new RuntimeException("DERP");
        	}			
		}
		return bytes;
	}


}

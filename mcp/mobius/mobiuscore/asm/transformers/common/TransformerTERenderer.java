package mcp.mobius.mobiuscore.asm.transformers.common;
import java.util.ArrayList;
import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;

import mcp.mobius.mobiuscore.asm.ObfTable;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;

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

public class TransformerTERenderer extends TransformerBase {

	private static String TER_RENDER;
	
	private static AbstractInsnNode[] TER_RENDER_PATTERN_TOP;
	private static AbstractInsnNode[] TER_RENDER_PATTERN_BOTTOM;
	
	private static AbstractInsnNode[] TER_RENDER_PAYLOAD_TOP;
	private static AbstractInsnNode[] TER_RENDER_PAYLOAD_BOTTOM;
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		//TER_RENDER = "a (Lasp;F)V";
		TER_RENDER = ObfTable.TERENDER_RENDERAT.getFullDescriptor();
		
		/*
		TER_RENDER_PATTERN_TOP =	new AbstractInsnNode[] 
				{//new LineNumberNode(-1, new LabelNode()), 
				 new VarInsnNode(Opcodes.ALOAD, -1),
				 new FieldInsnNode(Opcodes.GETFIELD, "bjd", "f", "Labw;"), //"net/minecraft/client/renderer/tileentity/TileEntityRenderer", "worldObj", "Lnet/minecraft/world/World;"),
				 new VarInsnNode(Opcodes.ALOAD, -1),
				 new FieldInsnNode(Opcodes.GETFIELD, "asp", "l", "I"),     //"net/minecraft/tileentity/TileEntity", "xCoord", "I"),
				 new VarInsnNode(Opcodes.ALOAD, -1),
				 new FieldInsnNode(Opcodes.GETFIELD, "asp", "m", "I"),     //"net/minecraft/tileentity/TileEntity", "yCoord", "I"),
				 new VarInsnNode(Opcodes.ALOAD, -1),
				 new FieldInsnNode(Opcodes.GETFIELD, "asp", "n", "I"),     //"net/minecraft/tileentity/TileEntity", "zCoord", "I"),				 
				 };
		
		TER_RENDER_PATTERN_BOTTOM =	new AbstractInsnNode[] 
				{
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "bjd", "a", "(Lasp;DDDF)V") //net/minecraft/client/renderer/tileentity/TileEntityRenderer.renderTileEntityAt(Lnet/minecraft/tileentity/TileEntity;DDDF)V)
				};
		*/
		
		TER_RENDER_PAYLOAD_TOP = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.RENDER_TILEENTITY.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 1),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;)V")};				
		
		TER_RENDER_PAYLOAD_BOTTOM = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.RENDER_TILEENTITY.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 1),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;)V")};			
		
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		this.dumpChecksum(bytes, name, srgname);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        MethodNode renderEntNode  = this.getMethod(classNode, TER_RENDER);
        if (this.checkPreviousInjection(renderEntNode)) return bytes;
        this.applyPayloadFirst(renderEntNode, TER_RENDER_PAYLOAD_TOP);
        this.applyPayloadLast (renderEntNode, TER_RENDER_PAYLOAD_BOTTOM); 
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}

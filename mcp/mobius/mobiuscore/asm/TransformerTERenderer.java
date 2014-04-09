package mcp.mobius.mobiuscore.asm;
import java.util.ArrayList;
import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;

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
		TER_RENDER = "a (Lasp;DDDF)V";
		
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
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        /*
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(TER_RENDER)){
        		//System.out.printf("Found TileEntityRenderer.renderTileEntity()... \n");
        		System.out.printf("Found TileEntityRenderer.renderTileEntityAt()... \n");
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		ArrayList<ArrayList<AbstractInsnNode>> match;

        		match = this.findPattern(methodNode, TER_RENDER_PATTERN_TOP);
        		if (match.size() != 0){
        			for (ArrayList<AbstractInsnNode> sublist : match){
        				System.out.printf("Trying to inject rendering profiler start... ");
        				this.applyPayloadBefore(instructions, sublist, TER_RENDER_PAYLOAD_TOP);
        				System.out.printf("Successful injection !\n");
        			}
        		} else {
        			System.out.printf("Error while injecting !\n");
        		}
        		
        		match = this.findPattern(methodNode, TER_RENDER_PATTERN_BOTTOM);
        		if (match.size() != 0){
        			for (ArrayList<AbstractInsnNode> sublist : match){
        				System.out.printf("Trying to inject rendering profiler stop... ");
        				this.applyPayloadAfter(instructions, sublist, TER_RENDER_PAYLOAD_BOTTOM);
        				System.out.printf("Successful injection !\n");
        			}
        		} else {
        			System.out.printf("Error while injecting !\n");
        		}        		
        	}
        }
        */
        
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(TER_RENDER)){
        		System.out.printf("[MobiusCore] Found TileEntityRenderer.renderTileEntityAt()... \n");
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadFirst(instructions, TER_RENDER_PAYLOAD_TOP);
        		this.applyPayloadLast(instructions, TER_RENDER_PAYLOAD_BOTTOM);        		
        	}
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}

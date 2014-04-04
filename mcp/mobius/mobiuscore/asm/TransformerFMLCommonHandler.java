package mcp.mobius.mobiuscore.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

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

public class TransformerFMLCommonHandler extends TransformerBase {

	//(Ljava/util/EnumSet<Lcpw/mods/fml/common/TickType;>;Lcpw/mods/fml/relauncher/Side;[Ljava/lang/Object;)V
	private static String FMLCH_TICKSTART;
	private static String FMLCH_TICKEND;	
	private static String FMLCH_ONPRESERVERTICK;
	private static String FMLCH_ONPOSTSERVERTICK;	
	private static String FMLCH_ONPREWORLDTICK;
	private static String FMLCH_ONPOSTWORLDTICK;
	
	private static AbstractInsnNode[] FMLCH_PATTERN_TICKSTART;
	private static AbstractInsnNode[] FMLCH_PATTERN_TICKEND;
	
	private static AbstractInsnNode[] FMLCH_PAYLOAD_TICKSTART_PRE;
	private static AbstractInsnNode[] FMLCH_PAYLOAD_TICKSTART_POST;	
	private static AbstractInsnNode[] FMLCH_PAYLOAD_TICKEND_PRE;
	private static AbstractInsnNode[] FMLCH_PAYLOAD_TICKEND_POST;
	
	private static AbstractInsnNode[] FMLCH_PAYLOAD_PRESERVERTICK;
	private static AbstractInsnNode[] FMLCH_PAYLOAD_POSTSERVERTICK;	
	
	private static AbstractInsnNode[] FMLCH_PAYLOAD_PREWORLDTICK;
	private static AbstractInsnNode[] FMLCH_PAYLOAD_POSTWORLDTICK;		
	
	
	private static boolean isEclipse;
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		FMLCH_TICKSTART = "tickStart (Ljava/util/EnumSet;Lcpw/mods/fml/relauncher/Side;[Ljava/lang/Object;)V";
		FMLCH_TICKEND   = "tickEnd (Ljava/util/EnumSet;Lcpw/mods/fml/relauncher/Side;[Ljava/lang/Object;)V";		
		FMLCH_ONPRESERVERTICK  = "onPreServerTick ()V";
		FMLCH_ONPOSTSERVERTICK = "onPostServerTick ()V";
		
		FMLCH_ONPREWORLDTICK  =  "onPreWorldTick (Ljava/lang/Object;)V";
		FMLCH_ONPOSTWORLDTICK =  "onPostWorldTick (Ljava/lang/Object;)V";
		
		FMLCH_PATTERN_TICKSTART =	new AbstractInsnNode[] 
				{//new LineNumberNode(-1, new LabelNode()), 
				 new VarInsnNode(Opcodes.ALOAD, -1),
				 new VarInsnNode(Opcodes.ALOAD, -1), 
				 new VarInsnNode(Opcodes.ALOAD, -1), 
				 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "cpw/mods/fml/common/IScheduledTickHandler", "tickStart", "(Ljava/util/EnumSet;[Ljava/lang/Object;)V")};		

		FMLCH_PATTERN_TICKEND =	new AbstractInsnNode[] 
				{//new LineNumberNode(-1, new LabelNode()), 
				 new VarInsnNode(Opcodes.ALOAD, -1),
				 new VarInsnNode(Opcodes.ALOAD, -1), 
				 new VarInsnNode(Opcodes.ALOAD, -1), 
				 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "cpw/mods/fml/common/IScheduledTickHandler", "tickEnd", "(Ljava/util/EnumSet;[Ljava/lang/Object;)V")};
		
		FMLCH_PAYLOAD_TICKSTART_PRE = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.HANDLER_TICKSTART.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 6),
				 new VarInsnNode(Opcodes.ALOAD, 7),				 
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;Ljava/lang/Object;)V")};				
		
		FMLCH_PAYLOAD_TICKSTART_POST = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.HANDLER_TICKSTART.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 6),
				 new VarInsnNode(Opcodes.ALOAD, 7),				 
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;Ljava/lang/Object;)V")};		

		FMLCH_PAYLOAD_TICKEND_PRE = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.HANDLER_TICKSTOP.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 6),
				 new VarInsnNode(Opcodes.ALOAD, 7),				 
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;Ljava/lang/Object;)V")};				
		
		FMLCH_PAYLOAD_TICKEND_POST = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.HANDLER_TICKSTOP.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 6),
				 new VarInsnNode(Opcodes.ALOAD, 7),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;Ljava/lang/Object;)V")};	
		
		FMLCH_PAYLOAD_PRESERVERTICK =	new AbstractInsnNode[] 
				{
				 new FieldInsnNode (Opcodes.GETSTATIC,     profilerClass, ProfilerSection.TICK.name(), profilerType),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "()V"),				
				};
		
		FMLCH_PAYLOAD_POSTSERVERTICK =	new AbstractInsnNode[] 
				{
				 new FieldInsnNode (Opcodes.GETSTATIC,     profilerClass, ProfilerSection.TICK.name(), profilerType),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "()V"),				
				};
		
		FMLCH_PAYLOAD_PREWORLDTICK = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.DIMENSION_TICK.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 1),				 
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;)V")};				
		
		FMLCH_PAYLOAD_POSTWORLDTICK = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.DIMENSION_TICK.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 1),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;)V")};			
	}	
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(FMLCH_TICKSTART)){
        		System.out.printf("Found FMLCH.tickStart()... \n");
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		ArrayList<ArrayList<AbstractInsnNode>> match;

        		match = this.findPattern(methodNode, FMLCH_PATTERN_TICKSTART);
        		if (match.size() != 0){
        			for (ArrayList<AbstractInsnNode> sublist : match){
        				System.out.printf("Trying to inject tick profiler... ");
        			
        				this.applyPayloadBefore(instructions, sublist, FMLCH_PAYLOAD_TICKSTART_PRE);
        				this.applyPayloadAfter (instructions, sublist, FMLCH_PAYLOAD_TICKSTART_POST);        			
        			
        				System.out.printf("Successful injection !\n");
        			}
        		} else {
        			System.out.printf("Error while injecting !\n");
        		}
        	}

        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(FMLCH_TICKEND)){
        		System.out.printf("Found FMLCH.tickEnd()... \n");
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		ArrayList<ArrayList<AbstractInsnNode>> match;

        		match = this.findPattern(methodNode, FMLCH_PATTERN_TICKEND);
        		if (match.size() != 0){
        			for (ArrayList<AbstractInsnNode> sublist : match){
	        			System.out.printf("Trying to inject tick profiler... ");
	        			
	        			this.applyPayloadBefore(instructions, sublist, FMLCH_PAYLOAD_TICKEND_PRE);
	        			this.applyPayloadAfter(instructions, sublist, FMLCH_PAYLOAD_TICKEND_POST);        			
	        			
	        			System.out.printf("Successful injection !\n");
        			}
        		} else {
        			System.out.printf("Error while injecting !\n");
        		}
        	} 
        	
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(FMLCH_ONPRESERVERTICK)){
        		System.out.printf("Found FMLCH.onPreServerTick()... \n");
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadFirst(instructions, FMLCH_PAYLOAD_PRESERVERTICK);
        	}
        	
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(FMLCH_ONPOSTSERVERTICK)){
        		System.out.printf("Found FMLCH.onPostServerTick()... \n");
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadLast(instructions, FMLCH_PAYLOAD_POSTSERVERTICK); 

        	}
        	
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(FMLCH_ONPREWORLDTICK)){
        		System.out.printf("Found FMLCH.onPreWorldTick()... \n");
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadFirst(instructions, FMLCH_PAYLOAD_PREWORLDTICK); 

        	}
        	
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(FMLCH_ONPOSTWORLDTICK)){
        		System.out.printf("Found FMLCH.onPostWorldTick()... \n");
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadLast(instructions, FMLCH_PAYLOAD_POSTWORLDTICK); 

        	}        	
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}

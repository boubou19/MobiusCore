package mcp.mobius.mobiuscore.asm.transformers.common;

import java.util.ArrayList;
import java.util.ListIterator;

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

public class TransformerMinecraftServer extends TransformerBase {

	private static String MCSERVER_RUN;

	private static AbstractInsnNode[] MCSERVER_PATTERN_MCSERVER_TICK;

	private static AbstractInsnNode[] MCSERVER_PAYLOAD_TICKSTART;
	private static AbstractInsnNode[] MCSERVER_PAYLOAD_TICKEND;	
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		MCSERVER_RUN = "run ()V";
			
		MCSERVER_PATTERN_MCSERVER_TICK = new AbstractInsnNode[] 
				{ 
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/server/MinecraftServer", "s", "()V"),
				 };
		
		MCSERVER_PAYLOAD_TICKSTART =	new AbstractInsnNode[] 
				{
				 new FieldInsnNode (Opcodes.GETSTATIC,       profilerClass, ProfilerSection.TICK.name(), profilerType),
				 new MethodInsnNode(Opcodes.INVOKEINTERFACE, profilerClass,     "start",    "()V"),				
				};
		
		MCSERVER_PAYLOAD_TICKEND =	new AbstractInsnNode[] 
				{
				 new FieldInsnNode (Opcodes.GETSTATIC,       profilerClass, ProfilerSection.TICK.name(), profilerType),
				 new MethodInsnNode(Opcodes.INVOKEINTERFACE, profilerClass,     "stop",    "()V"),				
				};		
		
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		//log.setLevel(Level.FINEST);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(MCSERVER_RUN)){
        		System.out.printf("Found MinecraftServer.run()... \n");
        		
        		/*
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		ArrayList<ArrayList<AbstractInsnNode>> match;
        		
        		match = this.findPattern(methodNode, MCSERVER_PATTERN_MCSERVER_TICK);
        		if (match.size() != 0){
        			for (ArrayList<AbstractInsnNode> sublist : match){
	        			System.out.printf("Trying to inject IProfilerTick.TickStart()/TickEnd()... ");
	        			
	        			this.applyPayloadBefore(instructions, sublist, MCSERVER_PAYLOAD_TICKSTART);
	        			this.applyPayloadAfter(instructions, sublist, MCSERVER_PAYLOAD_TICKEND);  
	        			
	        			System.out.printf("Successful injection !\n");
        			}
        		} else {
        			System.out.printf("Error while injecting IProfilerTick.TickStart()!\n");
        		}
        		*/
        	}
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        //ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES |ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}

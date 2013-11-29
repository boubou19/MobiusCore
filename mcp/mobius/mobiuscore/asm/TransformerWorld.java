package mcp.mobius.mobiuscore.asm;

import java.util.ArrayList;
import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.LineNumberNode;

public class TransformerWorld extends TransformerBase{

	private static String WORLD_UPDATEENTITIES = "h ()V";
	
    //ALOAD 0
    //ALOAD 2
    //INVOKEVIRTUAL net/minecraft/world/World.updateEntity(Lnet/minecraft/entity/Entity;)V	
	
	//tileentity.updateEntity(); World:2215
	private static AbstractInsnNode[] WORLD_UPDATE_PATTERN_TEUPDATE = 
		{new LineNumberNode(-1, new LabelNode()), 
		 new VarInsnNode(Opcodes.ALOAD, -1), 
		 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "asp", "h", "()V")};

	//ProfilerRegistrar.profilerTileEntity.Start(tileentity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_TEUPDATE = 
		{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerTileEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerTileEntity;"),
		 new VarInsnNode(Opcodes.ALOAD, 8), 
		 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerTileEntity", "Start", "(Lasp;)V")};	

	//ProfilerRegistrar.profilerTileEntity.Stop(tileentity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE = 
		{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerTileEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerTileEntity;"),
		 new VarInsnNode(Opcodes.ALOAD, 8), 
		 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerTileEntity", "Stop", "(Lasp;)V")};		
	
    //this.updateEntity(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PATTERN_ENTUPDATE = 
		{new LineNumberNode(-1, new LabelNode()), 
		 new VarInsnNode(Opcodes.ALOAD, -1),
		 new VarInsnNode(Opcodes.ALOAD, -1), 
	     new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "abw", "g", "(Lnn;)V")};	

	//ProfilerRegistrar.profilerEntity.Start(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_ENTUPDATE = 
		{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
		 new VarInsnNode(Opcodes.ALOAD, 2), 
		 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Start", "(Lnn;)V")};	

	//ProfilerRegistrar.profilerEntity.Stop(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE = 
		{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
		 new VarInsnNode(Opcodes.ALOAD, 2), 
		 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Stop", "(Lnn;)V")};			
	
	//ProfilerRegistrar.profilerEntity.Start(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_ENTUPDATE_MCPC = 
		{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
		 new VarInsnNode(Opcodes.ALOAD, 4),
		 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Start", "(Lnn;)V")};	

	//ProfilerRegistrar.profilerEntity.Stop(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE_MCPC = 
		{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
		 new VarInsnNode(Opcodes.ALOAD, 4),
		 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Stop", "(Lnn;)V")};		
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes){
		//log.setLevel(Level.FINEST);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(WORLD_UPDATEENTITIES)){
        		System.out.printf("Found World.updateEntities()... \n");
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		ArrayList<AbstractInsnNode> match;

        		match = this.findPattern(methodNode, WORLD_UPDATE_PATTERN_TEUPDATE);
        		if (match != null){
        			System.out.printf("Trying to inject tile entity profiler... ");
        			
        			this.applyPayloadBefore(instructions, match, WORLD_UPDATE_PAYLOAD_START_TEUPDATE);
        			this.applyPayloadAfter(instructions, match, WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE);        			
        			
        			System.out.printf("Successful injection !\n");
        		} else {
        			System.out.printf("Error while injecting !\n");
        		}
        		
        		match = this.findPattern(methodNode, WORLD_UPDATE_PATTERN_ENTUPDATE);
        		if (match != null){
        			System.out.printf("Trying to inject entity profiler... ");
        			
        			AbstractInsnNode[] PrevPayload = WORLD_UPDATE_PAYLOAD_START_ENTUPDATE;
        			AbstractInsnNode[] NextPayload = WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE;
        			VarInsnNode aload = (VarInsnNode)(match.get(2));
        			if (aload.var == 4){
        				PrevPayload = WORLD_UPDATE_PAYLOAD_START_ENTUPDATE_MCPC;
        				NextPayload = WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE_MCPC;        				
        			}
        			
        			this.applyPayloadBefore(instructions, match, PrevPayload);
        			this.applyPayloadAfter(instructions, match, NextPayload);
        			
        			System.out.printf("Successful injection !\n");
        		} else {
        			System.out.printf("Error while injecting !\n");
        		}        		
        		
        		
        	}
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}	
	
}

package mcp.mobius.mobiuscore.asm;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
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

	private static String WORLD_UPDATEENTITIES;
	
	//tileentity.updateEntity(); World:2215
	private static AbstractInsnNode[] WORLD_UPDATE_PATTERN_TEUPDATE;

	//ProfilerRegistrar.profilerTileEntity.Start(tileentity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_TEUPDATE;	

	//ProfilerRegistrar.profilerTileEntity.Stop(tileentity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE;	
	
    //this.updateEntity(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PATTERN_ENTUPDATE;	

	//ProfilerRegistrar.profilerEntity.Start(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_ENTUPDATE;	

	//ProfilerRegistrar.profilerEntity.Stop(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE;			
	
	//ProfilerRegistrar.profilerEntity.Start(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_ENTUPDATE_MCPC;

	//ProfilerRegistrar.profilerEntity.Stop(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE_MCPC;
	
	private static boolean isEclipse;
	
	private static HashMap<String, String> obfTable = new HashMap<String, String>();
	
	static{
		isEclipse = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
		
		obfTable.put("h ()V"   , "updateEntities ()V");
		obfTable.put("asp"     , "net/minecraft/tileentity/TileEntity");
		obfTable.put("h"       , "updateEntity");
		obfTable.put("g"       , "updateEntity");
		obfTable.put("(Lasp;)V", "(Lnet/minecraft/tileentity/TileEntity;)V");
		obfTable.put("(Lnn;)V" , "(Lnet/minecraft/entity/Entity;)V");
		obfTable.put("abw"     , "net/minecraft/world/World");
		
		WORLD_UPDATEENTITIES = getCorrectName("h ()V");
		
		WORLD_UPDATE_PATTERN_TEUPDATE =	new AbstractInsnNode[] 
			{new LineNumberNode(-1, new LabelNode()), 
			 new VarInsnNode(Opcodes.ALOAD, -1), 
			 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, getCorrectName("asp"), getCorrectName("h"), "()V")};

		WORLD_UPDATE_PAYLOAD_START_TEUPDATE = new AbstractInsnNode[]
			{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerTileEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerTileEntity;"),
			 new VarInsnNode(Opcodes.ALOAD, 8), 
			 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerTileEntity", "Start", getCorrectName("(Lasp;)V"))};	

		WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE = new AbstractInsnNode[]
			{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerTileEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerTileEntity;"),
			 new VarInsnNode(Opcodes.ALOAD, 8), 
			 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerTileEntity", "Stop", getCorrectName("(Lasp;)V"))};		
		
		WORLD_UPDATE_PATTERN_ENTUPDATE = new AbstractInsnNode[]
			{new LineNumberNode(-1, new LabelNode()), 
			 new VarInsnNode(Opcodes.ALOAD, -1),
			 new VarInsnNode(Opcodes.ALOAD, -1), 
		     new MethodInsnNode(Opcodes.INVOKEVIRTUAL, getCorrectName("abw"), getCorrectName("g"), getCorrectName("(Lnn;)V"))};	

		WORLD_UPDATE_PAYLOAD_START_ENTUPDATE = new AbstractInsnNode[]
			{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
			 new VarInsnNode(Opcodes.ALOAD, 2), 
			 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Start", getCorrectName("(Lnn;)V"))};	

		WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE = new AbstractInsnNode[]
			{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
			 new VarInsnNode(Opcodes.ALOAD, 2), 
			 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Stop", getCorrectName("(Lnn;)V"))};			
		
		WORLD_UPDATE_PAYLOAD_START_ENTUPDATE_MCPC = new AbstractInsnNode[]
			{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
			 new VarInsnNode(Opcodes.ALOAD, 4),
			 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Start", getCorrectName("(Lnn;)V"))};	

		WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE_MCPC = new AbstractInsnNode[]
			{new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
			 new VarInsnNode(Opcodes.ALOAD, 4),
			 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Stop", getCorrectName("(Lnn;)V"))};			
		
	}
	
	private static String getCorrectName(String key){
		if (isEclipse)
			return obfTable.get(key);
		else
			return key;
	}
	
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

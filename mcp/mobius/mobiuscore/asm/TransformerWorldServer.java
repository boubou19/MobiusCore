package mcp.mobius.mobiuscore.asm;

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

public class TransformerWorldServer extends TransformerBase {

	private static String WORLDSERVER_TICK;
	
	private static AbstractInsnNode[] WORLDSERVER_PAYLOAD_TICKSTART;
	private static AbstractInsnNode[] WORLDSERVER_PAYLOAD_TICKEND;
	
	private static boolean isEclipse;
	
	static{
		WORLDSERVER_TICK = "b ()V";

		/*
		WORLDSERVER_PAYLOAD_TICKSTART =	new AbstractInsnNode[] 
				{
				 new FieldInsnNode (Opcodes.GETSTATIC,       "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerWorldTick", "Lmcp/mobius/mobiuscore/profiler/IProfilerWorldTick;"),
				 new VarInsnNode   (Opcodes.ALOAD, 0),	
				 new FieldInsnNode (Opcodes.GETFIELD,        "net/minecraft/world/WorldServer", "provider", "Lnet/minecraft/world/WorldProvider;"),
				 new FieldInsnNode (Opcodes.GETFIELD,        "net/minecraft/world/WorldProvider", "dimensionId", "I"),
				 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerWorldTick",     "WorldTickStart",    "(I)V"),				 
				};
		*/		

		WORLDSERVER_PAYLOAD_TICKSTART =	new AbstractInsnNode[] 
				{
				 new FieldInsnNode (Opcodes.GETSTATIC,       "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerWorldTick", "Lmcp/mobius/mobiuscore/profiler/IProfilerWorldTick;"),
				 new VarInsnNode   (Opcodes.ALOAD, 0),	
				 new FieldInsnNode (Opcodes.GETFIELD,        "js", "t", "Laei;"),
				 new FieldInsnNode (Opcodes.GETFIELD,        "aei", "i", "I"),
				 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerWorldTick",     "WorldTickStart",    "(I)V"),				 
				};		

		WORLDSERVER_PAYLOAD_TICKEND =	new AbstractInsnNode[] 
				{
				 new FieldInsnNode (Opcodes.GETSTATIC,       "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerWorldTick", "Lmcp/mobius/mobiuscore/profiler/IProfilerWorldTick;"),
				 new VarInsnNode   (Opcodes.ALOAD, 0),	
				 new FieldInsnNode (Opcodes.GETFIELD,        "js", "t", "Laei;"),
				 new FieldInsnNode (Opcodes.GETFIELD,        "aei", "i", "I"),
				 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerWorldTick", "WorldTickEnd",    "(I)V"),				 
				};			
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		//log.setLevel(Level.FINEST);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(WORLDSERVER_TICK)){
        		System.out.printf("Found WorldServer.tick()... \n");
        		
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadFirst(instructions, WORLDSERVER_PAYLOAD_TICKSTART);
        		this.applyPayloadLast(instructions,  WORLDSERVER_PAYLOAD_TICKEND);
        	}
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES |ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}

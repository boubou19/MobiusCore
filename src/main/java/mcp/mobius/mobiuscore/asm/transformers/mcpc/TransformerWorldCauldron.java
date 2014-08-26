package mcp.mobius.mobiuscore.asm.transformers.mcpc;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import mcp.mobius.mobiuscore.asm.ObfTable;
import mcp.mobius.mobiuscore.asm.Opcode;
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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.LineNumberNode;

public class TransformerWorldCauldron extends TransformerBase{

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
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		WORLD_UPDATEENTITIES = ObfTable.WORLD_UPDATEENTITIES.getFullDescriptor(); //updateEntities.getFullDescriptor();
		
		WORLD_UPDATE_PATTERN_TEUPDATE =	new AbstractInsnNode[] 
			{
			new LineNumberNode(-1, new LabelNode()), 
			Opcode.ALOAD(-1), 
			Opcode.INVOKEVIRTUAL(ObfTable.TILEENTITY_UPDATEENTITY.getClazz(), ObfTable.TILEENTITY_UPDATEENTITY.getName(), ObfTable.TILEENTITY_UPDATEENTITY.getDescriptor())
			};

		WORLD_UPDATE_PAYLOAD_START_TEUPDATE = new AbstractInsnNode[]
			{
			Opcode.GETSTATIC(profilerClass, ProfilerSection.TILEENT_UPDATETIME.name(), profilerType),
			Opcode.ALOAD(8), 
			Opcode.INVOKEVIRTUAL(profilerClass, "start", "(Ljava/lang/Object;)V")
			};	

		WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE = new AbstractInsnNode[]
			{
			Opcode.GETSTATIC(profilerClass, ProfilerSection.TILEENT_UPDATETIME.name(), profilerType),
			Opcode.ALOAD(8), 
			Opcode.INVOKEVIRTUAL(profilerClass, "stop", "(Ljava/lang/Object;)V")
			};		
		
		WORLD_UPDATE_PATTERN_ENTUPDATE = new AbstractInsnNode[]
			{
			new LineNumberNode(-1, new LabelNode()), 
			Opcode.ALOAD(-1),
			Opcode.ALOAD(-1), 
		    Opcode.INVOKEVIRTUAL(ObfTable.WORLD_UPDATEENTITY.getClazz(), ObfTable.WORLD_UPDATEENTITY.getName(), ObfTable.WORLD_UPDATEENTITY.getDescriptor())
		    };	

		WORLD_UPDATE_PAYLOAD_START_ENTUPDATE = new AbstractInsnNode[]
			{
			Opcode.GETSTATIC(profilerClass, ProfilerSection.ENTITY_UPDATETIME.name(), profilerType),
			Opcode.ALOAD(2), 
			Opcode.INVOKEVIRTUAL(profilerClass, "start", "(Ljava/lang/Object;)V")
			};	

		WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE = new AbstractInsnNode[]
			{
			Opcode.GETSTATIC(profilerClass, ProfilerSection.ENTITY_UPDATETIME.name(), profilerType),
			Opcode.ALOAD(2), 
			Opcode.INVOKEVIRTUAL(profilerClass, "stop", "(Ljava/lang/Object;)V")
			};			
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes){
		this.dumpChecksum(bytes, name, srgname);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        MethodNode updateEntitiesNode = this.getMethod(classNode, WORLD_UPDATEENTITIES);
		System.out.printf("[MobiusCore] Found World.updateEntities()... \n");
		
		this.applyPayloadBefore(updateEntitiesNode, WORLD_UPDATE_PATTERN_TEUPDATE, WORLD_UPDATE_PAYLOAD_START_TEUPDATE);
		this.applyPayloadAfter (updateEntitiesNode, WORLD_UPDATE_PATTERN_TEUPDATE, WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE);

		this.applyPayloadBefore(updateEntitiesNode, WORLD_UPDATE_PATTERN_ENTUPDATE, WORLD_UPDATE_PAYLOAD_START_ENTUPDATE);
		this.applyPayloadAfter (updateEntitiesNode, WORLD_UPDATE_PATTERN_ENTUPDATE, WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE);		
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        
        return writer.toByteArray();
	}	
	
}

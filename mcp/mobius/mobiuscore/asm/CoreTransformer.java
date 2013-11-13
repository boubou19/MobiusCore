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

import net.minecraft.launchwrapper.IClassTransformer;

public class CoreTransformer implements IClassTransformer {

	private static String WORLD_UPDATEENTITIES = "h ()V";
	
    //ALOAD 0
    //ALOAD 2
    //INVOKEVIRTUAL net/minecraft/world/World.updateEntity(Lnet/minecraft/entity/Entity;)V	
	
	//tileentity.updateEntity(); World:2215
	private static AbstractInsnNode[] WORLD_UPDATE_PATTERN_TEUPDATE = {new LineNumberNode(-1, new LabelNode()), 
		                                                       		   new VarInsnNode(Opcodes.ALOAD, -1), 
		                                                       		   new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "asp", "h", "()V")};

	//ProfilerRegistrar.profilerTileEntity.Start(tileentity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_TEUPDATE = {new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerTileEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerTileEntity;"),
        													                 new VarInsnNode(Opcodes.ALOAD, 8), 
        													                 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerTileEntity", "Start", "(Lasp;)V")};	

	//ProfilerRegistrar.profilerTileEntity.Stop(tileentity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_TEUPDATE = {new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerTileEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerTileEntity;"),
        															        new VarInsnNode(Opcodes.ALOAD, 8), 
        															        new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerTileEntity", "Stop", "(Lasp;)V")};		
	
    //this.updateEntity(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PATTERN_ENTUPDATE = {new LineNumberNode(-1, new LabelNode()), 
		   																new VarInsnNode(Opcodes.ALOAD, -1),
		   																new VarInsnNode(Opcodes.ALOAD, -1), 
		   																new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "abw", "g", "(Lnn;)V")};	

	//ProfilerRegistrar.profilerEntity.Start(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_ENTUPDATE = {new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
        													                 new VarInsnNode(Opcodes.ALOAD, 2), 
        													                 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Start", "(Lnn;)V")};	

	//ProfilerRegistrar.profilerEntity.Stop(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE = {new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
        															        new VarInsnNode(Opcodes.ALOAD, 2), 
        															        new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Stop", "(Lnn;)V")};			
	
	//ProfilerRegistrar.profilerEntity.Start(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_START_ENTUPDATE_MCPC = {new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
																			 new VarInsnNode(Opcodes.ALOAD, 4),
        													                 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Start", "(Lnn;)V")};	

	//ProfilerRegistrar.profilerEntity.Stop(entity);
	private static AbstractInsnNode[] WORLD_UPDATE_PAYLOAD_STOP_ENTUPDATE_MCPC = {new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerEntity;"),
																			new VarInsnNode(Opcodes.ALOAD, 4),
        															        new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerEntity", "Stop", "(Lnn;)V")};		
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		if (srgname.equals("net.minecraft.world.World"))
			return transformWorld(bytes);

		return bytes;
	}

	private ArrayList<AbstractInsnNode> findPattern(MethodNode methodNode, AbstractInsnNode... pattern){
		InsnList instructions = methodNode.instructions;
		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
		ArrayList<AbstractInsnNode> returnList = new ArrayList<AbstractInsnNode>(); 
		
		while (iterator.hasNext()){
			boolean match = true;
			returnList.clear();
			
			for (int i = 0; i < pattern.length; i++){
				AbstractInsnNode insnNode = iterator.next();
				match = match && this.areInsnEqual(insnNode, pattern[i]);
				if (match)
					returnList.add(insnNode);
				else
					break;
			}
			
			if (match){
				System.out.printf("Pattern found ... ");
				return returnList;
			}
		}
		
		return returnList;
	}
	
	private boolean areInsnEqual(AbstractInsnNode insn1, AbstractInsnNode insn2){
		if (!insn1.getClass().equals(insn2.getClass())) return false;
		if (insn1.getOpcode() != insn2.getOpcode())     return false;
		
		if (insn1 instanceof LineNumberNode){
			if ((((LineNumberNode)insn2).line == -1) || (((LineNumberNode)insn2).line == -1)) return true;
			if (((LineNumberNode)insn2).line  != ((LineNumberNode)insn2).line) return false;
		}
			
		if (insn1 instanceof VarInsnNode){
			if ((((VarInsnNode)insn2).var == -1) || (((VarInsnNode)insn2).var == -1)) return true;
			if (((VarInsnNode)insn2).var  != ((VarInsnNode)insn2).var) return false;
		}

		if (insn1 instanceof MethodInsnNode){
			if ((((MethodInsnNode)insn1).owner.equals(((MethodInsnNode)insn2).owner)) &&
			    (((MethodInsnNode)insn1).name.equals(((MethodInsnNode)insn2).name))  &&
			    (((MethodInsnNode)insn1).desc.equals(((MethodInsnNode)insn2).desc)))
				return true;
		}
		
		return false;
		
	}
	
	private void printInsnNode (AbstractInsnNode insnNode){
		switch(insnNode.getType()){
		case AbstractInsnNode.LINE:
			System.out.printf("LINE : %s %s\n", ((LineNumberNode)insnNode).getOpcode(), ((LineNumberNode)insnNode).line);
			break;
			
		case AbstractInsnNode.VAR_INSN:
			System.out.printf("VAR_INSN : %s %s\n", ((VarInsnNode)insnNode).getOpcode(), ((VarInsnNode)insnNode).var);
			break;

		case AbstractInsnNode.METHOD_INSN:
			System.out.printf("METHOD_INSN : %s %s %s %s\n", ((MethodInsnNode)insnNode).getOpcode(),  ((MethodInsnNode)insnNode).owner, ((MethodInsnNode)insnNode).name, ((MethodInsnNode)insnNode).desc);
			break;        					
			
		default:
			System.out.printf("%s\n", insnNode);
			break;
		}		
	}
	
	private byte[] transformWorld(byte[] bytes){
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
	
	private void applyPayloadAfter(InsnList instructions, ArrayList<AbstractInsnNode> match,  AbstractInsnNode[] payload_pattern){
		InsnList payload = new InsnList();
		for (int i = 0; i < payload_pattern.length; i++)
			payload.add(payload_pattern[i]);
	
		instructions.insert(match.get(match.size() - 1), payload);		
	}
	
	private void applyPayloadBefore(InsnList instructions, ArrayList<AbstractInsnNode> match,  AbstractInsnNode[] payload_pattern){
		InsnList payload = new InsnList();
		for (int i = 0; i < payload_pattern.length; i++)
			payload.add(payload_pattern[i]);
	
		instructions.insertBefore(match.get(0), payload);		
	}	
}

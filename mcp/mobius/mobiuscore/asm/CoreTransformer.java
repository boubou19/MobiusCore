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
	private static AbstractInsnNode[] WORLD_UPDATEENTITIES_PATTERN1 = {new LineNumberNode(-1, new LabelNode()), 
		                                                       		   new VarInsnNode(Opcodes.ALOAD, -1), 
		                                                       		   new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "asp", "h", "()V")};

	private static AbstractInsnNode[] WORLD_UPDATEENTITIES_PAYLOAD1 = {new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerTileEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerTileEntity;"),
        													           new VarInsnNode(Opcodes.ALOAD, 8), 
        													           new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerTileEntity", "Start", "(Lasp;)V")};	

	private static AbstractInsnNode[] WORLD_UPDATEENTITIES_PAYLOAD2 = {new FieldInsnNode(Opcodes.GETSTATIC, "mcp/mobius/mobiuscore/profiler/ProfilerRegistrar", "profilerTileEntity", "Lmcp/mobius/mobiuscore/profiler/IProfilerTileEntity;"),
        															   new VarInsnNode(Opcodes.ALOAD, 8), 
        															   new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mcp/mobius/mobiuscore/profiler/IProfilerTileEntity", "Stop", "(Lasp;)V")};		
	
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
        		System.out.printf("Found World.updateEntities()... ");
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		
        		ArrayList<AbstractInsnNode> match = this.findPattern(methodNode, WORLD_UPDATEENTITIES_PATTERN1);
        		
        		if (match != null){
        		
        			InsnList payload = new InsnList();
        			for (int i = 0; i < WORLD_UPDATEENTITIES_PAYLOAD1.length; i++)
        				payload.add(WORLD_UPDATEENTITIES_PAYLOAD1[i]);
        		
        			instructions.insertBefore(match.get(0), payload);

        			payload.clear();
        			for (int i = 0; i < WORLD_UPDATEENTITIES_PAYLOAD2.length; i++)
        				payload.add(WORLD_UPDATEENTITIES_PAYLOAD2[i]);
        		
        			instructions.insert(match.get(match.size() - 1), payload);
        			
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

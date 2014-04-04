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

public class TransformerRenderManager extends TransformerBase {

	private static String RM_RENDERENT;
	
	private static AbstractInsnNode[] RM_RENDER_PAYLOAD_TOP;
	private static AbstractInsnNode[] RM_RENDER_PAYLOAD_BOTTOM;
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		RM_RENDERENT = "a (Lnn;F)V";
		
		RM_RENDER_PAYLOAD_TOP = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.RENDER_ENTITY.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 1),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;)V")};				
		
		RM_RENDER_PAYLOAD_BOTTOM = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.RENDER_ENTITY.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 1),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;)V")};			
		
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(RM_RENDERENT)){
        		System.out.printf("Found RenderManager.renderEntity()... \n");
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadFirst(instructions, RM_RENDER_PAYLOAD_TOP);
        		this.applyPayloadLast(instructions, RM_RENDER_PAYLOAD_BOTTOM);        		
        	}
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}

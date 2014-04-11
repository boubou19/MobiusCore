package mcp.mobius.mobiuscore.asm;

import java.util.ArrayList;
import java.util.ListIterator;

import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import static org.objectweb.asm.Opcodes.*;
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

public class TransformerASMEventHandler extends TransformerBase {

	private static String ASMEH_INVOKE;
	private static String ASMEH_INIT;	
	
	private static AbstractInsnNode[] ASMEH_INVOKE_PATTERN;
	
	private static AbstractInsnNode[] ASMEH_INVOKE_PAYLOAD_PRE;
	private static AbstractInsnNode[] ASMEH_INVOKE_PAYLOAD_POST;	

	private static AbstractInsnNode[] ASMEH_INIT_PAYLOAD_PRE;
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		ASMEH_INVOKE = "invoke (Lnet/minecraftforge/event/Event;)V";
		ASMEH_INIT = "<init> (Ljava/lang/Object;Ljava/lang/reflect/Method;)V";
		
		ASMEH_INVOKE_PATTERN =	new AbstractInsnNode[] 
				{//new LineNumberNode(-1, new LabelNode()), 
				 new VarInsnNode(Opcodes.ALOAD, -1),
				 new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/event/ASMEventHandler", "handler", "Lnet/minecraftforge/event/IEventListener;"), 
				 new VarInsnNode(Opcodes.ALOAD, -1), 
				 new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/event/IEventListener", "invoke", "(Lnet/minecraftforge/event/Event;)V")};		

		
		ASMEH_INIT_PAYLOAD_PRE = new AbstractInsnNode[]
				{
				new VarInsnNode(Opcodes.ALOAD, 0),
				new VarInsnNode(Opcodes.ALOAD, 2),
				new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "getDeclaringClass", "()Ljava/lang/Class;"),
				new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getCanonicalName", "()Ljava/lang/String;"),
				new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/event/ASMEventHandler", "package_", "Ljava/lang/String;"), 
				};

		
		ASMEH_INVOKE_PAYLOAD_PRE = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.EVENT_INVOKE.name(), profilerType),
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "()V")};				
		
		ASMEH_INVOKE_PAYLOAD_POST = new AbstractInsnNode[]
				{new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.EVENT_INVOKE.name(), profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 1),
				 new VarInsnNode(Opcodes.ALOAD, 0),
				 new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/event/ASMEventHandler", "package_", "Ljava/lang/String;"),				 
				 new VarInsnNode(Opcodes.ALOAD, 0),
				 new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/event/ASMEventHandler", "handler", "Lnet/minecraftforge/event/IEventListener;"),				 
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "stop", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V")};		

		
	}		
	
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        
        
        for (MethodNode methodNode : classNode.methods){
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(ASMEH_INIT)){
        		System.out.printf("[MobiusCore] Found ASMEH.<init>()... \n");
        		InsnList instructions = methodNode.instructions;
        		this.applyPayloadFirst(instructions, ASMEH_INIT_PAYLOAD_PRE);
        	}
        	
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(ASMEH_INVOKE)){
        		System.out.printf("[MobiusCore] Found ASMEH.invoke()... \n");
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		ArrayList<ArrayList<AbstractInsnNode>> match;

        		match = this.findPattern(methodNode, ASMEH_INVOKE_PATTERN);
        		if (match.size() != 0){
        			for (ArrayList<AbstractInsnNode> sublist : match){
        				System.out.printf("[MobiusCore] Trying to inject event profiler... ");
        			
        				this.applyPayloadBefore(instructions, sublist, ASMEH_INVOKE_PAYLOAD_PRE);
        				this.applyPayloadAfter (instructions, sublist, ASMEH_INVOKE_PAYLOAD_POST);        			
        			
        				System.out.printf("Successful injection !\n");
        			}
        		} else {
        			System.out.printf("Error while injecting !\n");
        		}
        	}
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        
        writer.visitField(ACC_PRIVATE + ACC_FINAL, "package_", "Ljava/lang/String;", null, null);
        
        return writer.toByteArray();        
	}

}

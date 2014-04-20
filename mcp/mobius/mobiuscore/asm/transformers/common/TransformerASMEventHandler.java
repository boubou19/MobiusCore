package mcp.mobius.mobiuscore.asm.transformers.common;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.ListIterator;

import mcp.mobius.mobiuscore.asm.Opcode;
import mcp.mobius.mobiuscore.asm.transformers.TransformerBase;
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

	/* SHOULD WORK BOTH SIDES */
	
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
				{ 
				Opcode.ALOAD(-1),
				Opcode.GETFIELD("net/minecraftforge/event/ASMEventHandler.handler Lnet/minecraftforge/event/IEventListener;"),
				Opcode.ALOAD(-1), 
				Opcode.INVOKEINTERFACE("net/minecraftforge/event/IEventListener.invoke (Lnet/minecraftforge/event/Event;)V")
				};		

		
		ASMEH_INIT_PAYLOAD_PRE = new AbstractInsnNode[]
				{
				Opcode.ALOAD(0),
				Opcode.ALOAD(2),
				Opcode.INVOKEVIRTUAL("java/lang/reflect/Method.getDeclaringClass ()Ljava/lang/Class;"),
				Opcode.INVOKEVIRTUAL("java/lang/Class.getCanonicalName ()Ljava/lang/String;"),
				Opcode.PUTFIELD("net/minecraftforge/event/ASMEventHandler.package_ Ljava/lang/String;"), 
				};

		
		ASMEH_INVOKE_PAYLOAD_PRE = new AbstractInsnNode[]
				{
				Opcode.GETSTATIC(profilerClass, ProfilerSection.EVENT_INVOKE.name(), profilerType),
				Opcode.INVOKEVIRTUAL(profilerClass, "start", "()V")
				};				
		
		ASMEH_INVOKE_PAYLOAD_POST = new AbstractInsnNode[]
				{
				Opcode.GETSTATIC(profilerClass, ProfilerSection.EVENT_INVOKE.name(), profilerType),
				Opcode.ALOAD(1),
				Opcode.ALOAD(0),
				Opcode.GETFIELD("net/minecraftforge/event/ASMEventHandler.package_ Ljava/lang/String;"),				
				Opcode.ALOAD(0),
				Opcode.GETFIELD("net/minecraftforge/event/ASMEventHandler.handler Lnet/minecraftforge/event/IEventListener;"),				 
				Opcode.INVOKEVIRTUAL(profilerClass, "stop", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V")
				};		
	}		
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		this.dumpChecksum(bytes, name, srgname);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        classReader.accept(classNode, 0);
		
        MethodNode initNode   = this.getMethod(classNode, ASMEH_INIT);
        this.applyPayloadFirst(initNode, ASMEH_INIT_PAYLOAD_PRE);
        
        MethodNode invokeNode = this.getMethod(classNode, ASMEH_INVOKE);
		this.applyPayloadBefore(invokeNode, ASMEH_INVOKE_PATTERN, ASMEH_INVOKE_PAYLOAD_PRE);
		this.applyPayloadAfter (invokeNode, ASMEH_INVOKE_PATTERN, ASMEH_INVOKE_PAYLOAD_POST);         
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        
        writer.visitField(ACC_PRIVATE + ACC_FINAL, "package_", "Ljava/lang/String;", null, null);
        
        return writer.toByteArray();        
	}

}

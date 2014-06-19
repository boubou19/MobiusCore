package mcp.mobius.mobiuscore.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class Opcode {

	public static InsnNode NOP(){
		return new InsnNode(Opcodes.NOP);
	}	

	public static InsnNode DUP(){
		return new InsnNode(Opcodes.DUP);
	}		
	
	public static InsnNode DUP_X1(){
		return new InsnNode(Opcodes.DUP_X1);
	}

	public static InsnNode DUP_X2(){
		return new InsnNode(Opcodes.DUP_X2);
	}	

	public static InsnNode DUP2(){
		return new InsnNode(Opcodes.DUP2);
	}	
	
	public static InsnNode SWAP(){
		return new InsnNode(Opcodes.SWAP);
	}	
	
	public static InsnNode POP(){
		return new InsnNode(Opcodes.POP);
	}	
		
	
	public static VarInsnNode ALOAD(int val){
		return new VarInsnNode(Opcodes.ALOAD, val);
	}
	
	public static FieldInsnNode GETFIELD(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new FieldInsnNode(Opcodes.GETFIELD, clazz, field, sig);
	}
	
	public static FieldInsnNode GETFIELD(String clazz, String field, String sig){
		return new FieldInsnNode(Opcodes.GETFIELD, clazz, field, sig);
	}	
	
	public static FieldInsnNode PUTFIELD(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new FieldInsnNode(Opcodes.PUTFIELD, clazz, field, sig);
	}
	
	public static FieldInsnNode PUTFIELD(String clazz, String field, String sig){
		return new FieldInsnNode(Opcodes.PUTFIELD, clazz, field, sig);
	}		
	
	public static FieldInsnNode GETSTATIC(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new FieldInsnNode(Opcodes.GETSTATIC, clazz, field, sig);
	}	
	
	public static FieldInsnNode GETSTATIC(String clazz, String field, String sig){
		return new FieldInsnNode(Opcodes.GETSTATIC, clazz, field, sig);
	}	
	
	public static MethodInsnNode INVOKEINTERFACE(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new MethodInsnNode(Opcodes.INVOKEINTERFACE, clazz, field, sig);		
	}
	
	public static MethodInsnNode INVOKEINTERFACE(String clazz, String field, String sig){
		return new MethodInsnNode(Opcodes.INVOKEINTERFACE, clazz, field, sig);
	}
	
	public static MethodInsnNode INVOKEVIRTUAL(String name){
		String clazz = name.split("\\.")[0];
		String field = name.split("\\.")[1].split(" ")[0];
		String sig   = name.split("\\.")[1].split(" ")[1];
		return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, clazz, field, sig);		
	}
	
	public static MethodInsnNode INVOKEVIRTUAL(String clazz, String field, String sig){
		return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, clazz, field, sig);
	}	
}

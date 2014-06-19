package mcp.mobius.mobiuscore.asm.transformers.common;

import java.util.ArrayList;
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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformerTcpConnection extends TransformerBase {

	private static String TCPCON_READPACKET;
	private static String TCPCON_SENDPACKET;	

	private static AbstractInsnNode[] TCPCON_PATTERN_FLAGSET;
	private static AbstractInsnNode[] TCPCON_PATTERN_INPACKET;		
	private static AbstractInsnNode[] TCPCON_PATTERN_OUTPACKET;	
	private static AbstractInsnNode[] TCPCON_PAYLOAD_INPACKET;
	private static AbstractInsnNode[] TCPCON_PAYLOAD_OUTPACKET;	
	
	static{
		String profilerClass =  ProfilerSection.getClassName();
		String profilerType  =  ProfilerSection.getTypeName();
		
		TCPCON_READPACKET   = ObfTable.TCPCONN_READPACKET.getFullDescriptor();
		TCPCON_SENDPACKET   = ObfTable.TCPCONN_SENDPACKET.getFullDescriptor();		
	
		TCPCON_PATTERN_OUTPACKET =	new AbstractInsnNode[]{
				Opcode.ALOAD(-1),
				Opcode.ALOAD(-1),
				new InsnNode(Opcodes.MONITOREXIT)
				};
		
		TCPCON_PATTERN_INPACKET =	new AbstractInsnNode[]{
				new FieldInsnNode(Opcodes.GETFIELD,      ObfTable.TCPCONN_NETWORKSOCKET.getClazz(), ObfTable.TCPCONN_NETWORKSOCKET.getName(), ObfTable.TCPCONN_NETWORKSOCKET.getDescriptor()),
				new MethodInsnNode(Opcodes.INVOKESTATIC, ObfTable.PACKET_READPACKET.getClazz(),     ObfTable.PACKET_READPACKET.getName(),     ObfTable.PACKET_READPACKET.getDescriptor()),
				new VarInsnNode(Opcodes.ASTORE, -1)
				};		
		
		TCPCON_PATTERN_FLAGSET =	new AbstractInsnNode[]{ 
				 new InsnNode(Opcodes.ICONST_1),
				 new VarInsnNode(Opcodes.ISTORE, 1)
				 };		
		
		TCPCON_PAYLOAD_INPACKET =	new AbstractInsnNode[]{ 
				 Opcode.GETSTATIC(profilerClass, ProfilerSection.PACKET_INBOUND.name() , profilerType),
				 Opcode.ALOAD(2), 
				 Opcode.INVOKEVIRTUAL(profilerClass, "start", "(Ljava/lang/Object;)V")};
		
		TCPCON_PAYLOAD_OUTPACKET = new AbstractInsnNode[]{
				 Opcode.GETSTATIC(profilerClass, ProfilerSection.PACKET_OUTBOUND.name() , profilerType),
				 Opcode.ALOAD(2), 
				 Opcode.INVOKEVIRTUAL(profilerClass, "start", "(Ljava/lang/Object;)V")};				
	}	
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		this.dumpChecksum(bytes, name, srgname);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        System.out.printf("Found TcpConnection. Starting injection\n");
        
        classReader.accept(classNode, 0);
		
        MethodNode readPacketNode  = this.getMethod(classNode, TCPCON_READPACKET);
        if (this.checkPreviousInjection(readPacketNode)) return bytes;           
        this.applyPayloadAfter(readPacketNode, TCPCON_PATTERN_INPACKET, TCPCON_PAYLOAD_INPACKET);
        
        MethodNode sendPacketNode  = this.getMethod(classNode, TCPCON_SENDPACKET);
        this.applyPayloadBefore(sendPacketNode, TCPCON_PATTERN_OUTPACKET, TCPCON_PAYLOAD_OUTPACKET);         
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}

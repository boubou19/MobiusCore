package mcp.mobius.mobiuscore.asm.transformers.common;

import java.util.ArrayList;
import java.util.ListIterator;

import mcp.mobius.mobiuscore.asm.ObfTable;
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

		/*
		TCPCON_PATTERN_OUTPACKET =	new AbstractInsnNode[]{
				new VarInsnNode(Opcodes.ALOAD, -1), 
				new VarInsnNode(Opcodes.ALOAD, -1),
				new FieldInsnNode(Opcodes.GETFIELD, "co", "m", "Ljava/io/DataOutputStream;"),
				new MethodInsnNode(Opcodes.INVOKEINTERFACE, "ey", "a", "(Ley;Ljava/io/DataOutput;)V")
				};
		*/			

		/*
		TCPCON_PATTERN_OUTPACKET =	new AbstractInsnNode[]{
				new FieldInsnNode(Opcodes.GETSTATIC, "co", "d", "[I"),
				new VarInsnNode(Opcodes.ASTORE, -1)
				};
		*/
		
		TCPCON_PATTERN_OUTPACKET =	new AbstractInsnNode[]{
				new VarInsnNode(Opcodes.ALOAD, -1),
				new VarInsnNode(Opcodes.ALOAD, -1),
				new InsnNode(Opcodes.MONITOREXIT)
				};
		
		TCPCON_PATTERN_INPACKET =	new AbstractInsnNode[]{
				new FieldInsnNode(Opcodes.GETFIELD, ObfTable.TCPCONN_NETWORKSOCKET.getClazz(), ObfTable.TCPCONN_NETWORKSOCKET.getName(), ObfTable.TCPCONN_NETWORKSOCKET.getDescriptor()),
				new MethodInsnNode(Opcodes.INVOKESTATIC, ObfTable.PACKET_READPACKET.getClazz(), ObfTable.PACKET_READPACKET.getName(), ObfTable.PACKET_READPACKET.getDescriptor()),
				new VarInsnNode(Opcodes.ASTORE, -1)
				};		
		
	    //GETFIELD net/minecraft/network/TcpConnection.networkSocket : Ljava/net/Socket;
	    //INVOKESTATIC net/minecraft/network/packet/Packet.readPacket(Lnet/minecraft/logging/ILogAgent;Ljava/io/DataInput;ZLjava/net/Socket;)Lnet/minecraft/network/packet/Packet;
	    //ASTORE 2	
		
		TCPCON_PATTERN_FLAGSET =	new AbstractInsnNode[]{ 
				 new InsnNode(Opcodes.ICONST_1),
				 new VarInsnNode(Opcodes.ISTORE, 1)
				 };		
		
		TCPCON_PAYLOAD_INPACKET =	new AbstractInsnNode[]{ 
				 new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.PACKET_INBOUND.name() , profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 2), 
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;)V")};
		
		TCPCON_PAYLOAD_OUTPACKET = new AbstractInsnNode[]{
				 new FieldInsnNode(Opcodes.GETSTATIC, profilerClass, ProfilerSection.PACKET_OUTBOUND.name() , profilerType),
				 new VarInsnNode(Opcodes.ALOAD, 2), 
				 new MethodInsnNode(Opcodes.INVOKEVIRTUAL, profilerClass, "start", "(Ljava/lang/Object;)V")};				
	}	
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		this.dumpChecksum(bytes, name, srgname);
		
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        System.out.printf("Found TcpConnection. Starting injection\n");
        
        classReader.accept(classNode, 0);
		
        MethodNode readPacketNode  = this.getMethod(classNode, TCPCON_READPACKET);
        this.applyPayloadAfter(readPacketNode, TCPCON_PATTERN_INPACKET, TCPCON_PAYLOAD_INPACKET);
        
        MethodNode sendPacketNode  = this.getMethod(classNode, TCPCON_SENDPACKET);
        this.applyPayloadBefore(sendPacketNode, TCPCON_PATTERN_OUTPACKET, TCPCON_PAYLOAD_OUTPACKET);         
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}

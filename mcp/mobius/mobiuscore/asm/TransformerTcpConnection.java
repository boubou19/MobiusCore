package mcp.mobius.mobiuscore.asm;

import java.util.ArrayList;
import java.util.ListIterator;

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
		
		TCPCON_READPACKET   = "i ()Z";
		TCPCON_SENDPACKET   = "a (Z)Ley;";		

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
				new FieldInsnNode(Opcodes.GETFIELD, "co", "j", "Ljava/net/Socket;"),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "ey", "a", "(Llp;Ljava/io/DataInput;ZLjava/net/Socket;)Ley;"),
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
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
		
        System.out.printf("Found TcpConnection. Starting injection\n");
        
        classReader.accept(classNode, 0);
		
        for (MethodNode methodNode : classNode.methods){
        	
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(TCPCON_READPACKET)){
        		System.out.printf("[MobiusCore] Found TcpConnection.readPacket()... \n");
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		ArrayList<ArrayList<AbstractInsnNode>> match;

        		match = this.findPattern(methodNode, TCPCON_PATTERN_INPACKET);
        		if (match.size() != 0){
        			for (ArrayList<AbstractInsnNode> sublist : match){
        				System.out.printf("[MobiusCore] Trying to inject input packet profiler... ");
        			
        				this.applyPayloadAfter(instructions, sublist, TCPCON_PAYLOAD_INPACKET);
        			
        				System.out.printf("Successful injection !\n");
        			}
        		} else {
        			System.out.printf("Error while injecting !\n");
        		}
        	}

        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(TCPCON_SENDPACKET)){
        		System.out.printf("[MobiusCore] Found TcpConnection.sendPacket()... \n");
        		InsnList instructions = methodNode.instructions;
        		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        		ArrayList<ArrayList<AbstractInsnNode>> match;

        		match = this.findPattern(methodNode, TCPCON_PATTERN_OUTPACKET);
        		if (match.size() != 0){
        			for (ArrayList<AbstractInsnNode> sublist : match){
	        			System.out.printf("[MobiusCore] Trying to inject output packet profiler... ");
	        			
	        			this.applyPayloadBefore(instructions, sublist, TCPCON_PAYLOAD_OUTPACKET);
	        			
	        			System.out.printf("Successful injection !\n");
	        			
        			}
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

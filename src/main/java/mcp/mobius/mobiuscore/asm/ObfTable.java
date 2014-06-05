package mcp.mobius.mobiuscore.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;

public enum ObfTable {
	WORLD_UPDATEENTITIES      ("afn", "h", "()V",      "net/minecraft/world/World",           "updateEntities", "()V"),
	WORLD_UPDATEENTITY        ("afn", "g", "(Lqn;)V",  "net/minecraft/world/World",           "updateEntity",   "(Lnet/minecraft/entity/Entity;)V"),
	TILEENTITY_UPDATEENTITY   ("and", "h", "()V",      "net/minecraft/tileentity/TileEntity", "updateEntity",   "()V"),

	WORLDSERVER_TICK          ("mj",  "b", "()V",   "net/minecraft/world/WorldServer",   "tick",        "()V"),
	WORLD_PROVIDER            ("mj",  "t", "Lapa;", "net/minecraft/world/WorldServer",   "provider",    "Lnet/minecraft/world/WorldProvider;"),
	WORLDPROVIDER_DIMID       ("apa", "i", "I",     "net/minecraft/world/WorldProvider", "dimensionId", "I"),	
	
	SERIALIZER_ENCODE         ("er",  "a", "(Lio/netty/channel/ChannelHandlerContext;Lfk;Lio/netty/buffer/ByteBuf;)V", "net/minecraft/util/MessageSerializer", "encode", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;Lio/netty/buffer/ByteBuf;)V"),
	DESERIALIZER_DECODE       ("eq",  "decode", "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V", "net/minecraft/util/MessageDeserializer", "decode", "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V"),

	PACKET_WRITEPACKETDATA    ("fk", "b", "(Lep;)V", "net/minecraft/network/Packet","writePacketData","(Lnet/minecraft/network/PacketBuffer;)V"),	
	PACKET_READPACKETDATA     ("fk", "a", "(Lep;)V", "net/minecraft/network/Packet","readPacketData", "(Lnet/minecraft/network/PacketBuffer;)V"),
	PACKET_GENERATEPACKET     ("fk", "a", "(Lcom/google/common/collect/BiMap;I)Lpk;", "net/minecraft/network/Packet", "generatePacket", "(Lcom/google/common/collect/BiMap;I)Lnet/minecraft/network/Packet;"),
	PACKETBUFFER_READABLE     ("ep", "readableBytes", "()I", "net/minecraft/network/PacketBuffer", "readableBytes", "()I"),
	PACKETBUFFER_CAPACITY     ("ep", "capacity",      "()I", "net/minecraft/network/PacketBuffer", "capacity",      "()I"),
	
	RENDERMANAGER_RENDERENTITY("bnf", "a", "(Lqn;FZ)Z", "net/minecraft/client/renderer/entity/RenderManager", "renderEntityStatic",  "(Lnet/minecraft/entity/Entity;FZ)Z"),
	RENDERMANAGER_RENDERPOSYAW("bnf", "a", "(Lqn;DDDFF)Z", "net/minecraft/client/renderer/entity/RenderManager", "renderEntityWithPosYaw",  "(Lnet/minecraft/entity/Entity;DDDFF)Z"),
	RENDERMANAGER_RENDER      ("bnf", "a", "(Lqn;DDDFFZ)Z", "net/minecraft/client/renderer/entity/RenderManager", "func_147939_a",  "(Lnet/minecraft/entity/Entity;DDDFFZ)Z"),
	
	TERENDER_RENDERAT         ("bmc", "a", "(Land;DDDF)V", "net/minecraft/client/renderer/tileentity/TileEntityRenderer", "renderTileEntityAt", "(Lnet/minecraft/tileentity/TileEntity;DDDF)V"),	
	
	FMLCH_PREWORLDTICK        ("cpw/mods/fml/common/FMLCommonHandler","onPreWorldTick","(Lafn;)V", "cpw/mods/fml/common/FMLCommonHandler","onPreWorldTick","(Lnet/minecraft/world/World;)V"),
	FMLCH_POSTWORLDTICK       ("cpw/mods/fml/common/FMLCommonHandler","onPostWorldTick","(Lafn;)V", "cpw/mods/fml/common/FMLCommonHandler","onPostWorldTick","(Lnet/minecraft/world/World;)V"),
	
	FMLPP_PROCESSPACKET       ("cpw/mods/fml/common/network/internal/FMLProxyPacket", "a", "(Les;)V", "cpw/mods/fml/common/network/internal/FMLProxyPacket", "processPacket", "(Lnet/minecraft/network/INetHandler;)V"),
	
	NETWORKLISTEN_NETWORKTICK ("kd",  "b", "()V",      "net/minecraft/network/NetworkListenThread",          "networkTick",   "()V"),
	TCPCONN_READPACKET		  ("co",  "i", "()Z",	   "net/minecraft/network/TcpConnection",                "readPacket",    "()Z"),
	TCPCONN_SENDPACKET        ("co",  "a", "(Z)Ley;",  "net/minecraft/network/TcpConnection",                "func_74460_a",  "(Z)Lnet/minecraft/network/packet/Packet;"),
	TCPCONN_NETWORKSOCKET     ("co",  "j", "Ljava/net/Socket;", "net/minecraft/network/TcpConnection",       "networkSocket", "Ljava/net/Socket;"),
	MEMCONN_ADDSENDQUEUE      ("cn",  "a", "(Ley;)V", "net/minecraft/network/MemoryConnection", "addToSendQueue", "(Lnet/minecraft/network/packet/Packet;)V"),
	MEMCONN_PROCESSREAD       ("cn",  "b", "()V", "net/minecraft/network/MemoryConnection", "processReadPackets", "()V"),
	MEMCONN_PAIREDCONN        ("cn",  "d", "Lcn;",    "net/minecraft/network/MemoryConnection", "pairedConnection",     "Lnet/minecraft/network/MemoryConnection;"),
    MEMCONN_PROCESSORCACHE    ("cn",  "b", "(Ley;)V", "net/minecraft/network/MemoryConnection", "processOrCachePacket", "(Lnet/minecraft/network/packet/Packet;)V"),	
    MEMCONN_MYNETHANDLER      ("cn", "e", "Lez;", "net/minecraft/network/MemoryConnection", "myNetHandler", "Lnet/minecraft/network/packet/NetHandler;");
    //PACKET_PROCESSPACKET      ("ey", "a", "(Lez;)V", "net/minecraft/network/packet/Packet", "processPacket", "(Lnet/minecraft/network/packet/NetHandler;)V"),    
	//PACKET_WRITEPACKET        ("ey",  "a", "(Llp;Ljava/io/DataInput;ZLjava/net/Socket;)Ley;", "net/minecraft/network/packet/Packet", "writePacket", "(Lnet/minecraft/network/packet/Packet;Ljava/io/DataOutput;)V"),
	//PACKET_READPACKET         ("ey",  "a", "(Llp;Ljava/io/DataInput;ZLjava/net/Socket;)Ley;", "net/minecraft/network/packet/Packet", "readPacket", "(Lnet/minecraft/logging/ILogAgent;Ljava/io/DataInput;ZLjava/net/Socket;)Lnet/minecraft/network/packet/Packet;"),



	
	private String clazzNameN;
	private String methodNameN;
	private String descriptorN;
	private String clazzNameS;
	private String methodNameS;
	private String descriptorS;	
	
	private ObfTable(String clazzn, String namen, String descn, String clazzs, String names, String descs){
		this.clazzNameN = clazzn;
		this.clazzNameS = clazzs;
		this.methodNameN = namen;
		this.methodNameS = names;
		this.descriptorN = descn;
		this.descriptorS = descs;
	}
	
	public String getClazz(){
		if (CoreTransformer.version == TargetVersion.ECLIPSE)
			return this.clazzNameS;
		else
			return this.clazzNameN;
	}

	public String getName(){
		if (CoreTransformer.version == TargetVersion.ECLIPSE)
			return this.methodNameS;
		else
			return this.methodNameN;
	}	

	public String getDescriptor(){
		if (CoreTransformer.version == TargetVersion.ECLIPSE)
			return this.descriptorS;
		else
			return this.descriptorN;
	}		
	
	public String getFullDescriptor(){
		if (CoreTransformer.version == TargetVersion.ECLIPSE)
			return this.methodNameS + " " + this.descriptorS;
		else
			return this.methodNameN + " " + this.descriptorN;
	}
	
	/*
	public FieldInsnNode getFieldNode(){
		return new FieldInsnNode(Opcodes.GETFIELD, this.getClazz(), this.getName(), this.getDescriptor());
	}
	*/
}

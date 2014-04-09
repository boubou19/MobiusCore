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

	// TileEntityHopper  => asi
	// TileEntityFurnace => asg

	TransformerWorld            transWorld = new TransformerWorld();
	TransformerFMLCommonHandler transFML   = new TransformerFMLCommonHandler(); 
	TransformerMinecraftServer  transMCS   = new TransformerMinecraftServer();
	TransformerWorldServer      transWorldServer   = new TransformerWorldServer();
	TransformerTcpConnection    transTCPConnection = new TransformerTcpConnection();
	TransformerRenderManager	transRenderManag   = new TransformerRenderManager();
	TransformerTERenderer		transTERenderer    = new TransformerTERenderer();
	TransformerNetworkListenThread transNetListen  = new TransformerNetworkListenThread();
	
	public CoreTransformer(){
		super();
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		//System.out.printf("[ %s ] %s\n", name, srgname);
		
		if (srgname.equals("net.minecraft.world.World")){
			System.out.printf("[MobiusCore] Found %s\n", srgname);
			return transWorld.transform(name, srgname, bytes);
		}
		
		if (srgname.equals("cpw.mods.fml.common.FMLCommonHandler")){
			System.out.printf("[MobiusCore] Found %s\n", srgname);
			return transFML.transform(name, srgname, bytes);		
		}

		//if (srgname.equals("net.minecraft.server.MinecraftServer")){
		//	return transMCS.transform(name, srgname, bytes);
		//}
		
		if (srgname.equals("net.minecraft.world.WorldServer")){
			System.out.printf("[MobiusCore] Found %s\n", srgname);
			return transWorldServer.transform(name, srgname, bytes);
		}		
		
		if (srgname.equals("net.minecraft.network.TcpConnection")){
			System.out.printf("[MobiusCore] Found %s\n", srgname);
			return transTCPConnection.transform(name, srgname, bytes);
		}			
		
		if (srgname.equals("net.minecraft.client.renderer.entity.RenderManager")){
			System.out.printf("[MobiusCore] Found %s\n", srgname);
			return transRenderManag.transform(name, srgname, bytes);
		}	
		
		if (srgname.equals("net.minecraft.client.renderer.tileentity.TileEntityRenderer")){
			System.out.printf("[MobiusCore] Found %s\n", srgname);
			return transTERenderer.transform(name, srgname, bytes);
		}			
		
		if (srgname.equals("net.minecraft.server.integrated.IntegratedServerListenThread")){
			System.out.printf("[MobiusCore] Found %s\n", srgname);
			return transNetListen.transform(name, srgname, bytes);
		}		
		
		if (srgname.equals("net.minecraft.server.dedicated.DedicatedServerListenThread")){
			System.out.printf("[MobiusCore] Found %s\n", srgname);
			return transNetListen.transform(name, srgname, bytes);
		}		
		
		return bytes;
	}


}

package mcp.mobius.mobiuscore.asm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public abstract class TransformerBase {
	
	HashMap<String, List<MethodDescriptor>> methodsToOverwrite = new HashMap<String, List<MethodDescriptor>>();
	HashMap<String, List<MethodDescriptor>> methodsToInject    = new HashMap<String, List<MethodDescriptor>>();		
	
	public abstract byte[] transform(String name, String srgname, byte[] bytes);
	
	protected ArrayList<AbstractInsnNode> findPattern(MethodNode methodNode, AbstractInsnNode... pattern){
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
				else
					break;
			}
			
			if (match){
				System.out.printf("Pattern found ... ");
				return returnList;
			}
		}
		
		return returnList;
	}
	
	protected boolean areInsnEqual(AbstractInsnNode insn1, AbstractInsnNode insn2){
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
	
	protected void printInsnNode (AbstractInsnNode insnNode){
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
	
	protected void applyPayloadAfter(InsnList instructions, ArrayList<AbstractInsnNode> match,  AbstractInsnNode[] payload_pattern){
		InsnList payload = new InsnList();
		for (int i = 0; i < payload_pattern.length; i++)
			payload.add(payload_pattern[i]);
	
		instructions.insert(match.get(match.size() - 1), payload);		
	}
	
	protected void applyPayloadBefore(InsnList instructions, ArrayList<AbstractInsnNode> match,  AbstractInsnNode[] payload_pattern){
		InsnList payload = new InsnList();
		for (int i = 0; i < payload_pattern.length; i++)
			payload.add(payload_pattern[i]);
	
		instructions.insertBefore(match.get(0), payload);		
	}	
	
	/*
	 * Will return a byte array of the content of the named class in the coremod jar
	 */
	protected byte[] getJarClass(String transformedName){
		byte[] bytes = null;
		
		try
		{
			ZipFile zip = new ZipFile(CoreDescription.location);
			ZipEntry entry = zip.getEntry(transformedName.replace('.', '/')+".class");
			//ZipEntry entry = zip.getEntry(name.replace('.', '/')+".class");
			if(entry == null)
				System.out.println(transformedName+" not found in "+CoreDescription.location.getName());
			else
			{
				InputStream zin = zip.getInputStream(entry);
				bytes = new byte[(int) entry.getSize()];
				zin.read(bytes);
				zin.close();
			}
			zip.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error overriding "+transformedName+" from "+CoreDescription.location.getName(), e);
		}
		
		return bytes;
	}	
	
	/*
	 * Overwrite method X with signature Y with the method from inside the coremod jar.
	 */
	protected byte[] overwriteMethod(String className, MethodDescriptor methodDesc, byte[] vanillaBytes){
		
		byte[] coremodBytes = this.getJarClass(className);
		
		ClassNode   vanillaNode   = new ClassNode();
        ClassReader vanillaReader = new ClassReader(vanillaBytes);
		ClassNode   coremodNode   = new ClassNode();
        ClassReader coremodReader = new ClassReader(coremodBytes);        
        
        vanillaReader.accept(vanillaNode, 0);
        coremodReader.accept(coremodNode, 0);
        
        MethodNode vanillaMethodNode = null;
        for (MethodNode node: vanillaNode.methods){
        	try{
        		if (node != null && node.desc.equals(methodDesc.getDescriptor()) && (node.name.equals(methodDesc.getMethodName()))){
        			System.out.printf("Found method node %s.%s %s in Vanilla.\n", className, methodDesc.getMethodName(), methodDesc.getDescriptor());
        			vanillaMethodNode = node;        		
        		}
        	} catch (Exception e){
        		//System.out.printf("Error while parsing %s %s\n", className, node);
        	}        		
        }
        
        MethodNode coremodMethodNode = null;
        for (MethodNode node: coremodNode.methods){
        	try{
        		if (node != null && node.desc.equals(methodDesc.getDescriptor()) && (node.name.equals(methodDesc.getMethodName()))){
        			System.out.printf("Found method node %s.%s %s in Coremod.\n", className, methodDesc.getMethodName(), methodDesc.getDescriptor());
        			coremodMethodNode = node;
        		}
        	} catch (Exception e){
        		//System.out.printf("Error while parsing %s %s\n", className, node);
        	}           		
        }
        
        if (vanillaMethodNode == null){
        	System.out.printf("Method node %s.%s %s not found in Vanilla ! This is going to crash !.\n", className, methodDesc.getMethodName(), methodDesc.getDescriptor());
        }
        if (coremodMethodNode == null){
        	System.out.printf("Method node %s.%s %s not found in Coremod ! This is going to crash !.\n", className, methodDesc.getMethodName(), methodDesc.getDescriptor());
        }
        
        vanillaNode.methods.remove(vanillaMethodNode);
        coremodMethodNode.accept(vanillaNode);
        //vanillaNode.methods.add(coremodMethodNode);
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        vanillaNode.accept(writer);
        return writer.toByteArray();        
	}
	
	/*
	 * Inject a new method with a new signature to the list of methods.
	 */
	protected byte[] injectMethod(String className, MethodDescriptor methodDesc, byte[] vanillaBytes){
		
		byte[] coremodBytes = this.getJarClass(className);
		
		ClassNode   vanillaNode   = new ClassNode();
        ClassReader vanillaReader = new ClassReader(vanillaBytes);
		ClassNode   coremodNode   = new ClassNode();
        ClassReader coremodReader = new ClassReader(coremodBytes);        
        
        vanillaReader.accept(vanillaNode, 0);
        coremodReader.accept(coremodNode, 0);
        
        MethodNode coremodMethodNode = null;
        for (MethodNode node: coremodNode.methods){
        	try{
        		if (node != null && node.desc.equals(methodDesc.getDescriptor()) && (node.name.equals(methodDesc.getMethodName()))){
        			System.out.printf("Found method node %s.%s %s in Coremod. Injecting !\n", className, methodDesc.getMethodName(), methodDesc.getDescriptor());
        			coremodMethodNode = node;
        		}
        	} catch (Exception e){
        		//System.out.printf("Error while parsing %s %s\n", className, node);
        	}
        }        

        if (coremodMethodNode == null){
        	System.out.printf("Method node %s.%s %s not found in Coremod ! This is going to crash !.\n", className, methodDesc.getMethodName(), methodDesc.getDescriptor());
        }        
        
        coremodMethodNode.accept(vanillaNode);
        //vanillaNode.methods.add(coremodMethodNode);
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        vanillaNode.accept(writer);
        return writer.toByteArray();		
	}	
}

package mcp.mobius.mobiuscore.asm;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public abstract class TransformerBase {
	
	HashMap<String, List<MethodDescriptor>> methodsToOverwrite = new HashMap<String, List<MethodDescriptor>>();
	HashMap<String, List<MethodDescriptor>> methodsToInject    = new HashMap<String, List<MethodDescriptor>>();		
	
	public abstract byte[] transform(String name, String srgname, byte[] bytes);

	protected ArrayList<ArrayList<AbstractInsnNode>> findPattern(MethodNode methodNode, AbstractInsnNode... pattern){
		return findPattern( methodNode, false, pattern);	
	}
	
	protected ArrayList<ArrayList<AbstractInsnNode>> findPattern(MethodNode methodNode, boolean dumpOpcodes, AbstractInsnNode... pattern){
		InsnList instructions = methodNode.instructions;
		ArrayList<ArrayList<AbstractInsnNode>> returnList = new ArrayList<ArrayList<AbstractInsnNode>>();
		
		//We are going to check all the intructions in the method to find pattens
		for (int indexFirstInst = 0; indexFirstInst < instructions.size(); indexFirstInst++){	
			
			AbstractInsnNode currNode = instructions.get(indexFirstInst);
			if (dumpOpcodes)
				printInsnNode(currNode);
			
			//We found the first element of the pattern. We are starting matching all the elements
			if (this.areInsnEqual(currNode, pattern[0]));{	
				boolean match = true;
				
				//We are forward matching all the instructions to see if they are ok. By the end of this loop, match is false if at least one inst doesn't match
				for (int indexPatternInst = 0; indexPatternInst < pattern.length; indexPatternInst++){
					try{
						match = match && this.areInsnEqual(instructions.get(indexFirstInst + indexPatternInst) , pattern[indexPatternInst]);
					} catch (ArrayIndexOutOfBoundsException e){
						return returnList;	// This can only happen if there is less than the number of pattern elements in the remaining instructions. We can return the current results directly.
					}
				}

				// All the instructions are equal, we create a new ArrayList and add it to the return list
				if (match){
					ArrayList<AbstractInsnNode> matchedPattern = new ArrayList<AbstractInsnNode>();
					for (int indexPatternInst = 0; indexPatternInst < pattern.length; indexPatternInst++){
						matchedPattern.add(instructions.get(indexFirstInst + indexPatternInst));
					}
					returnList.add(matchedPattern);
				}
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
			if (((LineNumberNode)insn2).line  == ((LineNumberNode)insn2).line) return true;			
		}
			
		if (insn1 instanceof VarInsnNode){
			if ((((VarInsnNode)insn2).var == -1) || (((VarInsnNode)insn2).var == -1)) return true;
			if (((VarInsnNode)insn2).var  != ((VarInsnNode)insn2).var) return false;
			if (((VarInsnNode)insn2).var  == ((VarInsnNode)insn2).var) return true;			
		}

		if (insn1 instanceof MethodInsnNode){
			if ((((MethodInsnNode)insn1).owner.equals(((MethodInsnNode)insn2).owner)) &&
			    (((MethodInsnNode)insn1).name.equals(((MethodInsnNode)insn2).name))  &&
			    (((MethodInsnNode)insn1).desc.equals(((MethodInsnNode)insn2).desc)))
				return true;
		}
		
		if (insn1 instanceof FieldInsnNode){
			if ((((FieldInsnNode)insn1).owner.equals(((FieldInsnNode)insn2).owner)) &&
			    (((FieldInsnNode)insn1).name.equals(((FieldInsnNode)insn2).name))  &&
			    (((FieldInsnNode)insn1).desc.equals(((FieldInsnNode)insn2).desc)))
				return true;
		}		
		
		if (insn1 instanceof InsnNode){
			if (insn1.getOpcode() == insn2.getOpcode())
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
			System.out.printf("[ %s ] %s\n", insnNode.getOpcode(), insnNode);
			break;
		}		
	}
	
	private void applyPayloadAfter(InsnList instructions, ArrayList<AbstractInsnNode> match,  AbstractInsnNode[] payload_pattern){
		InsnList payload = new InsnList();
		for (int i = 0; i < payload_pattern.length; i++)
			payload.add(payload_pattern[i]);
	
		instructions.insert(match.get(match.size() - 1), payload);		
	}
	
	private void applyPayloadBefore(InsnList instructions, ArrayList<AbstractInsnNode> match,  AbstractInsnNode[] payload_pattern){
		InsnList payload = new InsnList();
		for (int i = 0; i < payload_pattern.length; i++)
			payload.add(payload_pattern[i]);
	
		instructions.insertBefore(match.get(0), payload);
	}	
	
	protected void applyPayloadBefore(MethodNode methodNode, AbstractInsnNode[] pattern, AbstractInsnNode[] payload){
		ArrayList<ArrayList<AbstractInsnNode>> match;
		
		match = this.findPattern(methodNode, pattern);
		if (match.size() != 0){
			for (ArrayList<AbstractInsnNode> sublist : match){
    			this.applyPayloadBefore(methodNode.instructions, sublist, payload);
			}
			System.out.printf("[MobiusCore] Successful injection in %s %s\n", methodNode.name, methodNode.desc);
			return;
		}		
		throw new RuntimeException(String.format("Pattern not found while trying to inject into %s", methodNode.name));
	}
	
	protected void applyPayloadAfter(MethodNode methodNode, AbstractInsnNode[] pattern, AbstractInsnNode[] payload){
		ArrayList<ArrayList<AbstractInsnNode>> match;
		
		match = this.findPattern(methodNode, pattern);
		if (match.size() != 0){
			for (ArrayList<AbstractInsnNode> sublist : match){
    			this.applyPayloadAfter(methodNode.instructions, sublist, payload);
			}
			System.out.printf("[MobiusCore] Successful injection in %s %s\n", methodNode.name, methodNode.desc);			
			return;			
		}		
		throw new RuntimeException(String.format("Pattern not found while trying to inject into %s", methodNode.name));
	}	
	
	protected void applyPayloadFirst(MethodNode methodNode, AbstractInsnNode[] payload_pattern){
		InsnList instructions = methodNode.instructions;
		InsnList payload = new InsnList();
		for (int i = 0; i < payload_pattern.length; i++)
			payload.add(payload_pattern[i]);
	
		instructions.insertBefore(instructions.getFirst(), payload);
		
		System.out.printf("[MobiusCore] Successful injection in %s %s\n", methodNode.name, methodNode.desc);		
	}		
	
	protected void applyPayloadLast(MethodNode methodNode, AbstractInsnNode[] payload_pattern){
		InsnList instructions = methodNode.instructions;
		InsnList payload = new InsnList();
		for (int i = 0; i < payload_pattern.length; i++)
			payload.add(payload_pattern[i]);
	
		for (int i = instructions.size() - 1; i >= 0; i--){
			if (instructions.get(i).getOpcode() == Opcodes.RETURN){
				instructions.insertBefore(instructions.get(i), payload);
			}
		}

		System.out.printf("[MobiusCore] Successful injection in %s %s\n", methodNode.name, methodNode.desc);		
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
		
		try{
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
		} catch (ClassFormatError e){
			System.out.printf("Class already injected.");
			return vanillaBytes;
		}		
	}	
	
	protected void dumpChecksum(byte[] data, String filename){
		try{ 
			//System.out.printf("[MobiusCore] Found %s with checksum %s\n", filename, MessageDigest.getInstance("MD5").digest(data));
			
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(data);
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext.length() < 32 ){
			  hashtext = "0"+hashtext;
			}	
			
			System.out.printf("[MobiusCore] Found %s with checksum %s\n", filename, hashtext.toUpperCase());
			
		}
		catch (Exception e) {
		}			
	}
	
	protected MethodNode getMethod(ClassNode classNode, String methodName){
        for (MethodNode methodNode : classNode.methods)
        	if (String.format("%s %s", methodNode.name, methodNode.desc).equals(methodName))
        		return methodNode;

        throw new RuntimeException(String.format("Method %s not found in %s\n", methodName, classNode.name));
	}
}

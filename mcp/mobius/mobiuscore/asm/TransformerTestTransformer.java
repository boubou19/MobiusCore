package mcp.mobius.mobiuscore.asm;

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

public class TransformerTestTransformer extends TransformerBase {

	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		ClassNode   classNode   = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);		
        classReader.accept(classNode, 0);
        
        if (classNode.interfaces.size() > 0){
	        System.out.printf("+ %s\n", srgname);
	        for (String s : classNode.interfaces){
	        	System.out.printf("- 	%s\n", s);	
	        }
	        
	        if (classNode.interfaces.contains("net/minecraftforge/event/IEventListener")){
	        	System.out.printf("^^^^^^^^^^^^^^^^^^^^^^^\n");
	        }
        }
		return bytes;
	}

}

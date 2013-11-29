package mcp.mobius.mobiuscore.asm;

import java.util.ArrayList;

public class TransformerFurnace extends TransformerBase {

	public TransformerFurnace(){
		methodsToOverwrite.put("asg", new ArrayList<MethodDescriptor>());		
		methodsToOverwrite.get("asg").add(new MethodDescriptor("h", "()V"));	//bool updateEntity()
	}	
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		for (MethodDescriptor method : methodsToOverwrite.get(name)){
			System.out.printf("Trying to overwrite method %s.%s %s\n", name, method.getMethodName(), method.getDescriptor());
			bytes =  this.overwriteMethod(name, method, bytes);
		}		
		
		return bytes;
	}
}

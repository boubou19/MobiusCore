package mcp.mobius.mobiuscore.asm;

import java.util.ArrayList;

public class TransformerHopper extends TransformerBase {

	public TransformerHopper(){
		methodsToOverwrite.put("asi", new ArrayList<MethodDescriptor>());		
		methodsToOverwrite.get("asi").add(new MethodDescriptor("j", "()Z"));	//bool updateHopper()
		methodsToOverwrite.get("asi").add(new MethodDescriptor("u", "()Z"));	//bool insertItemToInventory()
		methodsToOverwrite.get("asi").add(new MethodDescriptor("a", "(Lash;)Z"));	//bool suckItemsIntoHopper(Hopper)		
		
		methodsToInject.put("asi", new ArrayList<MethodDescriptor>());
		methodsToInject.get("asi").add(new MethodDescriptor("isHopperEmpty", "()Z"));
		methodsToInject.get("asi").add(new MethodDescriptor("isHopperFull",  "()Z"));
		methodsToInject.get("asi").add(new MethodDescriptor("isInventoryFull", "(Lmo;I)Z"));
		methodsToInject.get("asi").add(new MethodDescriptor("isInventoryEmpty","(Lmo;I)Z"));		
	}
	
	@Override
	public byte[] transform(String name, String srgname, byte[] bytes) {
		
		
		for (MethodDescriptor method : methodsToInject.get(name)){
			System.out.printf("Trying to inject method %s.%s %s\n", name, method.getMethodName(), method.getDescriptor());
			bytes =  this.injectMethod(name, method, bytes);
		}
				
		
		for (MethodDescriptor method : methodsToOverwrite.get(name)){
			System.out.printf("Trying to overwrite method %s.%s %s\n", name, method.getMethodName(), method.getDescriptor());
			bytes =  this.overwriteMethod(name, method, bytes);
		}		
		
		return bytes;
	}

}

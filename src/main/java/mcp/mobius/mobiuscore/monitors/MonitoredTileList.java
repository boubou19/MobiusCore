package mcp.mobius.mobiuscore.monitors;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.mobiuscore.asm.CoreDescription;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class MonitoredTileList <E> extends MonitoredList<E>{
	
	private Map<String, Integer> count = new HashMap<String, Integer>();
	
	@Override
	protected void addCount(E e){
		String name = this.getName(e);
		try{
			count.put(name, count.get(name) + 1);
		} catch (NullPointerException ex){
			count.put(name, 1);
		} catch (Exception ex){
			ex.printStackTrace();
			count.put(name, 1);
		}
	}
	
	@Override
	protected void removeCount(int index){
		this.removeCount(this.get(index));
	}
	
	@Override
	protected void removeCount(Object o){
		String name = this.getName(o);
		this.count.put(name, this.count.get(name) - 1);			
	}

	@Override
	protected void clearCount(){
		this.count.clear();
	}	
	
	@Override
	public void printCount(){
		for (String s : this.count.keySet())
			CoreDescription.log.info(String.format("%s : %s", s, this.count.get(s)));
	}
	
	public Map<String, Integer> getCount(){
		return this.count;
	}
	
	protected String getName(Object o){
		TileEntity te = ((TileEntity)o);
		Block block = te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord);
		int   meta  = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
		return this.getStackName(block, meta);
	}	
	
	protected String getStackName(Block block, int meta){
		ItemStack is;
		String name  = String.format("te.%s.%d", block, meta);
		
		try{
			is = new ItemStack(block, 1, meta);
			name  = is.getDisplayName();
		}  catch (Exception e) {	}
		
		return name;
    } 	
	
}
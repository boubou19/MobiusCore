package mcp.mobius.mobiuscore.monitors;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import mcp.mobius.mobiuscore.asm.CoreDescription;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class MonitoredTileList <E> extends MonitoredList<E>{
	
	//private Map<String, Integer> count = new HashMap<String, Integer>();
	private Table<Block, Integer, Integer> count = HashBasedTable.create();
	
	@Override
	protected void addCount(E e){
		TileEntity te = ((TileEntity)e);
		if (te == null || te.getWorldObj() == null) return;
		
		Block block = te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord);
		int   meta  = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
		
		
		try{
			count.put(block, meta, count.get(block, meta) + 1);
		} catch (NullPointerException ex){
			count.put(block, meta, 1);
		} catch (Exception ex){
			ex.printStackTrace();
			count.put(block, meta, 1);
		}
	}
	
	@Override
	protected void removeCount(int index){
		this.removeCount(this.get(index));
	}
	
	@Override
	protected void removeCount(Object o){
		TileEntity te = ((TileEntity)o);
		if (te == null || te.getWorldObj() == null) return;		
		
		Block block = te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord);
		int   meta  = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
		
		try{
			this.count.put(block, meta, this.count.get(block, meta) - 1);
		} catch (NullPointerException e){
			this.count.put(block, meta, 0);
		}
	}

	@Override
	protected void clearCount(){
		this.count.clear();
	}	
	
	@Override
	public void printCount(){
		for (Cell c : this.count.cellSet())
			CoreDescription.log.info(String.format("%s | %s : %s", c.getRowKey(), c.getColumnKey(), c.getValue()));
			
	}
	
	public Table<Block, Integer, Integer> getCount(){
		return this.count;
	}
}
package mcp.mobius.mobiuscore.profiler;

import java.util.EnumSet;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public interface IProfilerHandler {

	public void StartTickStart(IScheduledTickHandler ticker, EnumSet<TickType> ticksToRun);
	public void StopTickStart (IScheduledTickHandler ticker, EnumSet<TickType> ticksToRun);	

	public void StartTickEnd(IScheduledTickHandler ticker, EnumSet<TickType> ticksToRun);
	public void StopTickEnd (IScheduledTickHandler ticker, EnumSet<TickType> ticksToRun);	
	
}

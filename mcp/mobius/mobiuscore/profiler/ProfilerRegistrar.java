package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static DummyProfiler dummy = new DummyProfiler();
	
	public static IProfilerTileEntity profilerTileEntity = dummy;
	public static IProfilerEntity     profilerEntity     = dummy;
	public static IProfilerHandler    profilerHandler    = dummy;
	public static IProfilerTick       profilerTick       = dummy;
	public static IProfilerSubtick    profilerSubtick    = dummy;

	public static IProfilerTileEntity regProfilerTileEntity = dummy;
	public static IProfilerEntity     regProfilerEntity     = dummy;
	public static IProfilerHandler    regProfilerHandler    = dummy;
	public static IProfilerTick       regProfilerTick       = dummy;	
	public static IProfilerSubtick    regProfilerSubtick    = dummy;
	
	public static void registerProfilerTileEntity(IProfilerTileEntity profiler){
		regProfilerTileEntity = profiler;
	}	

	public static void registerProfilerEntity(IProfilerEntity profiler){
		regProfilerEntity = profiler;
	}		

	public static void registerProfilerHandler(IProfilerHandler profiler){
		regProfilerHandler = profiler;
	}			

	public static void registerProfilerSubtick(IProfilerSubtick profiler){
		regProfilerSubtick = profiler;
	}					
	
	public static void registerProfilerTick(IProfilerTick profiler){
		profilerTick    = profiler;		
		regProfilerTick = profiler;
	}				
	
	public static void turnOn(){
		profilerTileEntity = regProfilerTileEntity;
		profilerEntity     = regProfilerEntity;
		profilerHandler    = regProfilerHandler;
		profilerSubtick    = regProfilerSubtick;
	}
	
	public static void turnOff(){
		profilerTileEntity = dummy;
		profilerEntity     = dummy;
		profilerHandler    = dummy;		
		profilerSubtick    = dummy;
	}
	
}

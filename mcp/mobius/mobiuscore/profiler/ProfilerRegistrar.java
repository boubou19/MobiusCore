package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static DummyProfiler dummy = new DummyProfiler();
	
	public static IProfilerTileEntity profilerTileEntity = dummy;
	public static IProfilerEntity     profilerEntity     = dummy;
	public static IProfilerTick       profilerTick       = dummy;

	public static IProfilerTileEntity regProfilerTileEntity = dummy;
	public static IProfilerEntity     regProfilerEntity     = dummy;
	public static IProfilerTick       regProfilerTick       = dummy;
	
	public static void registerProfilerTileEntity(IProfilerTileEntity profiler){
		regProfilerTileEntity = profiler;
	}	

	public static void registerProfilerEntity(IProfilerEntity profiler){
		regProfilerEntity = profiler;
	}		

	public static void registerProfilerTick(IProfilerTick profiler){
		regProfilerTick = profiler;
	}			
	
	public static void turnOn(){
		profilerTileEntity = regProfilerTileEntity;
		profilerEntity     = regProfilerEntity;
		profilerTick       = regProfilerTick;
	}
	
	public static void turnOff(){
		profilerTileEntity = dummy;
		profilerEntity     = dummy;
		profilerTick       = dummy;		
	}
	
}

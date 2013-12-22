package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static IProfilerTileEntity profilerTileEntity = new DummyProfiler();
	public static IProfilerEntity     profilerEntity     = new DummyProfiler();
	public static IProfilerTick       profilerTick     = new DummyProfiler();	
	
	public static void registerProfilerTileEntity(IProfilerTileEntity profiler){
		profilerTileEntity = profiler;
	}	

	public static void registerProfilerEntity(IProfilerEntity profiler){
		profilerEntity = profiler;
	}		

	public static void registerProfilerTick(IProfilerTick profiler){
		profilerTick = profiler;
	}			
	
}

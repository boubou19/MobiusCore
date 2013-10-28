package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static IProfilerTileEntity profilerTileEntity = new DummyProfiler();
	public static void registerTileEntityProfiler(IProfilerTileEntity profiler){
		profilerTileEntity = profiler;
	}	
	
}

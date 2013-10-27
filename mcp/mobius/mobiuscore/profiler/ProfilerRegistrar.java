package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static IProfiler profilerTileEntity = new DummyProfiler();
	public static void registerTileEntityProfiler(IProfiler profiler){
		profilerTileEntity = profiler;
	}	
	
}

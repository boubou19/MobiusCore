package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static DummyProfiler dummy = new DummyProfiler();
	
	public static IProfilerNetwork     profilerPacket     = dummy;	
	public static IProfilerNetwork     regProfilerPacket     = dummy;
	
	public static long timeStampLastRun = 0;
	
	public static void registerProfilerPacket(IProfilerNetwork profiler){
		profilerPacket    = profiler;		
		regProfilerPacket = profiler;
	}	
	
	
	public static void turnOff(){
		timeStampLastRun   = System.currentTimeMillis();
	}
	
}

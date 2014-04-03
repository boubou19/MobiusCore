package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static DummyProfiler dummy = new DummyProfiler();
	
	public static IProfilerHandler    profilerHandler    = dummy;
	public static IProfilerNetwork     profilerPacket     = dummy;	

	public static IProfilerHandler    regProfilerHandler    = dummy;
	public static IProfilerNetwork     regProfilerPacket     = dummy;
	
	public static long timeStampLastRun = 0;
	
	public static void registerProfilerHandler(IProfilerHandler profiler){
		regProfilerHandler = profiler;
	}			
	
	public static void registerProfilerPacket(IProfilerNetwork profiler){
		profilerPacket    = profiler;		
		regProfilerPacket = profiler;
	}	
	
	public static void turnOn(){
		profilerHandler    = regProfilerHandler;
	}
	
	public static void turnOff(){
		profilerHandler    = dummy;		
		
		timeStampLastRun   = System.currentTimeMillis();
	}
	
}

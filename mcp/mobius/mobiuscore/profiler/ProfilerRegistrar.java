package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static DummyProfiler dummy = new DummyProfiler();
	
	public static IProfilerTileEntity profilerTileEntity = dummy;
	public static IProfilerHandler    profilerHandler    = dummy;
	public static IProfilerEntUpdate  profilerEntUpdate  = dummy;
	public static IProfilerNetwork     profilerPacket     = dummy;	

	public static IProfilerTileEntity regProfilerTileEntity = dummy;
	public static IProfilerHandler    regProfilerHandler    = dummy;
	public static IProfilerEntUpdate  regProfilerEntUpdate  = dummy;	
	public static IProfilerNetwork     regProfilerPacket     = dummy;
	
	public static long timeStampLastRun = 0;
	
	public static void registerProfilerTileEntity(IProfilerTileEntity profiler){
		regProfilerTileEntity = profiler;
	}	

	public static void registerProfilerHandler(IProfilerHandler profiler){
		regProfilerHandler = profiler;
	}			

	public static void registerProfilerEntUpdate(IProfilerEntUpdate profiler){
		regProfilerEntUpdate = profiler;
	}	
	
	public static void registerProfilerPacket(IProfilerNetwork profiler){
		profilerPacket    = profiler;		
		regProfilerPacket = profiler;
	}	
	
	public static void turnOn(){
		profilerTileEntity = regProfilerTileEntity;
		profilerHandler    = regProfilerHandler;
		profilerEntUpdate  = regProfilerEntUpdate;
	}
	
	public static void turnOff(){
		profilerTileEntity = dummy;
		profilerHandler    = dummy;		
		profilerEntUpdate  = dummy;
		
		timeStampLastRun   = System.currentTimeMillis();
	}
	
}

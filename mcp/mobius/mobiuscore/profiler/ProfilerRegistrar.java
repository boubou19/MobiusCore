package mcp.mobius.mobiuscore.profiler;

public class ProfilerRegistrar {

	public static DummyProfiler dummy = new DummyProfiler();
	
	public static IProfilerTileEntity profilerTileEntity = dummy;
	public static IProfilerEntity     profilerEntity     = dummy;
	public static IProfilerHandler    profilerHandler    = dummy;
	public static IProfilerTick       profilerTick       = dummy;
	public static IProfilerWorldTick  profilerWorldTick  = dummy;
	public static IProfilerEntUpdate  profilerEntUpdate  = dummy;
	public static IProfilerPacket     profilerPacket     = dummy;	

	public static IProfilerTileEntity regProfilerTileEntity = dummy;
	public static IProfilerEntity     regProfilerEntity     = dummy;
	public static IProfilerHandler    regProfilerHandler    = dummy;
	public static IProfilerTick       regProfilerTick       = dummy;	
	public static IProfilerWorldTick  regProfilerWorldTick  = dummy;
	public static IProfilerEntUpdate  regProfilerEntUpdate  = dummy;	
	public static IProfilerPacket     regProfilerPacket     = dummy;
	
	public static long timeStampLastRun = 0;
	
	public static void registerProfilerTileEntity(IProfilerTileEntity profiler){
		regProfilerTileEntity = profiler;
	}	

	public static void registerProfilerEntity(IProfilerEntity profiler){
		regProfilerEntity = profiler;
	}		

	public static void registerProfilerHandler(IProfilerHandler profiler){
		regProfilerHandler = profiler;
	}			

	public static void registerProfilerWorldTick(IProfilerWorldTick profiler){
		regProfilerWorldTick = profiler;
	}					
	
	public static void registerProfilerEntUpdate(IProfilerEntUpdate profiler){
		regProfilerEntUpdate = profiler;
	}	
	
	public static void registerProfilerTick(IProfilerTick profiler){
		profilerTick    = profiler;		
		regProfilerTick = profiler;
	}				
	
	public static void registerProfilerPacket(IProfilerPacket profiler){
		profilerPacket    = profiler;		
		regProfilerPacket = profiler;
	}	
	
	public static void turnOn(){
		profilerTileEntity = regProfilerTileEntity;
		profilerEntity     = regProfilerEntity;
		profilerHandler    = regProfilerHandler;
		profilerWorldTick  = regProfilerWorldTick;
		profilerEntUpdate  = regProfilerEntUpdate;
	}
	
	public static void turnOff(){
		profilerTileEntity = dummy;
		profilerEntity     = dummy;
		profilerHandler    = dummy;		
		profilerWorldTick  = dummy;
		profilerEntUpdate  = dummy;
		
		timeStampLastRun   = System.currentTimeMillis();
	}
	
}

package mcp.mobius.mobiuscore.profiler;

import net.minecraft.world.World;

public enum ProfilerSection implements IProfilerBase{
	DIMENSION_TICK(RunType.REALTIME),			//Global section around the ticks for each dim (Blocks & ents).
	DIMENSION_BLOCKTICK(RunType.ONREQUEST),		//Subsection for dimension block tick.
	ENTITY_UPDATETIME(RunType.ONREQUEST),		//Profiling of the entity tick time, per entity.
	TICK(RunType.REALTIME),						//Tick timing profiling
	TILEENT_UPDATETIME(RunType.ONREQUEST),		//Profiling of the TileEntity tick time, per TE.
	HANDLER_TICKSTART(RunType.ONREQUEST), 		//Server handler for ServerTick start.
	HANDLER_TICKSTOP(RunType.ONREQUEST),  		//Server handler for ServerTick stop.
	PACKET_INBOUND(RunType.REALTIME),			//Outbound packet analysis
	PACKET_OUTBOUND(RunType.REALTIME);			//Inbound packet analysis
	
	public enum RunType{
		REALTIME,
		ONREQUEST;
	}
	
	private RunType       runType;
	private IProfilerBase profiler;
	private IProfilerBase profilerSuspended;
	
	public static long timeStampLastRun;
	
	private ProfilerSection(RunType runType){
		this.runType  = runType;
		this.profiler = new DummyProfiler();
	}
	
	public RunType getRunType(){
		return this.runType;
	}
	
	public IProfilerBase getProfiler(){
		return this.profilerSuspended;
	}
	
	public void setProfiler(IProfilerBase profiler){
		this.profilerSuspended = profiler;
		if (this.runType == RunType.REALTIME)
			this.profiler = profiler;
	}

	public void activate(){
		this.profiler = profilerSuspended;
		this.timeStampLastRun = System.currentTimeMillis();
	}
	
	public void desactivate(){
		if (this.runType == RunType.ONREQUEST)
			this.profiler = new DummyProfiler();
	}	
	
	public static void activateAll(){
		for (ProfilerSection section : ProfilerSection.values())
			section.activate();
	}

	public static void desactivateAll(){
		for (ProfilerSection section : ProfilerSection.values())
			section.desactivate();
	}	

	public static void resetAll(){
		for (ProfilerSection section : ProfilerSection.values())
			section.reset();
	}	
	
	public static String getClassName(){
		return ProfilerSection.class.getCanonicalName().replace(".", "/");
	}
	
	public static String getTypeName(){
		return "L" + ProfilerSection.getClassName() + ";";
	}	
	
	@Override
	public void reset() { this.profiler.reset(); this.profilerSuspended.reset(); }	
	@Override
	public void start() { this.profiler.start(); }
	@Override
	public void stop()  { this.profiler.stop(); }
	@Override
	public void start(Object key) { this.profiler.start(key); }
	@Override
	public void stop(Object key) { this.profiler.stop(key); }
	@Override
	public void start(Object key1, Object key2) { this.profiler.start(key1, key2); }
	@Override
	public void stop(Object key1, Object key2) { this.profiler.stop(key1, key2); }
}

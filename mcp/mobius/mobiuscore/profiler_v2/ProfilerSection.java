package mcp.mobius.mobiuscore.profiler_v2;

import net.minecraft.world.World;

public enum ProfilerSection implements IProfilerBase{
	DIMENSION_TICK(RunType.REALTIME),			//Global section around the ticks for each dim (Blocks & ents)
	DIMENSION_BLOCKTICK(RunType.ONREQUEST);		//Subsection for dimension block tick
	
	public enum RunType{
		REALTIME,
		ONREQUEST;
	}
	
	private RunType       runType;
	private IProfilerBase profiler;
	private IProfilerBase profilerSuspended;
	
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

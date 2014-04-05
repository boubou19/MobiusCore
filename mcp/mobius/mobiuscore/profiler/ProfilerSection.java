package mcp.mobius.mobiuscore.profiler;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;

public enum ProfilerSection implements IProfilerBase{
	DIMENSION_TICK     (RunType.REALTIME, Side.SERVER),			//Global section around the ticks for each dim (Blocks & ents).
	DIMENSION_BLOCKTICK(RunType.ONREQUEST, Side.SERVER),		//Subsection for dimension block tick.
	ENTITY_UPDATETIME  (RunType.ONREQUEST, Side.SERVER),		//Profiling of the entity tick time, per entity.
	TICK               (RunType.REALTIME, Side.SERVER),			//Tick timing profiling
	TILEENT_UPDATETIME (RunType.ONREQUEST, Side.SERVER),		//Profiling of the TileEntity tick time, per TE.
	HANDLER_TICKSTART  (RunType.ONREQUEST, Side.SERVER), 		//Server handler for ServerTick start.
	HANDLER_TICKSTOP   (RunType.ONREQUEST, Side.SERVER),  		//Server handler for ServerTick stop.
	PACKET_INBOUND     (RunType.REALTIME, Side.SERVER),			//Outbound packet analysis
	PACKET_OUTBOUND    (RunType.REALTIME, Side.SERVER),			//Inbound packet analysis
	
	RENDER_TILEENTITY  (RunType.ONREQUEST, Side.CLIENT),		//Profiler for TileEnt rendering
	RENDER_ENTITY      (RunType.ONREQUEST, Side.CLIENT);		//Profiler for Entity rendering
	
	public enum RunType{
		REALTIME,
		ONREQUEST;
	}
	
	private Side          side;
	private RunType       runType;
	private IProfilerBase profiler          = new DummyProfiler();;
	private IProfilerBase profilerSuspended = new DummyProfiler();;
	
	public static long timeStampLastRun;
	
	private ProfilerSection(RunType runType, Side side){
		this.runType  = runType;
		this.profiler = new DummyProfiler();
		this.side     = side;
	}
	
	public RunType getRunType(){
		return this.runType;
	}
	
	public Side getSide(){
		return this.side;
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
	
	public static void activateAll(Side trgside){
		for (ProfilerSection section : ProfilerSection.values())
			if (section.side == trgside)
				section.activate();
	}

	public static void desactivateAll(Side trgside){
		for (ProfilerSection section : ProfilerSection.values())
			if (section.side == trgside)
				section.desactivate();
	}	

	public static void resetAll(Side trgside){
		for (ProfilerSection section : ProfilerSection.values())
			if (section.side == trgside)
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

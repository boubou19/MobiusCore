package mcp.mobius.mobiuscore.profiler;

import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.server.MinecraftServer;

public class DeadManSwitch implements Runnable{

	private long offset              = 0L;
	private long maxTimer            = 0L;
	private int  sleepTime           = 1;
	private long timeLastTick        = 0L;
	private long timeDelta           = 0L;
	private long coolDownDelta       = 0L;
	private boolean switchLocked     = false;
	private long coolDown            = 1000 * 5;
	private Thread serverThread      = null;
	
	private MinecraftServer server = null;
	private Lock lock = new ReentrantLock();
	
	public DeadManSwitch(MinecraftServer server, long maxTimer, int sleepTime, Thread serverThread){
		this.server    = server;
		this.maxTimer  = maxTimer;
		this.sleepTime = sleepTime;
		this.serverThread = serverThread; 
		
	}
	
	public void resetTimer(){
		lock.lock();
		this.timeDelta = 0L;
		lock.unlock();

	}
	
	public long getTimeDelta(){
		lock.lock();
		timeDelta += System.nanoTime()/1000 - this.timeLastTick;
		lock.unlock();
		return timeDelta;
	}

	public long getCoolDownDelta(){
		coolDownDelta += System.nanoTime()/1000 - this.timeLastTick;
		return coolDownDelta;
	}	
	
	public void setMaxTimer(long maxTimer){
		this.maxTimer = maxTimer;
	}	
	
	public void setSleepTime(int sleepTime){
		this.sleepTime = sleepTime;
	}
	
	public void setServer(MinecraftServer server){
		this.server = server;
	}
	
	@Override
	public void run() {
		
		System.out.printf("Starting Dead Man Switch\n");
		
		timeLastTick = System.nanoTime()/1000;
		
		while (server.isServerRunning()){
			try {

				if (this.switchLocked && this.getCoolDownDelta() > this.coolDown){
					this.switchLocked = false;
					coolDownDelta = 0L;
				}
				
				if (!this.switchLocked && this.getTimeDelta() > this.maxTimer){
					System.out.printf("Tick error ! This tick took %d microseconds to complete !\n", this.getTimeDelta());
					for (StackTraceElement elem : serverThread.getStackTrace()){
						System.out.printf("%s\n", String.format("%s.%s:%s",elem.getClassName(), elem.getMethodName(), elem.getLineNumber()));
					}
					this.switchLocked = true;
				}
					
				Thread.sleep(1L);
				this.timeLastTick = System.nanoTime()/1000;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public static DeadManSwitch startDeadManSwitch(MinecraftServer server, long maxTimer, int sleepTime){
		DeadManSwitch deadManSwitch = new DeadManSwitch(server, maxTimer, sleepTime, Thread.currentThread());
		(new Thread(deadManSwitch)).start();
		return deadManSwitch;
	}
	
}

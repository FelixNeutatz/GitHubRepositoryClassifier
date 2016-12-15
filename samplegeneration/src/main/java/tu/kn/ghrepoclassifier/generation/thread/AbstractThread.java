package tu.kn.ghrepoclassifier.generation.thread;

import tu.kn.ghrepoclassifier.generation.Parallelizer;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by felix on 27.11.16.
 */
public abstract class AbstractThread extends Thread {
	
	protected Parallelizer p;
	protected String propertyFile;
	protected AtomicInteger count;
	protected int  maximumRecords;
	protected File outputDir;
	
	public AbstractThread(Parallelizer p,
						  String propertyFile,
						  File outputDir,
						  AtomicInteger count,
						  int maximumRecords) {
		this.p = p;
		this.propertyFile = propertyFile;
		this.count = count;
		this.maximumRecords = maximumRecords;
		this.outputDir = outputDir;
	}
	
	abstract public void run();

}

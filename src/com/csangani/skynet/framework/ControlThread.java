/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csangani.skynet.framework;

import com.csangani.skynet.OutputGenerator;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author Chirag Sangani
 */
public class ControlThread extends Thread {

    //   private GraphDatabaseService db;
    private PrintStream OutStream;
    private PrintStream ErrStream;
    private int NumberOfThreads;
    private static int UIDCounter;
    private Map<Integer, Double>[] Input;
    private CyclicBarrier PosEdge;
    public SimuationThread[] Threads;
    public int Ticks;
    public int MaxTicks;
    public Map<Integer, Double>[] Output;
    public Map<Integer, NodeData> Nodes;
    public Map<Integer, NodeData> InputNodes;
    public Map<Integer, NodeData> OutputNodes;
    public Map<Integer, NodeData>[] HiddenNodes;

    public static int getNewUID() {
	int output = UIDCounter;
	UIDCounter++;
	return output;
    }

    public static class PropertyTypes {

	public static final String ID = "ID";
	public static final String TYPE = "TYPE";
	public static final String ACTFUN = "ACTFUN";
	public static final String OUTPUT = "OUTPUT";
	public static final String WEIGHT = "WEIGHT";
    }

    public static class PropertyValues {

	public static class TYPE {

	    public static final String INPUT = "INPUT";
	    public static final String OUTPUT = "OUTPUT";
	}

	public static class ACTFUN {

	    public static final int STEP = 0;
	    public static final int IDENTITY = 1;
	}
    }

    public ControlThread() {
	assert false : "Framework Control invoked with no arguments";
    }

    public ControlThread(int numThreads, OutputStream out, OutputStream err, Map<Integer, Double>[] input) {
	this.OutStream = new PrintStream(out);
	this.ErrStream = new PrintStream(err);
	this.Input = input;
	this.NumberOfThreads = numThreads;
	Output = new HashMap[Input.length];
	for (int i = 0; i < Output.length; i++) {
	    Output[i] = new HashMap<Integer, Double>();
	}
	this.PosEdge = new CyclicBarrier(numThreads + 1, new Runnable() {

	    @Override
	    public void run() {
		if (Ticks == MaxTicks) {
		    for (SimuationThread thread : Threads) {
			thread.Terminate = true;
		    }
		} else if (Ticks < MaxTicks - HiddenNodes.length) {
		    for (NodeData node : InputNodes.values()) {
			node.Output = Input[Ticks].get(node.UID);
		    }
		}
		if (Ticks > HiddenNodes.length) {
		    for (NodeData node : OutputNodes.values()) {
			Output[Ticks - HiddenNodes.length - 1].put(node.UID, node.Output);
		    }
		}
	    }
	});
    }

    @Override
    public void run() {
	OutStream.print("Setting up simulation... ");
	Threads = new SimuationThread[NumberOfThreads];
	ThreadGroup simThreads = new ThreadGroup("simThreads");
	SimuationThread.PosEdge = PosEdge;
	SimuationThread.NegEdge = new CyclicBarrier(Threads.length);
	SimuationThread.Controller = this;
	SimuationThread.ErrStream = ErrStream;
	for (int i = 0; i < Threads.length; i++) {
	    Threads[i] = new SimuationThread(simThreads, "thread" + i);
	    Threads[i].Nodes = new HashMap<Integer, NodeData>();
	}
	for (Integer UID : Nodes.keySet()) {
	    Threads[UID % Threads.length].Nodes.put(UID, Nodes.get(UID));
	}
	for (NodeData node : Nodes.values()) {
	    if (node.IsInputNode) {
		node.Output = Input[0].get(node.UID);
	    }
	}
	OutStream.println("Done!");
	OutStream.print("Simulating... ");
	Date StartTime = new Date();
	for (SimuationThread thread : Threads) {
	    thread.start();
	}
	for (Ticks = 1; Ticks <= MaxTicks; Ticks++) {
	    try {
		PosEdge.await();
	    } catch (InterruptedException ex) {
		ex.printStackTrace(ErrStream);
	    } catch (BrokenBarrierException ex) {
		ex.printStackTrace(ErrStream);
	    }
	}
	Date StopTime = new Date();
	OutStream.println("Done!");
	OutStream.println("\nTime of execution: " + (StopTime.getTime() - StartTime.getTime()) + " ms");
	new OutputGenerator(Input, Output, ErrStream, InputNodes, HiddenNodes, OutputNodes).GenerateOutput();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csangani.skynet.framework;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author Chirag Sangani
 */
public class SimuationThread extends Thread {

    public Map<Integer, NodeData> Nodes;
    public boolean Terminate;
    public static ControlThread Controller;
    public static CyclicBarrier PosEdge;
    public static CyclicBarrier NegEdge;
    public static PrintStream ErrStream;

    public SimuationThread(ThreadGroup TG, String name) {
	super(TG, name);
    }

    @Override
    public void run() {
	while (true) {
	    if (Terminate) {
		return;
	    }
	    for (NodeData node : Nodes.values()) {
		if (!node.IsInputNode) {
		    double net = 0.0;
		    for (int InputNode : node.InputNodes.keySet()) {
			net += Controller.Threads[InputNode % Controller.Threads.length].Nodes.get(InputNode).Output * node.InputNodes.get(InputNode);
		    }
		    switch (node.ActivationFunction) {
			case ControlThread.PropertyValues.ACTFUN.IDENTITY:
			    node.Latch = net;
			    break;
			case ControlThread.PropertyValues.ACTFUN.STEP:
			    node.Latch = (net > 0) ? 1 : 0;
			    break;
			default:
			    assert false : "Undefined Activation Function";
		    }
		}
	    }
	    try {
		NegEdge.await();
		for (NodeData node : Nodes.values()) {
		    node.Output = node.Latch;
		}
		PosEdge.await();
	    } catch (InterruptedException ex) {
		ex.printStackTrace(ErrStream);
	    } catch (BrokenBarrierException ex) {
		ex.printStackTrace(ErrStream);
	    }
	}
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csangani.skynet;

import com.csangani.skynet.framework.ControlThread;
import com.csangani.skynet.framework.NodeData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Chirag Sangani
 */
public class FrontEnd {

    /**
     * @param args the command line arguments
     */
    public static int DIMENSIONS = 1000;
    public static int CATEGORIES = 100;
    public static int COMPLEXITY = 10000;
    public static int HIDDENLAYERS = 2;
    public static int DATA = 100;
    public static int THREADS = 2;

    public static void main(String[] args) {
	while (true) {
	    try {
		System.out.println("\nDimensions:");
		DIMENSIONS = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		break;
	    } catch (IOException ex) {
		System.err.println("Input error, please try again.");
	    } catch (NumberFormatException ex) {
		System.err.println("Input should be a number, please try again.");
	    }
	}
	while (true) {
	    try {
		System.out.println("\nCategories:");
		CATEGORIES = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		break;
	    } catch (IOException ex) {
		System.err.println("Input error, please try again.");
	    } catch (NumberFormatException ex) {
		System.err.println("Input should be a number, please try again.");
	    }
	}
	while (true) {
	    try {
		System.out.println("\nHidden Layers:");
		HIDDENLAYERS = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		break;
	    } catch (IOException ex) {
		System.err.println("Input error, please try again.");
	    } catch (NumberFormatException ex) {
		System.err.println("Input should be a number, please try again.");
	    }
	}
	while (true && HIDDENLAYERS > 0) {
	    try {
		System.out.println("\nComplexity:");
		COMPLEXITY = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		break;
	    } catch (IOException ex) {
		System.err.println("Input error, please try again.");
	    } catch (NumberFormatException ex) {
		System.err.println("Input should be a number, please try again.");
	    }
	}
	while (true) {
	    try {
		System.out.println("\nData Vectors:");
		DATA = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		break;
	    } catch (IOException ex) {
		System.err.println("Input error, please try again.");
	    } catch (NumberFormatException ex) {
		System.err.println("Input should be a number, please try again.");
	    }
	}
	while (true) {
	    try {
		System.out.println("\nThreads:");
		THREADS = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		break;
	    } catch (IOException ex) {
		System.err.println("Input error, please try again.");
	    } catch (NumberFormatException ex) {
		System.err.println("Input should be a number, please try again.");
	    }
	}

	System.out.print("\nGenerating randomized input... ");
	Random RANDOM = new Random();
	Map<Integer, Double>[] data = new HashMap[DATA];
	for (int j = 0; j < DATA; j++) {
	    data[j] = new HashMap<Integer, Double>(DIMENSIONS);
	    for (int i = 0; i < DIMENSIONS; i++) {
		data[j].put(i, RANDOM.nextDouble());
	    }
	}
	System.out.println("Done!");
	try {

	    System.out.println("Creating neural network architecture.");
	    Map<Integer, NodeData> Nodes = new HashMap<Integer, NodeData>(DIMENSIONS + CATEGORIES + COMPLEXITY);

	    System.out.print("Creating input nodes... ");
	    Map<Integer, NodeData> InputNodes = new HashMap<Integer, NodeData>(DIMENSIONS);
	    for (int i = 0; i < DIMENSIONS; i++) {
		int UID = ControlThread.getNewUID();
		int ACTFUN = ControlThread.PropertyValues.ACTFUN.IDENTITY;
		boolean inputNode = true;
		boolean outputNode = false;
		NodeData node = new NodeData(UID, ACTFUN, new HashMap<Integer, Double>(), inputNode, outputNode);
		InputNodes.put(UID, node);
	    }
	    System.out.println("Done!");

	    System.out.print("Creating hidden nodes... ");
	    Map<Integer, NodeData>[] HiddenNodes = new HashMap[HIDDENLAYERS];
	    for (int j = 0; j < HIDDENLAYERS; j++) {
		HiddenNodes[j] = new HashMap<Integer, NodeData>(COMPLEXITY / HIDDENLAYERS);
		for (int i = 0; i < COMPLEXITY / HIDDENLAYERS; i++) {
		    int UID = ControlThread.getNewUID();
		    int ACTFUN = ControlThread.PropertyValues.ACTFUN.IDENTITY;
		    boolean inputNode = false;
		    boolean outputNode = false;
		    NodeData node = new NodeData(UID, ACTFUN, (j == 0) ? new HashMap<Integer, Double>(DIMENSIONS) : new HashMap<Integer, Double>(COMPLEXITY / HIDDENLAYERS), inputNode, outputNode);
		    HiddenNodes[j].put(UID, node);
		}
	    }
	    System.out.println("Done!");

	    System.out.print("Creating output nodes... ");
	    Map<Integer, NodeData> OutputNodes = new HashMap<Integer, NodeData>(CATEGORIES);
	    for (int i = 0; i < CATEGORIES; i++) {
		int UID = ControlThread.getNewUID();
		int ACTFUN = ControlThread.PropertyValues.ACTFUN.IDENTITY;
		boolean inputNode = false;
		boolean outputNode = true;
		NodeData node = new NodeData(UID, ACTFUN, (HIDDENLAYERS > 0) ? new HashMap<Integer, Double>(COMPLEXITY / HIDDENLAYERS) : new HashMap<Integer, Double>(DIMENSIONS), inputNode, outputNode);
		OutputNodes.put(UID, node);
	    }
	    System.out.println("Done!");

	    //	Create synapses
	    System.out.print("Creating synapses... ");
	    if (HIDDENLAYERS > 0) {
		for (NodeData node : OutputNodes.values()) {
		    for (Integer UID : HiddenNodes[HIDDENLAYERS - 1].keySet()) {
			node.InputNodes.put(UID, RANDOM.nextDouble());
		    }
		}
		for (int i = 1; i < HIDDENLAYERS; i++) {
		    for (NodeData node : HiddenNodes[i].values()) {
			for (Integer UID : HiddenNodes[i - 1].keySet()) {
			    node.InputNodes.put(UID, RANDOM.nextDouble());
			}
		    }
		}
		for (NodeData node : HiddenNodes[0].values()) {
		    for (Integer UID : InputNodes.keySet()) {
			node.InputNodes.put(UID, RANDOM.nextDouble());
		    }
		}
	    } else {
		for (NodeData node : OutputNodes.values()) {
		    for (Integer UID : InputNodes.keySet()) {
			node.InputNodes.put(UID, RANDOM.nextDouble());
		    }
		}
	    }
	    System.out.println("Done!");

	    //	Collect nodes into single object
	    System.out.print("Compacting network architecture... ");
	    for (NodeData node : OutputNodes.values()) {
		Nodes.put(node.UID, node);
	    }
	    for (NodeData node : InputNodes.values()) {
		Nodes.put(node.UID, node);
	    }
	    for (int i = 0; i < HIDDENLAYERS; i++) {
		for (NodeData node : HiddenNodes[i].values()) {
		    Nodes.put(node.UID, node);
		}
	    }
	    System.out.println("Done!");


	    ControlThread controller = new ControlThread(THREADS, System.out, System.err, data);
	    controller.MaxTicks = data.length + HIDDENLAYERS;
	    controller.Nodes = Nodes;
	    controller.InputNodes = InputNodes;
	    controller.OutputNodes = OutputNodes;
	    controller.HiddenNodes = HiddenNodes;

	    controller.start();
	    controller.join();

	} catch (OutOfMemoryError ex) {
	    System.err.println("\nInsufficient memory.");
	} catch (Exception ex) {
	    ex.printStackTrace(System.err);
	}
    }
}
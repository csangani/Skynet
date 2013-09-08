/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csangani.skynet;

import com.csangani.skynet.framework.NodeData;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 *
 * @author Chirag Sangani
 */
public class OutputGenerator {

    private Map<Integer, Double>[] Input;
    private Map<Integer, Double>[] Output;
    private PrintStream OutStream;
    private Map<Integer, NodeData> InputNodes;
    private Map<Integer, NodeData>[] HiddenNodes;
    private Map<Integer, NodeData> OutputNodes;

    public OutputGenerator(Map<Integer, Double>[] Input, Map<Integer, Double>[] Output, OutputStream OutStream, Map<Integer, NodeData> InputNodes, Map<Integer, NodeData>[] HiddenNodes, Map<Integer, NodeData> OutputNodes) {
	this.OutStream = new PrintStream(OutStream);
	this.Input = Input;
	this.Output = Output;
	this.HiddenNodes = HiddenNodes;
	this.InputNodes = InputNodes;
	this.OutputNodes = OutputNodes;
    }

    public void GenerateOutput() {
	OutStream.println("OutputNodes: ");
	for (NodeData node : OutputNodes.values()) {
	    OutStream.print(node.UID + "\t");
	    for (Integer inpnode : node.InputNodes.keySet()) {
		OutStream.print(inpnode + ":" + node.InputNodes.get(inpnode) + "\t");
	    }
	    OutStream.println("");
	}
	OutStream.println("");
	OutStream.println("HiddenNodes: ");
	for (int i = 0; i < HiddenNodes.length; i++) {
	    for (NodeData node : HiddenNodes[i].values()) {
		OutStream.print(node.UID + "\t");
		for (Integer inpnode : node.InputNodes.keySet()) {
		    OutStream.print(inpnode + ":" + node.InputNodes.get(inpnode) + "\t");
		}
		OutStream.println("");
	    }
	}
	OutStream.println("");
	OutStream.println("Input:");
	for (Map<Integer, Double> dataMap : Input) {
	    for (Integer inpnode : dataMap.keySet()) {
		OutStream.print(inpnode + ":" + dataMap.get(inpnode) + "\t");
	    }
	    OutStream.println("");
	}
	OutStream.println("");
	OutStream.println("Output:");
	for (Map<Integer, Double> dataMap : Output) {
	    for (Integer inpnode : dataMap.keySet()) {
		OutStream.print(inpnode + ":" + dataMap.get(inpnode) + "\t");
	    }
	    OutStream.println("");
	}
	OutStream.println("");
    }
}

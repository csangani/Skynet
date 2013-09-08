/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csangani.skynet.framework;

import java.util.Map;

/**
 *
 * @author Chirag Sangani
 */
public class NodeData {

    public int UID;
    public double Output;
    public double Latch;
    public int ActivationFunction;
    public Map<Integer, Double> InputNodes;
    public boolean IsInputNode;
    public boolean IsOutputNode;

    public NodeData(int UID, int ACTFUN, Map<Integer, Double> InputNodes, boolean inputNode, boolean outputNode) {
	this.UID = UID;
	this.ActivationFunction = ACTFUN;
	this.InputNodes = InputNodes;
	this.IsInputNode = inputNode;
	this.IsOutputNode = outputNode;
    }
}
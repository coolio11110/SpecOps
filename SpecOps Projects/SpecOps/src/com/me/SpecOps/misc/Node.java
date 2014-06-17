package com.me.SpecOps.misc;

public class Node {
	float x,y,dir;
	boolean checked;
	
	public Node(float x, float y, float dir) {
		this.x = x;
		this.y = y;
		checked = false;
		this.dir = dir;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getDir() {
		return dir;
	}
	
	public boolean getChecked() {
		return checked;
	}
	
	public void setChecked(boolean bool) {
		checked = bool;
	}
}

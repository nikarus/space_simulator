package com.nikarus.spacesimulator;

public class Touch
{
	public int i;
    public float x, y;
    public Touch() {
    	Clear();
    }
    public void Touched(int i, float x, float y) {
    	this.i = i;
        this.x = x;
        this.y = y;
    }
    public void Clear() {
    	this.i = -1;
        this.x = -1;
        this.y = -1;
    }
}
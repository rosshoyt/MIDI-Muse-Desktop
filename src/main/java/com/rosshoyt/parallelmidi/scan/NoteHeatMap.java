package com.rosshoyt.parallelmidi.scan;

import java.io.Serializable;
import java.util.Arrays;

public class NoteHeatMap implements Serializable, Cloneable {
	private static final long serialVersionUID = -74910217358788424L;
	/**
	 * Defaults to 12. This way the frequency of
	 * each musical pitch of the octave is tracked.
	 */
	private int dim;
	private int low, high;
	private int[] cells;

	public NoteHeatMap(int dim, int low, int high) {
		this.dim = dim;
		this.low = low;
		this.high = high;
		cells = new int[dim];
	}
	
	public NoteHeatMap() {
		this(12, 0, 11);
	}

	public NoteHeatMap(int pitch) {
		this();
		accum(pitch);
	}
	
	public int getDim() {
		return dim;
	}
	public double getLow() {
		return low;
	}
	public double getHigh() {
		return high;
	}
	
	public Object clone() {
		NoteHeatMap copy = new NoteHeatMap(dim, low, high);
		for (int i = 0; i < cells.length; i++)
			copy.cells[i] = cells[i];
		return copy;
	}

	private int place(double where) {
		int index = (int) ((where - low) / ((high - low) / dim));
		if (index < 0)
			return 0;
		if (index >= dim)
			return dim - 1;
		return index;
	}

	private void incrCell(int r) {
		cells[r % dim]++;
	}

	public int getCell(int r) {
		return cells[r];
	}
	
	public void setCell(int r, int value) {
		cells[r % dim] = value;
	}

	public static NoteHeatMap combine(NoteHeatMap a, NoteHeatMap b) {
		NoteHeatMap heatmap = new NoteHeatMap(a.dim, a.low, a.high);
		for (int i = 0; i < heatmap.cells.length; i++)
			heatmap.cells[i] = a.cells[i] + b.cells[i];
		return heatmap;
	}

	public NoteHeatMap accum(int pitch) {
		incrCell(pitch);
		return this;
	}
	
	public NoteHeatMap addWeighted(NoteHeatMap other, double weight) {
		for (int i = 0; i < cells.length; i++)
			cells[i] += other.cells[i] * weight;
		return this;
	}
	
	public String toString() {
		return Arrays.toString(cells);
	}
}

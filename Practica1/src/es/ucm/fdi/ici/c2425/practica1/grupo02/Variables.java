package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.ArrayList;
import java.util.List;

import pacman.game.Constants.GHOST;

public class Variables {
	
	private List<GHOST> nearestGhosts;
	private int nPills;
	private double distanceFromNearestIndex;
	private boolean powerPill;
	private int numberOfGhost;
	
	public Variables(int nPills, double distanceFromNearestIndex) {
		this.nearestGhosts = new ArrayList<>();
		this.nPills = nPills;
		this.distanceFromNearestIndex = distanceFromNearestIndex;
		this.powerPill =false;
		numberOfGhost = 0;
	}
	
	public List<GHOST> getNearestGhosts() {
		return this.nearestGhosts;
	}
	
	public void setNearestGhosts(GHOST ghost) {
		this.nearestGhosts.add(ghost);
		this.numberOfGhost++;
	}
	
	public int getNumberOfPills() {
		return this.nPills;
	}
	
	public void setNumberOfPills(int pills) {
		this.nPills = pills;
	}
	
	public double getDistanceFromNearestIndex() {
		return this.distanceFromNearestIndex;
	}
	
	public void setDistanceFromNearestIndex(double distance) {
		this.distanceFromNearestIndex = distance;
	}
	
	public boolean powerPill() {
		return this.powerPill;
	}
	
	public void activatePowerPill(boolean value) {
		this.powerPill = value;
	}
	
	public int getNumberOfGhosts() {
		return this.numberOfGhost;
	}
	
	public void setNumberOfGhosts(int n) {
		this.numberOfGhost = n;
	}
}

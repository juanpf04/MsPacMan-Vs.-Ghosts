package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts;

import java.util.HashMap;
import java.util.Map;

import pacman.game.Constants.GHOST;

public class GhostsInfo {

	private int pacmanNextJunction;
	private int nearestEdibleGhostToPacman;

	// nearest not edible ghost to nearest edible ghost to pacman
	private int nearestGhost;

	private Map<GHOST, Double> distanceGhostToPacman;
	private Map<GHOST, Integer> nearestPPill;
	private Map<GHOST, Double> distanceToNearestPPill;
	// private Map<GHOST, Integer> nearestGhost;

	public GhostsInfo() {
		this.nearestPPill = new HashMap<GHOST, Integer>();
		// this.nearestGhost = new HashMap<GHOST, Integer>();
		this.distanceToNearestPPill = new HashMap<GHOST, Double>();
	}

	// getters

	public int getNearestGhost() {
		return this.nearestGhost;
	}

	/**
	 * Node of nearest edible ghost to pacman. Returns -1 if no ghost is edbile.
	 * 
	 * @param node
	 */
	public int getNearestEdibleGhostToPacman() {
		return this.nearestEdibleGhostToPacman;
	}

	public double getDistanceGhostToPacman(GHOST ghost) {
		return this.distanceGhostToPacman.get(ghost);
	}

	public int getPacmanNextJunction() {
		return pacmanNextJunction;
	}

	public int getNearestPPill(GHOST ghost) {
		return this.nearestPPill.get(ghost);
	}

	public double getDistanceToNearestPPill(GHOST ghost) {
		return this.distanceToNearestPPill.get(ghost);
	}

	// setters

	public void setNearestGhost(int node) {
		this.nearestGhost = node;
	}

	public void setNearestEdibleGhostToPacman(int node) {
		this.nearestEdibleGhostToPacman = node;
	}

	public void setPacmanNextJunction(int node) {
		this.pacmanNextJunction = node;
	}

	public void setNearestPPill(GHOST ghost, int index) {
		this.nearestPPill.put(ghost, index);
	}

	public void setDistanceToNearestPPill(GHOST ghost, double distance) {
		this.distanceToNearestPPill.put(ghost, distance);
	}

	public void setDistanceGhostToPacman(GHOST ghost, double distance) {
		this.distanceGhostToPacman.put(ghost, distance);
	}

	public int getNearestExit(GHOST ghost) {
		// TODO Auto-generated method stub
		return 0;
	}
}

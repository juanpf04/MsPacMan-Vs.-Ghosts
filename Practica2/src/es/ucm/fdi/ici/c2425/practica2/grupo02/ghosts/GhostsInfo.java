package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts;

import java.util.List;

/*
 * Data structure to hold all information relative to the ghosts.
 */
public class GhostsInfo {

	public int pacmanNextJunction;

	// Node of nearest edible ghost to pacman. Returns -1 if no ghost is edbile.
	public int nearestEdibleGhostToPacman;

	// nearest not edible ghost to nearest edible ghost to pacman
	public int nearestGhost;

	public int pacmanClosestPPill;

	public List<Integer> exits;
}

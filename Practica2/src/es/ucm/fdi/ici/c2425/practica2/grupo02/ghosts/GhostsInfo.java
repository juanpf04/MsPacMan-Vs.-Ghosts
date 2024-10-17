package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts;

import java.util.HashMap;
import java.util.Map;

import pacman.game.Constants.GHOST;

public class GhostsInfo {

	private Map<GHOST, Integer> nearestPPill;
	private Map<GHOST, Integer> nearestPill;
	private Map<GHOST, Integer> nearestGhost;

	public GhostsInfo() {
		this.nearestPPill = new HashMap<GHOST, Integer>();
		this.nearestPill = new HashMap<GHOST, Integer>();
		this.nearestGhost = new HashMap<GHOST, Integer>();
	}

	public int getNearestPPill(GHOST ghost) {
		return this.nearestPPill.get(ghost);
	}

	public int getNearestPill(GHOST ghost) {
		return this.nearestPill.get(ghost);
	}

	public int getNearestGhost(GHOST ghost) {
		return this.nearestGhost.get(ghost);
	}

	public void setNearesPPill(GHOST ghost, int index) {
		this.nearestPPill.put(ghost, index);
	}

	public void setNearestGhost(GHOST ghost, int index) {
		this.nearestGhost.put(ghost, index);
	}

	public void setNearesPill(GHOST ghost, int index) {
		this.nearestPill.put(ghost, index);
	}
}

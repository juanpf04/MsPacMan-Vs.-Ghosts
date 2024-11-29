package es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import es.ucm.fdi.ici.fuzzy.FuzzyInput;

public class GhostsInput extends FuzzyInput {

	/*
	 * Data structure to hold all information relative to the ghosts. Works as a
	 * Struct.
	 */
	public static class GhostsInfo {

		public int minDistanceFromPacmanToPPill;
		public int closestPPillToPacman;

//		public int[] activePills;
		public int pillCount;

//		public int[] activePowerPills; // done

		// Node of nearest edible ghost to pacman. Returns -1 if no ghost is edbile.
		public int nearestEdibleGhostToPacman;
//		public int distanceFromPacmanToNearestEdibleGhost; // done

		// nearest not edible ghost to nearest edible ghost to pacman
		public int nearestGhost;

		public List<Integer> exits;

		// Distance from ghost to center of pills
//		public Map<GHOST, Integer> distancesFromGhostToPill; // done
		// Distance from ghost to nearest power pill of pacman
		public Map<GHOST, Integer> distancesFromGhostToPPill;
		public Map<GHOST, Integer> distancesFromGhostToPacman;
//		public Map<GHOST, Integer> distancesFromPacmanToGhost; // done

		// Distance from ghost to nearest edible ghost to pacman
		public Map<GHOST, Integer> distancesFromGhostToEdibleGhost;
		public Map<GHOST, Integer> distancesFromEdibleGhostToGhost;

		public Map<GHOST, Boolean> isGhostBehindPacman;
		public Map<GHOST, Boolean> isGhostEdible;
		public Map<GHOST, Boolean> isGhostInLair; // done
//		public Map<GHOST, Boolean> doesGhostRequireAction; // done

		// Density of ghosts around a certain ghost
		public Map<GHOST, Double> ghostDensity;

		public Map<GHOST, Integer> closestGhostIndex;

//		public int edibleGhosts; // done

		public GhostsInfo() {
			this.exits = new ArrayList<>();
			this.isGhostBehindPacman = new HashMap<>();
			this.isGhostEdible = new HashMap<>();
			this.isGhostInLair = new HashMap<>();
//			this.doesGhostRequireAction = new HashMap<>();
//			this.distancesFromGhostToPill = new HashMap<>();
			this.distancesFromGhostToPPill = new HashMap<>();
			this.distancesFromGhostToPacman = new HashMap<>();
//			this.distancesFromPacmanToGhost = new HashMap<>();
			this.ghostDensity = new HashMap<>();
			this.distancesFromGhostToEdibleGhost = new HashMap<>();
			this.distancesFromEdibleGhostToGhost = new HashMap<>();

			this.closestGhostIndex = new HashMap<>();
		}
	}

	private GhostsInfo info;

	public GhostsInput(Game game, GhostsInfo info) {
		super(game);
		this.info = info;
		this.parseInput();
	}

	private double[] distance;

	@Override
	public void parseInput() {

		distance = new double[] { -1, -1, -1, -1 };

		for (GHOST g : GHOST.values()) {
			int index = g.ordinal();
			int pos = game.getGhostCurrentNodeIndex(g);
			if (pos != -1) {
				distance[index] = game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			} else
				distance[index] = -1;
		}
	}

	public boolean isVisible() {
		return this.game.getPacmanCurrentNodeIndex() != -1;
	}

	public HashMap<String, Double> getFuzzyValues(GHOST ghost) {
		HashMap<String, Double> vars = new HashMap<String, Double>();
		
		for (GHOST g : GHOST.values()) {
			vars.put("edible", 1.0);
			vars.put("behindPacman", 1.0);
			vars.put("MSPACMANconfidence", 1.0);
			vars.put("MSPACMANconfidence", 1.0);
			vars.put("distanceNearestPPill", 1.0);
			vars.put("MSPACMANdistanceNearestPPill", 1.0);
			vars.put("MSPACMANdistance", 1.0);
			vars.put("distanceToClosestEdibleGhost", 1.0);
			vars.put("distanceToClosestNotEdibleGhost", 1.0);
			vars.put("ghostDensity", 1.0);
			vars.put("pillCount", 1.0);
		}
		
		return vars;
	}

	@Override
	public HashMap<String, Double> getFuzzyValues() {
		return null;
	}
}

package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.c2425.practica5.grupo02.POS;
import es.ucm.fdi.ici.cbr.CBRInput;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInput extends CBRInput {

	private static final int RANGO_DISTANCIA = 40;
	
	/*
	 * Data structure to hold all information relative to the pacman. Works as a
	 * Struct.
	 */
	public static class MsPacManInfo {

		public Integer score; // current score
		public Integer level; // current level
		
		public Integer index; // current index
		public MOVE move; // last move made
		
		public Integer edibleTime; // time of edible ghost
		
		public Integer edibleGhosts; // number of edible ghosts
		public Integer jailGhosts; // number of ghosts in jail
		
		public Integer pillDistance; // nearest pill distance
		public Integer ppillDistance; // nearest power pill distance
		
		public Integer ghostDistance; // nearest ghost distance
		public Integer edibleGhostDistance; // nearest edible ghost distance
		
		public POS relativePosGhost; // relative position of nearest ghosts
		public POS relativePosEdibleGhost; // relative position of nearest edible ghosts
		
	}

	private MsPacManInfo info;

	public MsPacManInput(Game game, MsPacManInfo info) {
		super(game);
		this.info = info;
		parseInput();
	}

	@Override
	public void parseInput() {
		if(this.info == null)
            return;
		
		computeScore();
		computeLevel();
		computeMsPacMan();
		computeGhosts();
		computePills();
		computePowerPills();
		computeRelativePosGhost();
	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();

		description.setScore(this.info.score);
		description.setLevel(this.info.level);
		description.setIndex(this.info.index);
		description.setMove(this.info.move);
		description.setEdibleTime(this.info.edibleTime);
		description.setEdibleGhosts(this.info.edibleGhosts);
		description.setJailGhosts(this.info.jailGhosts);
		description.setPillDistance(this.info.pillDistance);
		description.setPpillDistance(this.info.ppillDistance);
		description.setGhostDistance(this.info.ghostDistance);
		description.setEdibleGhostDistance(this.info.edibleGhostDistance);
		description.setRelativePosGhost(this.info.relativePosGhost);
		description.setRelativePosEdibleGhost(this.info.relativePosEdibleGhost);

		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}

	private void computeScore() {
		this.info.score = this.game.getScore();
	}

	private void computeLevel() {
		this.info.level = this.game.getCurrentLevel();
	}
	
	private void computeMsPacMan() {
		this.info.index = this.game.getPacmanCurrentNodeIndex();
		this.info.move = this.game.getPacmanLastMoveMade();
	}

	private void computeGhosts() {
		this.info.ghostDistance = Integer.MAX_VALUE;
		this.info.edibleGhostDistance = Integer.MAX_VALUE;
		this.info.edibleTime = 0;
		this.info.edibleGhosts = 0;
		this.info.jailGhosts = 0;

		for (GHOST g : GHOST.values()) {
			if (this.game.getGhostLairTime(g) > 0)
				this.info.jailGhosts++;
			else {
				int ghost = this.game.getGhostCurrentNodeIndex(g);
				int distance = this.game.getShortestPathDistance(this.info.index, ghost);

				if (this.game.isGhostEdible(g)) {
					this.info.edibleGhosts++;

					this.info.edibleTime = this.game.getGhostEdibleTime(g);

					if (distance < this.info.edibleGhostDistance)
						this.info.edibleGhostDistance = distance;
				} else {
					if (distance < this.info.ghostDistance)
						this.info.ghostDistance = distance;
				}
			}
		}
	}

	private void computePowerPills() {
		this.info.ppillDistance = Integer.MAX_VALUE;
		for (int ppill : this.game.getActivePowerPillsIndices()) {
			int distance = this.game.getShortestPathDistance(this.info.index, ppill);
			if (distance < this.info.ppillDistance)
				this.info.ppillDistance = distance;
		}
	}

	private void computePills() {
		this.info.pillDistance = Integer.MAX_VALUE;
		for (int pill : this.game.getActivePillsIndices()) {
			int distance = this.game.getShortestPathDistance(this.info.index, pill);
			if (distance < this.info.pillDistance)
				this.info.pillDistance = distance;
		}
	}

	private void computeRelativePosGhost() {
		this.info.relativePosGhost = POS.NONE;
		this.info.relativePosEdibleGhost = POS.NONE;
		for (GHOST g : GHOST.values()) {
			if (this.game.getGhostLairTime(g) > 0)
				this.info.relativePosGhost = POS.NONE;
			else {
				if(this.game.isGhostEdible(g)) {
					if (fantasmaDelante(g, RANGO_DISTANCIA) && fantasmaDetras(g, RANGO_DISTANCIA)) {
						this.info.relativePosEdibleGhost = POS.BOTH;
					} else if (fantasmaDelante(g, RANGO_DISTANCIA)) {
						this.info.relativePosEdibleGhost = POS.FRONT;
					} else if (fantasmaDetras(g, RANGO_DISTANCIA)) {
						this.info.relativePosEdibleGhost = POS.BACK;
					} else {
						this.info.relativePosEdibleGhost = POS.NONE;
					}
				}
				else {
					if (fantasmaDelante(g, RANGO_DISTANCIA) && fantasmaDetras(g, RANGO_DISTANCIA)) {
						this.info.relativePosGhost = POS.BOTH;
					} else if (fantasmaDelante(g, RANGO_DISTANCIA)) {
						this.info.relativePosGhost = POS.FRONT;
					} else if (fantasmaDetras(g, RANGO_DISTANCIA)) {
						this.info.relativePosGhost = POS.BACK;
					} else {
						this.info.relativePosGhost= POS.NONE;
					}
				}
			}
		}
	}

	public boolean fantasmaDelante(GHOST ghost, int maxDistance) {
		if (this.game.getGhostLairTime(ghost) > 0)
			return false;

		int ghostIndex = this.game.getGhostCurrentNodeIndex(ghost);

		int[] pathToGhost = this.game.getShortestPath(this.info.index, ghostIndex, this.info.move);
		if (pathToGhost.length > 0 && pathToGhost.length <= maxDistance) {
			return true;
		}
		return false;
	}

	public boolean fantasmaDetras(GHOST ghost, int maxDistance) {
		if (this.game.getGhostLairTime(ghost) > 0)
			return false;

		int ghostIndex = this.game.getGhostCurrentNodeIndex(ghost);

		int[] pathToGhost = this.game.getShortestPath(ghostIndex, this.info.index, this.info.move.opposite());
		if (pathToGhost.length > 0 && pathToGhost.length <= maxDistance) {
			return true;
		}
		return false;
	}
}

package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.c2425.practica5.grupo02.POS;
import es.ucm.fdi.ici.cbr.CBRInput;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

@SuppressWarnings("rawtypes")
public class MsPacManInput extends CBRInput {

	private static final int RANGO_DISTANCIA = 40;

	private Integer score; // obtained score
	private Integer edibleTime; // time of edible ghost
	private Integer ppillDistance; // nearest power pill distance
	private Integer pillDistance; // nearest pill distance
	private Integer ghostDistance; // nearest ghost distance
	private Integer edibleGhostDistance; // nearest edible ghost distance
	private Integer edibleGhosts; // number of edible ghosts
	private Integer jailGhosts; // number of ghosts in jail
	private Enum relativePosGhost; // relative position of nearest ghost
	private Enum relativePosEdibleGhost; // relative position of nearest edible ghost

	private int mspacman; // pacman index

	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		this.mspacman = game.getPacmanCurrentNodeIndex();
		computeScore();
		computeGhosts();
		computePowerPills();
		computePills();
		computeRelativePosGhost(relativePosGhost);
		computeRelativePosGhost(relativePosEdibleGhost);
	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();

		description.setScore(this.score);
		description.setEdibleTime(this.edibleTime);
		description.setPpillDistance(this.ppillDistance);
		description.setPillDistance(this.pillDistance);
		description.setGhostDistance(this.ghostDistance);
		description.setEdibleGhostDistance(this.edibleGhostDistance);
		description.setEdibleGhosts(this.edibleGhosts);
		description.setJailGhosts(this.jailGhosts);
		description.setRelativePosGhost(this.relativePosGhost);
		description.setRelativePosEdibleGhost(this.relativePosEdibleGhost);

		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}

	private void computeScore() {
		this.score = this.game.getScore();
	}

	private void computeGhosts() {
		this.ghostDistance = Integer.MAX_VALUE;
		this.edibleGhostDistance = Integer.MAX_VALUE;
		this.edibleTime = 0;
		this.edibleGhosts = 0;
		this.jailGhosts = 0;

		for (GHOST g : GHOST.values()) {
			if (this.game.getGhostLairTime(g) > 0)
				this.jailGhosts++;
			else {
				int ghost = this.game.getGhostCurrentNodeIndex(g);
				int distance = this.game.getShortestPathDistance(this.mspacman, ghost);

				if (this.game.isGhostEdible(g)) {
					this.edibleGhosts++;

					this.edibleTime = this.game.getGhostEdibleTime(g);

					if (distance < this.edibleGhostDistance)
						this.edibleGhostDistance = distance;
				} else {
					if (distance < this.ghostDistance)
						this.ghostDistance = distance;
				}
			}
		}
	}

	private void computePowerPills() {
		this.ppillDistance = Integer.MAX_VALUE;
		for (int ppill : this.game.getActivePowerPillsIndices()) {
			int distance = this.game.getShortestPathDistance(this.mspacman, ppill);
			if (distance < this.ppillDistance)
				this.ppillDistance = distance;
		}
	}

	private void computePills() {
		this.pillDistance = Integer.MAX_VALUE;
		for (int pill : this.game.getActivePillsIndices()) {
			int distance = this.game.getShortestPathDistance(this.mspacman, pill);
			if (distance < this.pillDistance)
				this.pillDistance = distance;
		}
	}

	private void computeRelativePosGhost(Enum relative) {
		for (GHOST g : GHOST.values()) {
			if (fantasmaDelante(g, RANGO_DISTANCIA) && fantasmaDetras(g, RANGO_DISTANCIA)) {
				relative = POS.BOTH;
			} else if (fantasmaDelante(g, RANGO_DISTANCIA)) {
				relative = POS.FRONT;
			} else if (fantasmaDetras(g, RANGO_DISTANCIA)) {
				relative = POS.BACK;
			} else {
				relative = POS.NONE;
			}
		}
	}

	public boolean fantasmaDelante(GHOST ghost, int maxDistance) {
		if (game.getGhostLairTime(ghost) > 0)
			return false;

		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
		MOVE pacmanMove = game.getPacmanLastMoveMade();

		int[] pathToGhost = game.getShortestPath(pacmanIndex, ghostIndex, pacmanMove);
		if (pathToGhost.length > 0 && pathToGhost.length <= maxDistance) {
			return true;
		}
		return false;
	}

	public boolean fantasmaDetras(GHOST ghost, int maxDistance) {
		if (game.getGhostLairTime(ghost) > 0)
			return false;

		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
		MOVE pacmanMove = game.getPacmanLastMoveMade();

		int[] pathToGhost = game.getShortestPath(ghostIndex, pacmanIndex, pacmanMove.opposite());
		if (pathToGhost.length > 0 && pathToGhost.length <= maxDistance) {
			return true;
		}
		return false;
	}
}

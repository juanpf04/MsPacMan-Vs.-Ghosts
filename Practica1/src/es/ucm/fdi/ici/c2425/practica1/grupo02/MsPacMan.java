package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.awt.Color;
import java.util.function.Predicate;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.GameView;

public class MsPacMan extends PacmanController {

	private final static Color[] COLOURS = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };
	private final static int LIMIT_DISTANCE = 50;

	private int currentNode;
	private MOVE lastMove;

	@Override
	public MOVE getMove(Game game, long timeDue) {
		this.currentNode = game.getPacmanCurrentNodeIndex();
		this.lastMove = game.getPacmanLastMoveMade();

		GHOST nearestGhost = this.getNearestChasingGhost(game);

		if (nearestGhost != null) {
			int ghostNode = game.getGhostCurrentNodeIndex(nearestGhost);

			GameView.addPoints(game, COLOURS[nearestGhost.ordinal()],
					game.getShortestPath(ghostNode, this.currentNode, game.getGhostLastMoveMade(nearestGhost)));

			return game.getNextMoveAwayFromTarget(this.currentNode, ghostNode, this.lastMove, DM.EUCLID);

		}

		nearestGhost = this.getNearestEdibleGhost(game);

		if (nearestGhost != null) {
			int ghostNode = game.getGhostCurrentNodeIndex(nearestGhost);

			GameView.addPoints(game, Color.BLUE, game.getShortestPath(this.currentNode, ghostNode, this.lastMove));

			return game.getNextMoveTowardsTarget(this.currentNode, ghostNode, this.lastMove, DM.EUCLID);
		}

		int nearestPill = this.getNearestPill(game);

		GameView.addLines(game, Color.WHITE, this.currentNode, nearestPill);

		return game.getNextMoveTowardsTarget(this.currentNode, nearestPill, this.lastMove, DM.EUCLID);
	}

	private GHOST getNearestChasingGhost(Game game) {
		return this.getNearestGhost(game, g -> !game.isGhostEdible(g));
	}

	private GHOST getNearestEdibleGhost(Game game) {
		return this.getNearestGhost(game, g -> game.isGhostEdible(g));
	}

	private GHOST getNearestGhost(Game game, Predicate<GHOST> filter) {
		int nearestDistance = 0;
		GHOST nearestGhost = null;

		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostLairTime(ghost) <= 0) {
				int distance = game.getShortestPathDistance(this.currentNode, game.getGhostCurrentNodeIndex(ghost),
						this.lastMove);

				if (filter.test(ghost) && distance < LIMIT_DISTANCE
						&& (nearestGhost == null || distance < nearestDistance)) {
					nearestDistance = distance;
					nearestGhost = ghost;
				}
			}
		}

		return nearestGhost;
	}

	private int getNearestPill(Game game) {
		int nearestDistance = 0;
		int nearestPill = -1;

		for (int pill : game.getActivePillsIndices()) {
			int distance = game.getShortestPathDistance(this.currentNode, pill, this.lastMove);

			if (nearestPill == -1 || distance < nearestDistance) {
				nearestDistance = distance;
				nearestPill = pill;
			}
		}

		return nearestPill;
	}

	public String getName() {
		return "Fantasmikos";
	}

}
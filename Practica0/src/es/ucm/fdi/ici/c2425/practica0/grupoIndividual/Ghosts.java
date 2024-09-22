package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class Ghosts extends GhostController {

	private final static int LIMIT_DISTANCE = 50;

	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private MOVE[] allMoves = MOVE.values();
	private Random rnd = new Random();

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		this.moves.clear();

		int mspacman = game.getPacmanCurrentNodeIndex();

		for (GHOST ghost : GHOST.values()) {
			if (game.doesGhostRequireAction(ghost)) {

				if (game.isGhostEdible(ghost) || isPacmanCloseToPowerPill(game))
					this.moves.put(ghost,
							game.getNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost), mspacman, DM.PATH));
				else {
					if (this.rnd.nextFloat() < 0.9)
						this.moves.put(ghost,
								game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), mspacman, DM.PATH));
					else
						this.moves.put(ghost, this.allMoves[this.rnd.nextInt(this.allMoves.length)]);

				}
			}
		}
		return this.moves;
	}

	private boolean isPacmanCloseToPowerPill(Game game) {
		for (int powerPill : game.getActivePowerPillsIndices()) {
			if (game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), powerPill) < LIMIT_DISTANCE)
				return true;
		}

		return false;
	}

	public String getName() {
		return "JGhosts";
	}
}

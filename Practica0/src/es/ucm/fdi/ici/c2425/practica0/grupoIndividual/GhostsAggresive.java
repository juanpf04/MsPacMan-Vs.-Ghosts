package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.util.EnumMap;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class GhostsAggresive extends GhostController {

	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		moves.clear();

		int mspacman = game.getPacmanCurrentNodeIndex();
		
		for (GHOST ghostType : GHOST.values()) {
			if (game.doesGhostRequireAction(ghostType)) {
				int ghost = game.getGhostCurrentNodeIndex(ghostType);
				moves.put(ghostType, game.getApproximateNextMoveTowardsTarget(ghost, mspacman,
						game.getGhostLastMoveMade(ghostType), DM.PATH));
			}
		}

		return moves;
	}

	public String getName() {
		return "GhostsAggresive";
	}
}

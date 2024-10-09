package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.awt.Color;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.GameView;

public class MsPacManRunAway extends PacmanController {

	private Color[] colours = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };
	
	@Override
	public MOVE getMove(Game game, long timeDue) {

		int mspacman = game.getPacmanCurrentNodeIndex();
		GHOST nearestGhostType = null;
		int nearestGhost = 0;
		int nearestDistance = 0;

		for (GHOST ghostType : GHOST.values()) {
			int ghost = game.getGhostCurrentNodeIndex(ghostType);
			int distance = game.getShortestPathDistance(ghost, mspacman, game.getGhostLastMoveMade(ghostType));
			if (nearestGhostType == null || distance < nearestDistance) {
				nearestGhostType = ghostType;
				nearestGhost = ghost;
				nearestDistance = distance;
			}
		}
	
//		if (game.getGhostLairTime(nearestGhostType) <= 0)
//			GameView.addPoints(game, colours[nearestGhostType.ordinal()],
//					game.getShortestPath(nearestGhost, mspacman, game.getGhostLastMoveMade(nearestGhostType)));

		return game.getApproximateNextMoveAwayFromTarget(mspacman, nearestGhost, game.getPacmanLastMoveMade(), DM.PATH);
	}

	public String getName() {
		return "MsPacManRunAway";
	}

}

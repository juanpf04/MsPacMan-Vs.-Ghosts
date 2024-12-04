
import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManRunAway extends PacmanController {
	
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

		return game.getApproximateNextMoveAwayFromTarget(mspacman, nearestGhost, game.getPacmanLastMoveMade(), DM.PATH);
	}

	public String getName() {
		return "MsPacManRunAway";
	}

}

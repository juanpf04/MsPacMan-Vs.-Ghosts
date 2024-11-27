package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayAction implements Action {

	private GHOST ghost;

	public RunAwayAction(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(this.ghost))
			return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(this.ghost),
					game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(this.ghost), DM.PATH);

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return "Run away";
	}
}

package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInfo;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class DisperseAction implements Action {

	private GHOST ghost;
	private GhostsInfo info;
	private int quitar;

	public DisperseAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) { // TODO
		if (game.doesGhostRequireAction(ghost)) {
			int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
			int pacmanIndex = game.getPacmanCurrentNodeIndex();
			MOVE lastMove = game.getGhostLastMoveMade(ghost);

			MOVE awayFromPacman = game.getApproximateNextMoveAwayFromTarget(ghostIndex, pacmanIndex, lastMove, DM.PATH);
			MOVE towardsPacman = game.getApproximateNextMoveTowardsTarget(ghostIndex, pacmanIndex, lastMove, DM.PATH);
			MOVE disperse = game.getApproximateNextMoveAwayFromTarget(ghostIndex, this.info.getNearestGhost(ghost),
					lastMove, DM.PATH);

			return towardsPacman == disperse ? awayFromPacman : disperse;
		}

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return "Disperse";
	}
}

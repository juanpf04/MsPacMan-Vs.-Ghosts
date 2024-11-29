package es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.GhostsInput.GhostsInfo;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToGhostAction implements Action {

	private GHOST ghost;
	private GhostsInfo info;

	public GoToGhostAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost,
				game.isGhostEdible(ghost) ? this.info.nearestGhost : this.info.nearestEdibleGhostToPacman);
		return action.execute(game);
	}

	@Override
	public String getActionId() {
		return "GoToGhost";
	}
}

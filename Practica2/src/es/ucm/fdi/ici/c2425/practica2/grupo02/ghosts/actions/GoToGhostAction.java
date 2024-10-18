package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInfo;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToGhostAction implements Action {

	private GHOST ghost;
	private GhostsInfo info;
	private boolean isEdible;

	public GoToGhostAction(GHOST ghost, GhostsInfo info, boolean isEdible) {
		this.ghost = ghost;
		this.info = info;
		this.isEdible = isEdible;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost,
				this.isEdible ? this.info.getNearestGhost() : this.info.getNearestEdibleGhostToPacman());
		return action.execute(game);
	}

	@Override
	public String getActionId() {
		return "Go to ghost";
	}
}

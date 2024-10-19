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

	/**
	 * Go to ghost action
	 * 
	 * @param ghost    ghost who will go to
	 * @param info     ghosts info
	 * @param isEdible true if the ghost who will go to is edible
	 */
	public GoToGhostAction(GHOST ghost, boolean isEdible, GhostsInfo info) {
		this.ghost = ghost;
		this.isEdible = isEdible;
		this.info = info;
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

package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInfo;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToPowePillAction implements Action {

	private GHOST ghost;
	private GhostsInfo info;

	public GoToPowePillAction(GHOST ghost, GhostsInfo info) {
		this.ghost = ghost;
		this.info = info;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost, this.info.getNearestPPill(this.ghost));
		return action.execute(game);
	}

	@Override
	public String getActionId() {
		return "Go to power pill";
	}
}

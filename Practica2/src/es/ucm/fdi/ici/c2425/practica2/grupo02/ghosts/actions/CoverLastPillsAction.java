package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CoverLastPillsAction implements Action {

	private GHOST ghost;
	private int implementation;

	public CoverLastPillsAction(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost, this.getNearestPill(this.ghost));
		return action.execute(game);
	}

	private int getNearestPill(GHOST ghost) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getActionId() {
		return "Cover last pills";
	}
}

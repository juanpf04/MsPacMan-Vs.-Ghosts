package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseMsPacManAction implements Action {

	private GHOST ghost;

	public ChaseMsPacManAction(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public MOVE execute(Game game) {
		Action action = new GoToAction(this.ghost, game.getPacmanCurrentNodeIndex());
		return action.execute(game);
	}

	@Override
	public String getActionId() {
		return "Chase";
	}
}

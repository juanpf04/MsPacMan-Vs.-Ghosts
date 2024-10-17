package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class LairAction implements Action {

	GHOST ghost;

	public LairAction(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public MOVE execute(Game game) {
		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return ghost + " in lair";
	}
}

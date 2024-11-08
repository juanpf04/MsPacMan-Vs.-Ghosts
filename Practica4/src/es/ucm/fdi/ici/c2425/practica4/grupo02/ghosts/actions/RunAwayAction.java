package es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions;

import java.util.Random;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayAction implements Action {

	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();

	public RunAwayAction(GHOST ghost) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public MOVE execute(Game game) {
		return allMoves[rnd.nextInt(allMoves.length)];
	}

	@Override
	public String getActionId() {
		return "Runaway";
	}

}

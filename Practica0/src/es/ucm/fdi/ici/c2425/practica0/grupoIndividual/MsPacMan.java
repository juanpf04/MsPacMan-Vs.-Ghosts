package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.awt.Color;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	private final static Color[] COLOURS = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };
	private final static int LIMIT_DISTANCE = 20;
	
	@Override
	public MOVE getMove(Game game, long timeDue) {

		

		return null;
	}

	public String getName() {
		return "JPacMan";
	}

}

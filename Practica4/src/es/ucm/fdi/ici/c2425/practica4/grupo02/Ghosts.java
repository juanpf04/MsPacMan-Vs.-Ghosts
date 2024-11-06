package es.ucm.fdi.ici.c2425.practica4.grupo02;

import java.util.EnumMap;

import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {



	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Team 02");

	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		for (GHOST ghost : GHOST.values()) {
			result.put(ghost, MOVE.NEUTRAL);
		}

		return result;
	}

}

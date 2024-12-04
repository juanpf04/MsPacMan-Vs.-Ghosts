package es.ucm.fdi.ici.c2425.practica5.grupo02;

import java.util.EnumMap;

import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.ici.c2425.practica5.grupo02.ghosts.GhostsCBRengine;
import es.ucm.fdi.ici.c2425.practica5.grupo02.ghosts.GhostsStorageManager;
import es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.MsPacManInput;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	GhostsCBRengine cbrEngine;
	GhostsStorageManager storageManager;

	public Ghosts() {
		this.storageManager = new GhostsStorageManager();
		cbrEngine = new GhostsCBRengine(storageManager);
	}

	@Override
	public void preCompute(String opponent) {
		cbrEngine.setOpponent(opponent);
		try {
			cbrEngine.configure();
			cbrEngine.preCycle();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void postCompute() {
		try {
			cbrEngine.postCycle();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		for (GHOST g : GHOST.values())
			result.put(g, MOVE.NEUTRAL);

		// This implementation only computes a new action when MsPacMan is in a
		// junction.
		// This is relevant for the case storage policy
		if (!game.isJunction(game.getPacmanCurrentNodeIndex()))
			return result;

		try {
			MsPacManInput input = new MsPacManInput(game);
			input.parseInput();
			storageManager.setGame(game);
			cbrEngine.cycle(input.getQuery());
			MOVE move = cbrEngine.getSolution();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

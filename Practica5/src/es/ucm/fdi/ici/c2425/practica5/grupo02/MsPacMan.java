package es.ucm.fdi.ici.c2425.practica5.grupo02;

import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.MsPacManCBRengine;
import es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.MsPacManStorageManager;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	private MsPacManCBRengine cbrEngine;
	private MsPacManStorageManager storageManager;

	public MsPacMan() {
		this.setName("JPacman");
		this.setTeam("Team 02");
		
		this.storageManager = new MsPacManStorageManager();
		this.cbrEngine = new MsPacManCBRengine(this.storageManager);
	}

	@Override
	public void preCompute(String opponent) {
		this.cbrEngine.setOpponent(opponent);
		try {
			this.cbrEngine.configure();
			this.cbrEngine.preCycle();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void postCompute() {
		try {
			this.cbrEngine.postCycle();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		MOVE move = MOVE.NEUTRAL;
		try {
			// This implementation only computes a new action when MsPacMan is in a
			// junction.
			// This is relevant for the case storage policy
			if (game.isJunction(game.getPacmanCurrentNodeIndex())) {
				MsPacManInput input = new MsPacManInput(game);
				this.storageManager.setGame(game);
				this.cbrEngine.cycle(input.getQuery());

				move = this.cbrEngine.getSolution();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return move;
	}

}

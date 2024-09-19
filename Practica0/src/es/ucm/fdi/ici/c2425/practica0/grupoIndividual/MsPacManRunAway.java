package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.awt.Color;
import java.util.Random;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.GameView;

public class MsPacManRunAway extends PacmanController {

	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();

	private Color[] colours = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };

	@Override
	public MOVE getMove(Game game, long timeDue) {

		// Show lines to Pills
//    	int[] activePowerPills = game.getActivePillsIndices();
//    	
//    	for(int i = 0; i< activePowerPills.length;i++) {
//    		GameView.addLines(game, Color.CYAN, game.getPacmanCurrentNodeIndex(), activePowerPills[i]);
//    	}

		// Show way to ghosts
		for (GHOST ghostType : GHOST.values()) {
			int ghost = game.getGhostCurrentNodeIndex(ghostType);
			int mspacman = game.getPacmanCurrentNodeIndex();
			if (game.getGhostLairTime(ghostType) <= 0)
				GameView.addPoints(game, colours[ghostType.ordinal()],
						game.getShortestPath(ghost, mspacman, game.getGhostLastMoveMade(ghostType)));
		}

		return allMoves[rnd.nextInt(allMoves.length)];
	}

	public String getName() {
		return "MsPacManRandom";
	}

}

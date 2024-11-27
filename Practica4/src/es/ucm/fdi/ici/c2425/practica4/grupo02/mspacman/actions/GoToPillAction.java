package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;

import java.util.Random;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.methods.Movimientos;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToPillAction implements Action {

	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();

	@Override
	public String getActionId() {
		return "GoToPill";
	}

	@Override
	public MOVE execute(Game game) {
		// return allMoves[rnd.nextInt(allMoves.length)];
		MOVE move = moveOfPillNearestToPacmanValidate(game);
		return move != null ? move : allMoves[rnd.nextInt(allMoves.length)];
	}

	private MOVE moveOfPillNearestToPacmanValidate(Game game) {
		Movimientos movimientos = new Movimientos();

		int pacmanNode = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		MOVE sol = null;

		while (pacmanNode != -1) {
			if (game.getActivePillsIndices()[pacmanNode] != -1) {
				return lastMove;
			}

			pacmanNode = movimientos.sumarMovimientos(game, pacmanNode, lastMove);
		}

		pacmanNode = game.getPacmanCurrentNodeIndex();
		MOVE oppositeMove = lastMove.opposite();

		while (pacmanNode != -1) {
			if (game.getActivePillsIndices()[pacmanNode] != -1) {
				return oppositeMove;
			}

			pacmanNode = movimientos.sumarMovimientos(game, pacmanNode, oppositeMove);
		}

		return sol;
	}
}

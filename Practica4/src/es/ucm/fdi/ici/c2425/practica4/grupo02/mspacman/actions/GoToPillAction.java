package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.methods.Movimientos;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GoToPillAction implements Action {

	@Override
	public String getActionId() {
		return "GoToPill";
	}

	@Override
	public MOVE execute(Game game) {
		try {
			MOVE move = moveOfPillNearestToPacmanValidate(game);
			return move != null ? move : MOVE.NEUTRAL;
		} catch (Exception e) {
			return MOVE.NEUTRAL;
		}
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

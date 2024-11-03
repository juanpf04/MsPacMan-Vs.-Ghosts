package es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class AvoidCorner implements RulesAction{

    @Override
    public String getActionId() {
        return "Pacman evita las esquinas";}

    @Override
    public MOVE execute(Game game) {
        int currentNode = game.getPacmanCurrentNodeIndex();
        MOVE lastMove = game.getPacmanLastMoveMade();

        MOVE[] possibleMoves = game.getPossibleMoves(currentNode, lastMove);
        for (MOVE move : possibleMoves) {
            int nextNode = game.getNeighbour(currentNode, move);
            if (!isCorner(nextNode, game)) {
                return move;
            }
        }
        return MOVE.NEUTRAL; // Si todos los movimientos llevan a una esquina, retornar NEUTRAL
     
    }

    private boolean isCorner(int node, Game game) {
         return game.getNeighbouringNodes(node).length < 2;
    }

    @Override
    public void parseFact(Fact actionFact) {
         try {
			Value value = actionFact.getSlotValue("avoidcorner");
			if(value == null)
				return;
		} catch (JessException e) {
			e.printStackTrace();
		}    
    }

}

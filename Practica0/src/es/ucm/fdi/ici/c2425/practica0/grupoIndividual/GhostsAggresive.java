package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.util.EnumMap;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class GhostsAggresive extends GhostController {
    private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        moves.clear();
        for (GHOST ghostType : GHOST.values()) {
            if (game.doesGhostRequireAction(ghostType)) {
                int ghostNodeIndex = game.getGhostCurrentNodeIndex(ghostType);
                int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
                MOVE lastGhostMove = game.getGhostLastMoveMade(ghostType);
                MOVE nextMove = game.getApproximateNextMoveTowardsTarget(
                    ghostNodeIndex,
                    pacmanNodeIndex,
                    lastGhostMove,
                    DM.PATH);
                moves.put(ghostType, nextMove);
            }
        }
        return moves;
    }
    
    public String getName() {
    	return "GhostsAggresive";
    }
}

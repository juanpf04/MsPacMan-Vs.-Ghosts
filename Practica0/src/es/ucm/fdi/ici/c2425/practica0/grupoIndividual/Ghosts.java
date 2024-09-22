package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class Ghosts extends GhostController {
    private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        moves.clear();
        for (GHOST ghostType : GHOST.values()) {
            if (game.doesGhostRequireAction(ghostType)) {
                int ghostNodeIndex = game.getGhostCurrentNodeIndex(ghostType);
                int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
                MOVE lastGhostMove = game.getGhostLastMoveMade(ghostType);

                if (isEdible(game,ghostType) || isPMCloseToPPill(game, 15)) {
                    MOVE nextMove = game.getApproximateNextMoveTowardsTarget(
                        ghostNodeIndex,
                        pacmanNodeIndex,
                        lastGhostMove,
                        DM.PATH).opposite();
                    moves.put(ghostType,nextMove);
                } else {
					if(Math.random() < 0.9) {
                        MOVE nextMove = game.getNextMoveTowardsTarget(
                            ghostNodeIndex, 
                            pacmanNodeIndex,
                            DM.PATH);
						moves.put(ghostType, nextMove);
					}
					else {
                        Random rnd = new Random();
                        MOVE nextMove = MOVE.values()[rnd.nextInt(MOVE.values().length)];
						moves.put(ghostType, nextMove);
					}
				}
            }
        }
        return moves;
    }
    

    private boolean isEdible(Game game, GHOST ghost) {
        return game.getGhostEdibleTime(ghost) > 0;
    }


    private boolean isPMCloseToPPill(Game game, double limit) {
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
        int[] ppills = game.getActivePowerPillsIndices(); 
        for (int ppill : ppills) {
            double dist = game.getDistance(pacmanNodeIndex, ppill, DM.PATH);
            if (dist < limit) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
    	return "Ghosts";
    }
}

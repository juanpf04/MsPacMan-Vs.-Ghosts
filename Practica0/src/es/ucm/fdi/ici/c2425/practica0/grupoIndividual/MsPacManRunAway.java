package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManRunAway extends PacmanController{
	

    @Override
    public MOVE getMove(Game game, long timeDue) {
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
        int ghostNodeIndex = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
        double minDist = game.getDistance(pacmanNodeIndex, ghostNodeIndex, DM.PATH);

        // find node of closest ghost
        for (GHOST ghostType : GHOST.values()) {
            int temp = game.getGhostCurrentNodeIndex(ghostType);
            double currDist = game.getDistance(pacmanNodeIndex, temp, DM.PATH);
            if (currDist < minDist) {
                minDist = currDist;
            }
        }

        // find next move toward closest ghost
        MOVE lastMove = game.getPacmanLastMoveMade();
        MOVE nextMove = game.getApproximateNextMoveTowardsTarget(
            pacmanNodeIndex,
            ghostNodeIndex,
            lastMove,
            DM.PATH);

        // invert
        return nextMove.opposite();
    }

    public String getName() {
    	return "MsPacManRunAway";
    }
    
}
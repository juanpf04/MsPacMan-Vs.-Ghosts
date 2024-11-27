package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;

import java.util.Random;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayFromPINKY implements Action{

    private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();
    @Override
    public String getActionId() {
        return "RunawayFromPINKY";
    }

    @Override
    public MOVE execute(Game game) {
        int posPinky = game.getGhostCurrentNodeIndex(GHOST.PINKY);

		if(posPinky == -1) {
			return allMoves[rnd.nextInt(allMoves.length)];
		}
		
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		MOVE lastMove   = game.getPacmanLastMoveMade();
		
		return game.getNextMoveAwayFromTarget(pacmanNode, posPinky, lastMove, DM.PATH);
    }

}

package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions;

import java.util.Random;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayFromSUE implements Action{

    private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();
    @Override
    public String getActionId() {
        return "RunawayFromSUE";
    }

    @Override
    public MOVE execute(Game game) {
        int posSue = game.getGhostCurrentNodeIndex(GHOST.SUE);

		if(posSue == -1) {
			return allMoves[rnd.nextInt(allMoves.length)];
		}
		
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		MOVE lastMove   = game.getPacmanLastMoveMade();
		
		return game.getNextMoveAwayFromTarget(pacmanNode, posSue, lastMove, DM.PATH);
    }

}

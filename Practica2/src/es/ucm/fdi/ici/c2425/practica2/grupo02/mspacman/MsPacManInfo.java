package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInfo {
    private Game game;

    public MsPacManInfo(Game game){
        this.game = game;
    }

    public int getPacmanCurrentNodeIndex() {
        return game.getPacmanCurrentNodeIndex();
    }

    public MOVE getPacmanLastMoveMade() {
        return game.getPacmanLastMoveMade();
    }

    public int getGhostCurrentNodeIndex(GHOST ghost) {
        return game.getGhostCurrentNodeIndex(ghost);
    }

    public int getGhostEdibleTime(GHOST ghost) {
        return game.getGhostEdibleTime(ghost);
    }

    public int[] getActivePillsIndices() {
        return game.getActivePillsIndices();
    }

    public int[] getActivePowerPillsIndices() {
        return game.getActivePowerPillsIndices();
    }

    public int getShortestPathDistance(int fromNodeIndex, int toNodeIndex) {
        return game.getShortestPathDistance(fromNodeIndex, toNodeIndex);
    }

    public MOVE getNextMoveTowardsTarget(int fromNodeIndex, int toNodeIndex, MOVE lastMoveMade) {
        return game.getNextMoveTowardsTarget(fromNodeIndex, toNodeIndex, lastMoveMade, DM.PATH);
    }

}

package es.ucm.fdi.ici.c2425.practica0.grupoIndividual;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

import java.awt.Color;
import java.util.Arrays;

public class MsPacMan extends PacmanController{
	

    @Override
    public MOVE getMove(Game game, long timeDue) {
        // useful parameters
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
        MOVE lastMove = game.getPacmanLastMoveMade();
        
        // nearest chasing ghost
        GHOST nearestGhost = getNearestChasingGhost(game, 20);
        if (nearestGhost != null) {
            int ghostNodeIndex = game.getGhostCurrentNodeIndex(nearestGhost);
            GameView.addLines(game, Color.RED, pacmanNodeIndex, ghostNodeIndex);
            return game.getApproximateNextMoveTowardsTarget(
                pacmanNodeIndex,
                ghostNodeIndex,
                lastMove,
                DM.PATH).opposite();
        }

        // nearest edible ghost
        nearestGhost = getNearestEdibleGhost(game, 20);
        if (nearestGhost != null) {
            int ghostNodeIndex = game.getGhostCurrentNodeIndex(nearestGhost);
            GameView.addLines(game, Color.GREEN, pacmanNodeIndex, ghostNodeIndex);
            return game.getApproximateNextMoveTowardsTarget(
                pacmanNodeIndex,
                ghostNodeIndex,
                lastMove,
                DM.PATH);
        }

        // move towards pill
        int nearestPill = getNearestPill(game);
        return game.getApproximateNextMoveTowardsTarget(
            pacmanNodeIndex,
            nearestPill,
            lastMove,
            DM.PATH);
    }


    /**
     * Return nearest chasing Ghost within given distance limit
     * @param game
     * @param limit
     * @return
     */
    private GHOST getNearestChasingGhost(Game game, double limit) {
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
        double minDist = limit;
        GHOST nearestGhost = null;

        // find closest ghost within limit (if exists)
        for (GHOST ghostType : GHOST.values()) {
            int temp = game.getGhostCurrentNodeIndex(ghostType);
            double currDist = game.getDistance(pacmanNodeIndex, temp, DM.PATH);
            if (currDist < minDist) {
                if (!isEdible(game,ghostType)) {
                    minDist = currDist;
                    nearestGhost = ghostType;
                }
            }
        }

        return nearestGhost;
    }


    /**
     * Return nearest edible Ghost within given distance limit
     * @param game
     * @param limit
     * @return
     */
    private GHOST getNearestEdibleGhost(Game game, double limit) {
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
        double minDist = limit;
        GHOST nearestGhost = null;

        // find closest ghost within limit (if exists)
        for (GHOST ghostType : GHOST.values()) {
            int temp = game.getGhostCurrentNodeIndex(ghostType);
            double currDist = game.getDistance(pacmanNodeIndex, temp, DM.PATH);
            if (currDist < minDist) {
                if (isEdible(game,ghostType)) {
                    minDist = currDist;
                    nearestGhost = ghostType;
                }
            }
        }

        return nearestGhost;
    }

    
    private boolean isEdible(Game game, GHOST ghost) {
        return game.getGhostEdibleTime(ghost) > 0;
    }


    private int getNearestPill(Game game) {
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();

        int[] array1 = game.getActivePillsIndices();
        int[] array2 = game.getActivePowerPillsIndices();
        int[] pills = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, pills, array1.length, array2.length);

        int smallest = pills[0];
        double minDist = game.getDistance(pacmanNodeIndex, pills[0], DM.PATH);

        for (int i = 1; i < pills.length; i++) {
            double currDist = game.getDistance(pacmanNodeIndex, pills[i], DM.PATH);
            if (currDist < minDist) {
                minDist = currDist;
                smallest = pills[i];
            }
        }

        return smallest;

    }


    public String getName() {
    	return "MsPacMan";
    }
    
}
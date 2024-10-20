package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman;

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

    public MOVE[] getPossibleMoves(int pacmanNode, MOVE pacmanLastMoveMade) {
       return game.getPossibleMoves(pacmanNode, pacmanLastMoveMade);
    }

}

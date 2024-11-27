package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.methods;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Movimientos {

    public int sumarMovimientos(Game game, int currentIndex, MOVE move){
        int[] neighbours = game.getNeighbouringNodes(currentIndex);

        switch(move)
        {
            case UP:
                int min = minimunOfVector(neighbours);
                return min != -1 && min != currentIndex - 1? min : -1;
            case RIGHT:
                return currentIndex + 1 != -1 ? currentIndex + 1 : -1;
            case DOWN:
                int max = maximumOfVector(neighbours);
                return max != -1 && max != currentIndex + 1? max : -1;
            case LEFT:
                return currentIndex - 1 != -1 ? currentIndex - 1 : -1;
            default:
                return -1;
        }
    }

    private int maximumOfVector(int[] v){
        int max = v[0];
        for(int i = 1; i < v.length; i++){
            if(v[i] > max){
                max = v[i];
            }
        }
        return max;
    }

    private int minimunOfVector(int[] v){
        int min = v[0];
        for(int i = 1; i < v.length; i++){
            if(v[i] < min){
                min = v[i];
            }
        }
        return min;
    }

}

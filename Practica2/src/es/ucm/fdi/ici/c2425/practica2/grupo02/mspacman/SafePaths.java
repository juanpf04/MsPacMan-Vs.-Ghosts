package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman;

import pacman.game.Constants.MOVE;
import java.util.HashMap;
import java.util.Map;

public class SafePaths {
    private Map<MOVE, Integer[]> safePaths;

    public SafePaths() {
        this.safePaths = new HashMap<>();
    }

    public void addSafePath(MOVE move, int ghostCount, int nPills) {
        Integer[] values = {ghostCount, nPills};
        this.safePaths.put(move,values);
    }

    public Map<MOVE, Integer[]> getSafePaths() {
        return this.safePaths;
    }

    public boolean isEmpty() {
        return this.safePaths.isEmpty();
    }

    public int size() {
        return this.safePaths.size();
    }
}
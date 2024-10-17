package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman;

import pacman.game.Constants.MOVE;
import java.util.HashMap;
import java.util.Map;

public class SafePaths {
    private Map<MOVE, Integer> safePaths;

    public SafePaths() {
        this.safePaths = new HashMap<>();
    }

    public void addSafePath(MOVE move, int ghostCount) {
        this.safePaths.put(move, ghostCount);
    }

    public Map<MOVE, Integer> getSafePaths() {
        return this.safePaths;
    }

    public boolean isEmpty() {
        return this.safePaths.isEmpty();
    }
}
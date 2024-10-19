package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman;

import pacman.game.Constants.MOVE;
import java.util.HashMap;
import java.util.Map;

public class SafePaths {
    private static Map<MOVE, Integer> safePaths;
    private static Map<MOVE, Integer> pillsForPath;

    public SafePaths() {
        SafePaths.safePaths = new HashMap<>();
        SafePaths.pillsForPath = new HashMap<>();
    }

    public static void addSafePath(MOVE move, int score) {
        safePaths.put(move, score);
    }

    public static void addPillsForPath(MOVE move, int pills) {
        pillsForPath.put(move, pills);
    }

    public static Map<MOVE, Integer> getSafePaths() {
        return safePaths;
    }

    public static Map<MOVE, Integer> getPillsForPath() {
        return pillsForPath;
    }

    public static boolean isEmpty() {
        return safePaths.isEmpty();
    }


    public static void clear() {
        safePaths.clear();
    }

    public int size() {
       return safePaths.size();
    }
}
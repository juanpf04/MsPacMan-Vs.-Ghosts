

import es.ucm.fdi.ici.c2425.practica4.grupoYY.MsPacMan;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;
import pacman.game.internal.POType;


public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setGhostPO(true)
                .setPacmanPO(true)
                .setPacmanPOvisual(true)
                .setGhostsPOvisual(false)
                .setPOType(POType.RADIUS)
                .setSightLimit(30)
                .setVisual(true)
                .setScaleFactor(2.5)
                .build();

        PacmanController pacMan = new MsPacMan();
        GhostController ghosts = new GhostsRandom();
        
        System.out.println( 
        		executor.runGame(pacMan, ghosts, 40)
        );
        
    }
}

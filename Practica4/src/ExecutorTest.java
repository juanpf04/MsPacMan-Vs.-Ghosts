

import es.ucm.fdi.ici.c2425.practica4.grupo02.Ghosts;
import es.ucm.fdi.ici.c2425.practica4.grupo02.MsPacMan;
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
                .setPacmanPOvisual(false)
                .setGhostsPOvisual(true)
                .setPOType(POType.LOS)
                .setSightLimit(100)
                .setVisual(true)
                .setScaleFactor(2.5)
                .build();

        PacmanController pacMan = new MsPacManRandom();
        GhostController ghosts = new Ghosts();
        
        System.out.println( 
        		executor.runGame(pacMan, ghosts, 40)
        );
        
    }
}

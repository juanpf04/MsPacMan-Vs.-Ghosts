import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.GhostsRandom;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.MsPacManRandom;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setVisual(true)
                .setScaleFactor(2.5)
                .build();

        PacmanController pacMan = new MsPacManRandom();
        GhostController ghosts = new GhostsRandom();
        // dfd
        System.out.println( 
            executor.runGame(pacMan, ghosts, 30) //last parameter defines speed
        );     
    }
	
}

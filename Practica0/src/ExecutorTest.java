import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.GhostsAggresive;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.GhostsRandom;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.MsPacManRandom;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.MsPacManRunAway;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.MsPacMan;
//import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.Ghost;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.controllers.PacmanController;

public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setVisual(true)
                .setScaleFactor(2.5)
                .build();

        PacmanController pacMan = new MsPacMan();
        // PacmanController pacMan = new MsPacManRunAway();
        //PacmanController pacMan = new HumanController(new KeyBoardInput());
        GhostController ghosts = new GhostsRandom();
        // GhostController ghosts = new GhostsAggresive();
        System.out.println( 
            executor.runGame(pacMan, ghosts, 30) //last parameter defines speed
        );     
    }
	
}

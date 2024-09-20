import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.Ghosts;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.GhostsAggresive;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.GhostsRandom;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.MsPacMan;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.MsPacManRandom;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.MsPacManRunAway;
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

        PacmanController pacMan = null; 
        
        switch (4) {
		case 1: 
			pacMan = new HumanController(new KeyBoardInput());
			break;
		case 2: 
			pacMan = new MsPacManRandom();
			break;
		case 3: 
			pacMan = new MsPacManRunAway();
			break;			
		case 4: 
			pacMan = new MsPacMan();
			break;			
		}
        
        GhostController ghosts = null;
        
        switch (3) {
		case 1: 
			ghosts = new GhostsRandom();
			break;
		case 2: 
			ghosts = new GhostsAggresive();
			break;			
		case 3: 
			ghosts = new Ghosts();
			break;			
		}
        
        System.out.println( 
            executor.runGame(pacMan, ghosts, 30) //last parameter defines speed
        );     
    }
	
}

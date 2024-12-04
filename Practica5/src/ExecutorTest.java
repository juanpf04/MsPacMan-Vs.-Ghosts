import es.ucm.fdi.ici.c2425.practica5.grupo02.Ghosts;
import es.ucm.fdi.ici.c2425.practica5.grupo02.MsPacMan;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(1000)
                .setVisual(true)
                .setScaleFactor(2.5)
                .build();

        PacmanController pacMan = new MsPacManRunAway();
        GhostController ghosts = new Ghosts();
        
        System.out.println( 
            executor.runGame(pacMan, ghosts, 2) //last parameter defines speed
        );     
    }
	
}

import es.ucm.fdi.ici.c2425.practica5.grupo02.MsPacMan;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(1000)
                .setTimeLimit(400)
                .setVisual(false)
                .setScaleFactor(2.5)
                .build();

        PacmanController pacMan = new MsPacMan();
        GhostController ghosts = new AggressiveGhosts();
        
        int i = executor.runGame(pacMan, ghosts, 0); //last parameter defines speed
        int j = 0;
        int k=0;
		while(k++<100) 
			j =executor.runGame(pacMan, ghosts, 0); //last parameter defines speed
    
		System.out.println(j + " - " + i + " = " + (j - i));
    }
	
}

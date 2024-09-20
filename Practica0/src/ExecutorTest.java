import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.Ghosts;
import es.ucm.fdi.ici.c2425.practica0.grupoIndividual.MsPacMan;
import pacman.Executor;

public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setVisual(true)
                .setScaleFactor(2.5)
                .build();

        MsPacMan pacMan = new MsPacMan();
        Ghosts ghosts = new Ghosts();
        
        System.out.println( 
            executor.runGame(pacMan, ghosts, 30) //last parameter defines speed
        );     
    }
	
}

package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.chase_actions;
import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MoreGhostsChaseAction implements Action{


    @Override
    public MOVE execute(Game game) {
        MsPacManInput input = new MsPacManInput(game);
        //El new ese lo he puesto para que no salga error, pero tengo que mirarlo
        //Ya que en chase simplemente comprobamos el camino con mas fantasmas comestibles sin
        //importar que sea seguro o no ya que damos por hecho que lo es hasta que detectamos un fantasma cerca
        //y pasamos a flee
        
        return input.pacmanRequieresAction() ? input.pathWithMoreEdibleGhosts() : MOVE.NEUTRAL;
    }

    
    @Override
    public String getActionId() {
        return "More ghosts action from Chase";
    }
    
}

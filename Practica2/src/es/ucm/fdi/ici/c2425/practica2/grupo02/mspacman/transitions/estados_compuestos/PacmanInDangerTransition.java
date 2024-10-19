package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_compuestos;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class PacmanInDangerTransition implements Transition {

    private final static int RANGE_PILLS = 40;
    private final static int RANGE_CHASE = 40;
    private String state;
    public PacmanInDangerTransition(String state){
        super();
        this.state = state;
    }
    @Override
    public boolean evaluate(Input in) {
        MsPacManInput input = (MsPacManInput) in;

        if(input.pacmanRequieresAction()){
            if (this.state.equals("from Pills")) {
                return input.getClosestNotEdibleGhost(RANGE_PILLS);
            } else if (this.state.equals("from Chase")) {
                return input.getClosestNotEdibleGhost(RANGE_CHASE);
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Pacman in danger " + this.state;
    }

}

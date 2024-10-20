package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_compuestos;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class PacmanInDangerTransition implements Transition {

    private String state;

    public PacmanInDangerTransition(String state){
        super();
        this.state = state;
    }
    @Override
    public boolean evaluate(Input in) {
        MsPacManInput input = (MsPacManInput) in;
        return input.getClosestNotEdibleGhost(40);
    }

    @Override
    public String toString() {
        return "Pacman in danger " + this.state;
    }

}

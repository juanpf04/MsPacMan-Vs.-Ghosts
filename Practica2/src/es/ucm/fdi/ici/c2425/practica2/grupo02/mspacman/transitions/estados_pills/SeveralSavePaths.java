package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_pills;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class SeveralSavePaths implements Transition {

    private String state;

    public SeveralSavePaths(String state){
        super();
        this.state = state;
    }

    @Override
    public boolean evaluate(Input in) {
        MsPacManInput input = (MsPacManInput) in;
        return input.pacmanRequieresAction() ? input.moreThanOneSavePath() : false;
    }

    @Override
    public String toString() {
        return "Several Save Paths from" + this.state;
    }
    
}

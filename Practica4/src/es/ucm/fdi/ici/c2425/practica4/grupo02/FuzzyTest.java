package es.ucm.fdi.ici.c2425.practica4.grupo02;

import java.io.File;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyTest {
	
	private static final String RULES_PATH = "src"+File.separator+"es"+File.separator+"ucm"+File.separator+"fdi"+File.separator+"ici"+File.separator+"c2425"+File.separator+"practica4"+File.separator+"grupo02"+File.separator+"ghosts"+File.separator;

	private static final String RULES_FILE = "ghosts.fcl";

	public static void main(String[] args) {
		
		 // Load from 'FCL' file
		String rulesFile = String.format("%s%s", RULES_PATH, RULES_FILE);
		FIS fis = FIS.load(rulesFile, true);

		// Error while loading?
		if (fis == null) { 
			System.err.println("Can't load file: '" + rulesFile + "'");
			System.exit(1);
		}

		// Get function block
		FunctionBlock fb = fis.getFunctionBlock("FuzzyGhosts");
		
		// Show
		JFuzzyChart.get().chart(fb);

		// Set inputs
        fis.setVariable("MSPACMANdistance", 20);
        fis.setVariable("MSPACMANconfidence", 0);
        fis.setVariable("edible", 1);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable runAway = fb.getVariable("Runaway");
        JFuzzyChart.get().chart(runAway, runAway.getDefuzzifier(), true);

        Variable goToPPill = fb.getVariable("GoToPPill");
        JFuzzyChart.get().chart(goToPPill, goToPPill.getDefuzzifier(), true);

	}

}

package es.ucm.fdi.ici.c2425.practica4.grupo02;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyTest {

	public static void main(String[] args) {

		// Load from 'FCL' file
		String rulesFile = String.format("%s%s", Ghosts.RULES_PATH, Ghosts.RULES_FILE);
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
		Variable runAway = fb.getVariable("RunAway");
		JFuzzyChart.get().chart(runAway, runAway.getDefuzzifier(), true);

		Variable goToPPill = fb.getVariable("GoToPPill");
		JFuzzyChart.get().chart(goToPPill, goToPPill.getDefuzzifier(), true);

	}

}

import es.ucm.fdi.ici.Scores;
import es.ucm.fdi.ici.PacManEvaluator;


public class Evaluate {

	public static void main(String[] args) {
		int i = 1;
		while (i != 0) {
			PacManEvaluator evaluator = new PacManEvaluator();
			Scores scores = evaluator.evaluate();
			scores.printScoreAndRanking();	
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i--;
		}

	}

}

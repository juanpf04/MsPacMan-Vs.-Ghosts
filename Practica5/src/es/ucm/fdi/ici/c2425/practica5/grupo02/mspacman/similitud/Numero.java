package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.similitud;

import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

public class Numero implements LocalSimilarityFunction {

	@Override
	public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {
		Number num1 = (Number) caseObject;
		Number num2 = (Number) queryObject;

		if (num1 == num2)
			return 1;
		else
			return 0;
	}

	@Override
	public boolean isApplicable(Object o1, Object o2) {
		if ((o1 == null) && (o2 == null))
			return true;
		else if (o1 == null)
			return o2 instanceof Number;
		else if (o2 == null)
			return o1 instanceof Number;
		else
			return (o1 instanceof Number) && (o2 instanceof Number);
	}

}

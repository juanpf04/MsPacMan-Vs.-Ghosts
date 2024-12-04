package es.ucm.fdi.ici.c2425.practica5.grupo02.similitud;

import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

public class Enumerado implements LocalSimilarityFunction{
	
	
	@Override
	public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {
		RelativePosition pos1 = (RelativePosition) caseObject;
		RelativePosition pos2 = (RelativePosition) queryObject;
	
		if(isApplicable(caseObject, queryObject)) {
			if(pos1 == pos2) {
				return 1;
			}
			else{
				if(pos1 == RelativePosition.DELANTE) {
					if(pos2 == RelativePosition.DETRAS) {
						return 0;
					}
					else if(pos2 == RelativePosition.AMBOS) {
						return 0.5;
					}
					else{
						return 0.5;
					}
				}
				else if(pos1 == RelativePosition.DETRAS) {
					if(pos2 == RelativePosition.DELANTE) {
						return 0;
					}
					else if(pos2 == RelativePosition.AMBOS) {
						return 0.5;
					}
					else{
						return 0.5;
					}
				}
				else if(pos1 == RelativePosition.AMBOS) {
					if(pos2 == RelativePosition.DELANTE) {
						return 0.5;
					}
					else if(pos2 == RelativePosition.DETRAS) {
						return 0.5;
					}
					else{
						return 0;
					}
				}
				else{
					if(pos2 == RelativePosition.DELANTE) {
						return 0.5;
					}
					else if(pos2 == RelativePosition.DETRAS) {
						return 0.5;
					}
					else{
						return 0;
					}
				}
			}
				
		}
		
		return 0;
	}

	@Override
	public boolean isApplicable(Object o1, Object o2) {
		
		if((o1==null)&&(o2==null))
			return true;
		else if(o1==null)
			return o2 instanceof Enum;
		else if(o2==null)
			return o1 instanceof Enum;
		else
			return (o1 instanceof Enum)&&(o2 instanceof Enum);
	}

}

package es.ucm.fdi.ici.c2425.practica3.grupo02;

import java.io.File;
import java.util.Iterator;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class Retetest {

	public static void main(String args[]) {
		String RULES_PATH = "es" + File.separator + "ucm" + File.separator + "fdi" + File.separator + "ici"
				+ File.separator + "c2425" + File.separator + "practica3" + File.separator + "grupo02" + File.separator;
		String RULES_FILE = "rulestest.clp";
		String altPath = "Practica3\\src\\es\\ucm\\fdi\\ici\\c2425\\practica3\\grupo02\\";
		String rulesFile = String.format("%s%s", altPath, RULES_FILE);
		Rete jess = new Rete();
		try {
			jess.batch(rulesFile);
			jess.reset();
			jess.run();
			Iterator<?> it = jess.listFacts();
			while (it.hasNext()) {
				Fact dd = (Fact) it.next();
				System.out.println(dd.toString());
			}
		} catch (JessException e) {
			e.printStackTrace();
		}
	}
}

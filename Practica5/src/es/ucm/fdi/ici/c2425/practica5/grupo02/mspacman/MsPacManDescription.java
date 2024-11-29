package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;

public class MsPacManDescription implements CaseComponent {

	Integer id;
	
	Integer score;
	Integer timeOfNearestEdibleGhost;
	Integer nearestPPill;
	Integer nearestPill;
	Integer nearestGhost;
	Boolean allGhostsEdible;
	@SuppressWarnings("rawtypes")
	Enum relativePos;
	Boolean allInJail;
	Boolean edibleGhost;
	


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getTime() {
		return timeOfNearestEdibleGhost;
	}

	public void setTime(Integer time) {
		this.timeOfNearestEdibleGhost = time;
	}

	public Integer getNearestPPill() {
		return nearestPPill;
	}

	public void setNearestPPill(Integer nearestPPill) {
		this.nearestPPill = nearestPPill;
	}

	public Integer getNearestPill() {
		return nearestPill;
	}

	public void setNearestPill(Integer nearestPill) {
		this.nearestPill = nearestPill;
	}

	public Integer getNearestGhost() {
		return nearestGhost;
	}

	public void setNearestGhost(Integer nearestGhost) {
		this.nearestGhost = nearestGhost;
	}

	public Boolean getAllGhostsEdible() {
		return allGhostsEdible;
	}

	public void setAllGhostsEdible(Boolean allGhostsEdible) {
		this.allGhostsEdible = allGhostsEdible;
	}

	public Boolean getAllInJail() {
		return allInJail;
	}

	public void setAllInJail(Boolean allInJail) {
		this.allInJail = allInJail;
	}

	public Boolean getEdibleGhost() {
		return edibleGhost;
	}

	public void setEdibleGhost(Boolean edibleGhost) {
		this.edibleGhost = edibleGhost;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [id=" + id + 
			", score=" + score + ", time=" + timeOfNearestEdibleGhost + ", nearestPPill="
				+ nearestPPill + ", nearestPill=" + nearestPill + ", allGhostsEdible=" + allGhostsEdible + 
				", nearestGhost=" + nearestGhost + ", allInJail=" + allInJail + ", edibleGhost=" + edibleGhost + "]";
	}


	
	

}

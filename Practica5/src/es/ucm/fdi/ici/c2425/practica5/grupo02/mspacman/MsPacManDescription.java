package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;

public class MsPacManDescription implements CaseComponent {

	private Integer id;
	
	private Integer score;
	private Integer timeEdibleGhost;
	private Integer nearestPPillDistance;
	private Integer nearestPillDistance;
	private Integer nearestGhostDistance;
	private Integer numberEdibleGhosts;
	private Integer numberJailGhosts;
	@SuppressWarnings("rawtypes")
	private Enum relativePosGhost;
	@SuppressWarnings("rawtypes")
	private Enum relativePosEdibleGhost;
	


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
		return timeEdibleGhost;
	}

	public void setTime(Integer time) {
		this.timeEdibleGhost = time;
	}

	public Integer getNearestPPillDistance() {
		return nearestPPillDistance;
	}

	public void setNearestPPillDistance(Integer nearestPPill) {
		this.nearestPPillDistance = nearestPPill;
	}

	public Integer getNearestPillDistance() {
		return nearestPillDistance;
	}

	public void setNearestPillDistance(Integer nearestPill) {
		this.nearestPillDistance = nearestPill;
	}

	public Integer getNearestGhostDistance() {
		return nearestGhostDistance;
	}

	public void setNearestGhostDistance(Integer nearestGhost) {
		this.nearestGhostDistance = nearestGhost;
	}

	public Integer getNumberEdibleGhosts() {
		return numberEdibleGhosts;
	}

	public void setNumberEdibleGhosts(Integer allGhostsEdible) {
		this.numberEdibleGhosts = allGhostsEdible;
	}

	public Integer getNumberJailGhosts() {
		return numberJailGhosts;
	}

	public void setNumberJailGhosts(Integer allInJail) {
		this.numberJailGhosts = allInJail;
	}

	public Enum getRelativePosGhost() {
		return this.relativePosGhost;
	}

	public void setRelativePosGhost(Enum relativePos) {
		this.relativePosGhost = relativePos;
	}

	public Enum getRelativePosEdibleGhost() {
		return this.relativePosEdibleGhost;
	}

	public void setRelativePosEdibleGhost(Enum relativePos) {
		this.relativePosEdibleGhost = relativePos;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [id=" + id + 
			", score=" + score + ", time=" + timeEdibleGhost + ", nearestPPill="
				+ nearestPPillDistance + ", nearestPill=" + nearestPillDistance 
				+ ", numberEdibleGhosts=" + numberEdibleGhosts +  ", nearestGhostDistance=" 
				+ nearestGhostDistance + ", numberJailGhosts=" + numberJailGhosts 
				+ ", numberEdibleGhosts=" + numberEdibleGhosts + "]";
	}


	
	

}

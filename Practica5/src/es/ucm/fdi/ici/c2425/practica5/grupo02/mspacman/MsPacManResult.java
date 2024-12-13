package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;

@SuppressWarnings("rawtypes")
public class MsPacManResult implements CaseComponent {

	private Integer id;
	private Integer score;
	private Integer timeEdibleGhost;
	private Integer nearestPPillDistance;
	private Integer nearestPillDistance;
	private Integer nearestGhostDistance;
	private Integer nearestEdibleGhostDistance;
	private Integer edibleGhosts;
	private Integer numberJailGhosts;
	private Integer relativePosGhost;
	private Integer relativePosEdibleGhost;

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
	
	public Integer getTimeEdibleGhost() {
		return timeEdibleGhost;
	}

	public void setTimeEdibleGhost(Integer time) {
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

	public Integer getNearestEdibleGhostDistance() {
		return nearestEdibleGhostDistance;
	}

	public void setNearestEdibleGhostDistance(Integer nearestEdibleGhost) {
		this.nearestGhostDistance = nearestEdibleGhost;
	}

	public Integer getEdibleGhosts() {
		return edibleGhosts;
	}

	public void setEdibleGhosts(Integer edibleGhost) {
		this.edibleGhosts = edibleGhost;
	}

	public Integer getNumberJailGhosts() {
		return numberJailGhosts;
	}

	public void setNumberJailGhosts(Integer numberJailGhosts) {
		this.numberJailGhosts = numberJailGhosts;
	}

	public Integer getRelativePosGhost() {
		return relativePosGhost;
	}

	public void setRelativePosGhost(Integer relativePosGhost) {
		this.relativePosGhost = relativePosGhost;
	}

	public Integer getRelativePosEdibleGhost() {
		return relativePosEdibleGhost;
	}

	public void setRelativePosEdibleGhost(Integer relativeEdibleGhost) {
		this.relativePosEdibleGhost = relativeEdibleGhost;
	}
	
	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [id=" + id + ", score=" + score + ", time=" + timeEdibleGhost + ", nearestPPill="
				+ nearestPPillDistance + "nearestPill=" + this.nearestPillDistance + "nearestGhost="
				+ nearestGhostDistance + ", " + ", nearestEdibleGhost=" + nearestEdibleGhostDistance + " edibleGhost="
				+ edibleGhosts + ", ghostsJail=" + numberJailGhosts + " relativePosGhost=" + relativePosGhost
				+ ", relativePosEdibleGhost=" + relativePosEdibleGhost + "]";
	}

}

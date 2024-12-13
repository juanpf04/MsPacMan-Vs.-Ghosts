package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;

@SuppressWarnings("rawtypes")
public class MsPacManDescription implements CaseComponent {

	private Integer id;

	private Integer score; // obtained score
	private Integer edibleTime; // time of edible ghost
	private Integer ppillDistance; // nearest power pill distance
	private Integer pillDistance; // nearest pill distance
	private Integer ghostDistance; // nearest ghost distance
	private Integer edibleGhostDistance; // nearest edible ghost distance
	private Integer edibleGhosts; // number of edible ghosts
	private Integer jailGhosts; // number of ghosts in jail
	private Enum relativePosGhost; // relative position of nearest ghost
	private Enum relativePosEdibleGhost; // relative position of nearest edible ghost

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

	public Integer getEdibleTime() {
		return edibleTime;
	}

	public void setEdibleTime(Integer edibleTime) {
		this.edibleTime = edibleTime;
	}

	public Integer getPpillDistance() {
		return ppillDistance;
	}

	public void setPpillDistance(Integer ppillDistance) {
		this.ppillDistance = ppillDistance;
	}

	public Integer getPillDistance() {
		return pillDistance;
	}

	public void setPillDistance(Integer pillDistance) {
		this.pillDistance = pillDistance;
	}

	public Integer getGhostDistance() {
		return ghostDistance;
	}

	public void setGhostDistance(Integer ghostDistance) {
		this.ghostDistance = ghostDistance;
	}

	public Integer getEdibleGhostDistance() {
		return edibleGhostDistance;
	}

	public void setEdibleGhostDistance(Integer edibleGhostDistance) {
		this.edibleGhostDistance = edibleGhostDistance;
	}

	public Integer getEdibleGhosts() {
		return edibleGhosts;
	}

	public void setEdibleGhosts(Integer edibleGhosts) {
		this.edibleGhosts = edibleGhosts;
	}

	public Integer getJailGhosts() {
		return jailGhosts;
	}

	public void setJailGhosts(Integer jailGhosts) {
		this.jailGhosts = jailGhosts;
	}

	public Enum getRelativePosGhost() {
		return relativePosGhost;
	}

	public void setRelativePosGhost(Enum relativePosGhost) {
		this.relativePosGhost = relativePosGhost;
	}

	public Enum getRelativePosEdibleGhost() {
		return relativePosEdibleGhost;
	}

	public void setRelativePosEdibleGhost(Enum relativePosEdibleGhost) {
		this.relativePosEdibleGhost = relativePosEdibleGhost;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [id=" + id + ", score=" + score + ", edibleTime=" + edibleTime + ", nearestPPill="
				+ ppillDistance + "nearestPill=" + this.pillDistance + "nearestGhost=" + ghostDistance + ", "
				+ ", nearestEdibleGhost=" + edibleGhostDistance + " numbreOfEdibleGhosts=" + edibleGhosts
				+ ", ghostsInJail=" + jailGhosts + " relativePosGhost=" + relativePosGhost + ", relativePosEdibleGhost="
				+ relativePosEdibleGhost + "]";
	}

}

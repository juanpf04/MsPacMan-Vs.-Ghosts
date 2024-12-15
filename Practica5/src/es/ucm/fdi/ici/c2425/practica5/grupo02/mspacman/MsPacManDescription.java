package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import es.ucm.fdi.ici.c2425.practica5.grupo02.POS;
import pacman.game.Constants.MOVE;

public class MsPacManDescription implements CaseComponent {

	private Integer id;

	private Integer score; // current score
	private Integer level; // current level

	private Integer index; // current index
	private MOVE move; // last move made

	private Integer edibleTime; // time of edible ghost

	private Integer edibleGhosts; // number of edible ghosts
	private Integer jailGhosts; // number of ghosts in jail

	private Integer pillDistance; // nearest pill distance
	private Integer ppillDistance; // nearest power pill distance

	private Integer ghostDistance; // nearest ghost distance
	private Integer edibleGhostDistance; // nearest edible ghost distance

	private POS relativePosGhost; // relative position of nearest ghosts
	private POS relativePosEdibleGhost; // relative position of nearest edible ghosts

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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public MOVE getMove() {
		return move;
	}

	public void setMove(MOVE move) {
		this.move = move;
	}

	public Integer getEdibleTime() {
		return edibleTime;
	}

	public void setEdibleTime(Integer edibleTime) {
		this.edibleTime = edibleTime;
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

	public Integer getPillDistance() {
		return pillDistance;
	}

	public void setPillDistance(Integer pillDistance) {
		this.pillDistance = pillDistance;
	}

	public Integer getPpillDistance() {
		return ppillDistance;
	}

	public void setPpillDistance(Integer ppillDistance) {
		this.ppillDistance = ppillDistance;
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

	public POS getRelativePosGhost() {
		return relativePosGhost;
	}

	public void setRelativePosGhost(POS relativePosGhost) {
		this.relativePosGhost = relativePosGhost;
	}

	public POS getRelativePosEdibleGhost() {
		return relativePosEdibleGhost;
	}

	public void setRelativePosEdibleGhost(POS relativePosEdibleGhost) {
		this.relativePosEdibleGhost = relativePosEdibleGhost;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
	    return "MsPacManDescription [" +
	           "id=" + id + ", " +
	           "score=" + score + ", " +
	           "level=" + level + ", " +
	           "index=" + index + ", " +
	           "move=" + move + ", " +
	           "edibleTime=" + edibleTime + ", " +
	           "edibleGhosts=" + edibleGhosts + ", " +
	           "jailGhosts=" + jailGhosts + ", " +
	           "pillDistance=" + pillDistance + ", " +
	           "ppillDistance=" + ppillDistance + ", " +
	           "ghostDistance=" + ghostDistance + ", " +
	           "edibleGhostDistance=" + edibleGhostDistance + ", " +
	           "relativePosGhost=" + relativePosGhost + ", " +
	           "relativePosEdibleGhost=" + relativePosEdibleGhost +
	           "]";
	}
	
}

;FACTS ASSERTED BY GAME INPUT
(deftemplate GHOST
    (slot name (type SYMBOL))
	(slot edible (type SYMBOL))
	(slot behindPacman (type SYMBOL))
	(slot distanceMSPACMANNearestPPill (type NUMBER))
	(slot distanceMSPACMAN (type NUMBER))
	(slot distanceToClosestEdibleGhost (type NUMBER))
	(slot distanceToClosestNotEdibleGhost (type NUMBER))
	(slot ghostDensity (type NUMBER))
	(slot pillCount (type NUMBER)))

	
(deftemplate MSPACMAN 
    (slot mindistancePPill (type NUMBER)))
    
;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id) (slot info (default "")) (slot priority (type NUMBER) ) ; mandatory slots
	(slot runawaystrategy (type SYMBOL)) ; Extra slot for the runaway action
	(slot edible (type SYMBOL)) ; Extra slot for the go to Ghost action
) 


(deffacts ghost
    (GHOST (name BLINKY) 
    (edible false) 
    (behindPacman false) 
    (distanceMSPACMANNearestPPill 10) 
    (distanceMSPACMAN 50) 
    (distanceToClosestEdibleGhost 30) 
    (distanceToClosestNotEdibleGhost 40) 
    (ghostDensity 1.5) 
    (pillCount 20)))

;RULES --------------------------------------------------------------------------------------------

(defrule goToNearestPPillToPacman
	(GHOST (name ?g) (edible false) (distanceMSPACMANNearestPPill ?d1)) 
	(MSPACMAN (mindistancePPill ?d2)) 
	(test (< ?d1 ?d2)) ; ghost closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id goToNearestPPillToPacman) 
			(info (str-cat "MSPacMan near PPill, " ?g " nearest --> go to ppill")) 
			(priority 50) 
		)
	)
)

(defrule protectEdibleGhost
	(GHOST (edible false) (distanceMSPACMAN ?d1) (distanceToClosestEdibleGhost ?d2)) 
	(test (< ?d2 ?d1)) ; Ghost closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id protectEdibleGhost) 
			(info "Edible ghost near --> go to edible ghost") 
			(edible false)
			(priority 50) 
		)
	)
)

(defrule goToSafeGhost
	(GHOST (edible true) (distanceMSPACMAN ?d1) (distanceToClosestNotEdibleGhost ?d2)) 
	(test (< ?d2 ?d1)) ; Ghost closer to safe ghost than mspacman
	=>  
	(assert 
		(ACTION 
			(id goToSafeGhost) 
			(info "Not edible ghost near --> go to ghost")
			(edible true) 
			(priority 50) 
		)
	)
)

(defrule disperse
	(GHOST (ghostDensity ?d1)) 
	(test (< 1.5 ?d1)) ; density higher than threshold
	=>  
	(assert 
		(ACTION 
			(id disperse) 
			(info "Density too high --> move away from other ghosts") 
			(priority 50) 
		)
	)
)

(defrule goToLastPills
	(GHOST (edible false) (pillCount ?d1)) 
	(test (< ?d1 15)) ; density higher than threshold
	=>  
	(assert 
		(ACTION 
			(id goToLastPills) 
			(info "Few pills left --> go to last pills") 
			(priority 50) 
		)
	)
)

(defrule blockExits
	(GHOST (edible false) (behindPacman true)) 
	=>  
	(assert 
		(ACTION 
			(id blockExits) 
			(info "behind pacman --> cover exits") 
			(priority 50) 
		)
	)
)

(defrule runAway
	(GHOST (edible true)) 
	=>  
	(assert 
		(ACTION (id runAway) (info "Comestible --> huir") (priority 30) 
			(runawaystrategy CORNER)
		)
	)
)

(defrule runAway2
	(MSPACMAN (mindistancePPill ?d)) 
	(test (<= ?d 30)) 
	=>  
	(assert 
		(ACTION (id runAway) (info "MSPacMan cerca PPill") (priority 50) 
			(runawaystrategy RANDOM)
		)
	)
)

(defrule notDisperse
	(GHOST (edible true) (ghostDensity ?d1)) 
	(test (< ?d1 1.5)) ; density higher than threshold
	=>  
	(assert 
		(ACTION 
			(id runAway) 
			(info "Density low --> run away normally") 
			(priority 50) 
		)
	)
)
	
(defrule chase
	(GHOST (edible false))
	(MSPACMAN (mindistancePPill ?d1))
	(test (< 30 ?d1))
	=> 
	(assert (ACTION (id chase) (info "No comestible --> perseguir")  (priority 10)))
)

(defrule chase2
	(GHOST (edible false) (distanceMSPACMAN ?d1) (distanceToClosestEdibleGhost ?d2)) 
	(test (< ?d1 ?d2)) ; Ghost closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id chase) 
			(info "Edible ghost near --> go to edible ghost") 
			(priority 50) 
		)
	)
)

(defrule chase3
	(GHOST (edible false) (distanceMSPACMAN ?d1) (behindPacman false)) 
	(test (< ?d1 50)) ; Ghost closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id chase) 
			(info "Edible ghost near --> go to edible ghost") 
			(priority 50) 
		)
	)
)

(defrule chase4
	(GHOST (edible false) (distanceMSPACMANNearestPPill ?d1)) 
	(test (< ?d1 30)) ; Ghost closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id chase) 
			(info "Edible ghost near --> go to edible ghost") 
			(priority 50) 
		)
	)
)
(defrule notDisperse2
	(GHOST (edible false) (ghostDensity ?d1)) 
	(test (< ?d1 1.5)) ; density higher than threshold
	=>  
	(assert 
		(ACTION 
			(id chase) 
			(info "Density low --> chase normally") 
			(priority 50) 
		)
	)
)
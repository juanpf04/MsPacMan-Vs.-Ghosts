;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot edible (type SYMBOL))
	(slot distanceMSPACMANNearestPPill (type NUMBER))
	(slot distanceMSPACMAN (type NUMBER))
	(slot lairTime (type NUMBER))
	(slot edibleTime (type NUMBER))
	(slot lastMoveMade (type SYMBOL))
	(slot distanceToClosestEdibleGhost (type NUMBER))
	(slot distanceToClosestNotEdibleGhost (type NUMBER))
	(slot ghostDensity (type NUMBER))
	(slot pillCount (type NUMBER))
	(slot behindPacman (type SYMBOL)))

	
(deftemplate MSPACMAN 
    (slot mindistancePPill (type NUMBER)))
    
;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id) (slot info (default "")) (slot priority (type NUMBER) ) ; mandatory slots
	(slot runawaystrategy (type SYMBOL)) ; Extra slot for the runaway action
) 

;RULES --------------------------------------------------------------------------------------------

(defrule BLINKYgoesToNearestPPillToPacman
	(BLINKY (edible false) (distanceMSPACMANNearestPPill ?d1)) 
	(MSPACMAN (mindistancePPill ?d2)) 
	(test (< ?d1 ?d2)) ; blinky closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id BLINKYgoesToNearestPPillToPacman) 
			(info "MSPacMan cerca PPill, Blinky mas cerca --> ir a ppill") 
			(priority 50) 
		)
	)
)

(defrule BLINKYprotectsEdibleGhost
	(BLINKY (edible false) (distanceMSPACMAN ?d1) (distanceToClosestEdibleGhost ?d2)) 
	(test (< ?d2 ?d1)) ; blinky closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id BLINKYprotectsEdibleGhost) 
			(info "Edible ghost near --> go to edible ghost") 
			(priority 50) 
		)
	)
)

(defrule BLINKYgoesToSafeGhost
	(BLINKY (edible true) (distanceMSPACMAN ?d1) (distanceToClosestNotEdibleGhost ?d2)) 
	(test (< ?d2 ?d1)) ; blinky closer to safe ghost than mspacman
	=>  
	(assert 
		(ACTION 
			(id BLINKYgoesToSafeGhost) 
			(info "Not edible ghost near --> go to ghost") 
			(priority 50) 
		)
	)
)

(defrule BLINKYdisperses
	(BLINKY (ghostDensity ?d1)) 
	(test (< 1.5 ?d1)) ; density higher than threshold
	=>  
	(assert 
		(ACTION 
			(id BLINKYdisperses) 
			(info "Density too high --> move away from other ghosts") 
			(priority 50) 
		)
	)
)

(defrule BLINKYgoesToLastPills
	(BLINKY (edible false) (pillCount ?d1)) 
	(test (< ?d1 15)) ; density higher than threshold
	=>  
	(assert 
		(ACTION 
			(id BLINKYgoesToLastPills) 
			(info "Few pills left --> go to last pills") 
			(priority 50) 
		)
	)
)

(defrule BLINKYblocksExits
	(BLINKY (edible false) (behindPacman true)) 
	=>  
	(assert 
		(ACTION 
			(id BLINKYblocksExits) 
			(info "behind pacman --> cover exits") 
			(priority 50) 
		)
	)
)

(defrule BLINKYrunsAway
	(BLINKY (edible true)) 
	=>  
	(assert 
		(ACTION (id BLINKYrunsAway) (info "Comestible --> huir") (priority 30) 
			(runawaystrategy CORNER)
		)
	)
)

(defrule BLINKYrunsAway2
	(MSPACMAN (mindistancePPill ?d)) (test (<= ?d 30)) 
	=>  
	(assert 
		(ACTION (id BLINKYrunsAway) (info "MSPacMan cerca PPill") (priority 50) 
			(runawaystrategy RANDOM)
		)
	)
)

(defrule BLINKYnotDisperses
	(BLINKY (edible true) (ghostDensity ?d1)) 
	(test (< ?d1 1.5)) ; density higher than threshold
	=>  
	(assert 
		(ACTION 
			(id BLINKYrunsAway) 
			(info "Density low --> run away normally") 
			(priority 50) 
		)
	)
)
	
(defrule BLINKYchases
	(BLINKY (edible false))
	(MSPACMAN (mindistancePPill ?d1))
	(test (< 30 ?d1))
	=> 
	(assert (ACTION (id BLINKYchases) (info "No comestible --> perseguir")  (priority 10)))
)

(defrule BLINKYchase2
	(BLINKY (edible false) (distanceMSPACMAN ?d1) (distanceToClosestEdibleGhost ?d2)) 
	(test (< ?d1 ?d2)) ; blinky closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id BLINKYchases) 
			(info "Edible ghost near --> go to edible ghost") 
			(priority 50) 
		)
	)
)

(defrule BLINKYchase3
	(BLINKY (edible false) (distanceMSPACMAN ?d1) (behindPacman false)) 
	(test (< ?d1 50)) ; blinky closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id BLINKYchases) 
			(info "Edible ghost near --> go to edible ghost") 
			(priority 50) 
		)
	)
)

(defrule BLINKYchase4
	(BLINKY (edible false) (distanceMSPACMANNearestPPill ?d1)) 
	(test (< ?d1 30)) ; blinky closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id BLINKYchases) 
			(info "Edible ghost near --> go to edible ghost") 
			(priority 50) 
		)
	)
)
(defrule BLINKYnotDisperse2
	(BLINKY (edible false) (ghostDensity ?d1)) 
	(test (< ?d1 1.5)) ; density higher than threshold
	=>  
	(assert 
		(ACTION 
			(id BLINKYchases) 
			(info "Density low --> chase normally") 
			(priority 50) 
		)
	)
)
;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot edible (type SYMBOL))
	(slot distanceMSPACMANNearestPPill (type NUMBER))
	(slot distanceMSPACMAN (type NUMBER))
	(slot lairTime (type NUMBER))
	(slot edibleTime (type NUMBER))
	(slot lastMoveMade (type SYMBOL)))
	
(deftemplate MSPACMAN 
    (slot mindistancePPill (type NUMBER)))
    
;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id) (slot info (default "")) (slot priority (type NUMBER) ) ; mandatory slots
	(slot runawaystrategy (type SYMBOL)) ; Extra slot for the runaway action
) 

;RULES 
(defrule BLINKYrunsAwayMSPACMANclosePPill
	(MSPACMAN (mindistancePPill ?d)) (test (<= ?d 30)) 
	=>  
	(assert 
		(ACTION (id BLINKYrunsAway) (info "MSPacMan cerca PPill") (priority 50) 
			(runawaystrategy RANDOM)
		)
	)
)

(defrule BLINKYgoesToPPillCloserThanMSPACMAN
	(BLINKY (edible false) (distanceMSPACMANNearestPPill ?d1)) 
	(MSPACMAN (mindistancePPill ?d2)) 
	(test (< ?d1 ?d2)) ; blinky closer to mspacman closest ppill than mspacman
	=>  
	(assert 
		(ACTION 
			(id BLINKYgoesToNearestPPillToPacman) 
			(info "MSPacMan cerca PPill, Blinky mas cerca") 
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
	
(defrule BLINKYchases
	(BLINKY (edible false)) 
	=> 
	(assert (ACTION (id BLINKYchases) (info "No comestible --> perseguir")  (priority 10) ))
)
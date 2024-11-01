;FACTS ASSERTED BY GAME INPUT
(deftemplate GHOST
	(slot edible (type SYMBOL)))

(deftemplate MSPACMAN 
    (slot mindistancePPill (type NUMBER)) )
    
;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id) (slot info (default "")) (slot priority (type NUMBER) ) ; mandatory slots
	(slot runawaystrategy (type SYMBOL)) ; Extra slot for the runaway action
) 

;RULES 
(defrule runAwayMSPACMANclosePPill
	(MSPACMAN (mindistancePPill ?d)) (test (<= ?d 30)) 
	=>  
	(assert 
		(ACTION (id runAway) (info "MSPacMan cerca PPill") (priority 50) 
			(runawaystrategy RANDOM)
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
	
(defrule chase
	(GHOST (edible false)) 
	=> 
	(assert (ACTION (id chase) (info "No comestible --> perseguir")  (priority 10) ))
)	
	
	
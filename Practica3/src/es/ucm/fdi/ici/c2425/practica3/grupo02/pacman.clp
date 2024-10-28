(deftemplate PACMAN
	(slot nearBLINKY (type FLOAT))
	(slot nearINKY (type FLOAT))
    (slot nearPINKY (type FLOAT))
    (slot nearSUE (type FLOAT))    
)

(deftemplate BLINKY
	(slot edible (type SYMBOL)))
	
(deftemplate INKY
	(slot edible (type SYMBOL)))
	
(deftemplate PINKY
	(slot edible (type SYMBOL)))

(deftemplate SUE
	(slot edible (type SYMBOL)))


;DEFINITION OF THE ACTION FACT-------------------------------------
(deftemplate ACTION
	(slot id) (slot info (default "")) (slot priority (type NUMBER) ) ; mandatory slots
	(slot runawaystrategypacman (type SYMBOL)) ; Extra slot for the runaway action
	(slot chasestrategypacman (type SYMBOL)) ; Extra slot for the chase action
) 

 
;RULES----------------------------------------------- 

;REGLAS DE PACMAN PARA BLINKY-------------------------------------------
(defrule PACMANrunsAwayMSPACMANcloseBLINKY
	(MSPACMAN (nearBLINKY ?d)) (test (<= ?d 30)) 
	=>  
	(assert 
		(ACTION (id pacmanRunAwayBlinky) (info "MSPacMan cerca de BLINKY") (priority 50) 
			(runawaystrategypacman RANDOM)
		)
	)
)

(defrule PACMANrunsAway
	(BLINKY (edible false)) 
	=>  
	(assert 
		(ACTION (id INKYrunsAway) (info "No comestible --> huir") (priority 30) 
			(runawaystrategypacman CORNER)
		)
	)
)
	
(defrule PACMANchasesBLINKY
	(BLINKY (edible true)) 
	=> 
	(assert 
		(ACTION (id INKYchases) (info "Comestible --> perseguir")  (priority 10) 
			(chasestrategypacman RANDOM)
		)
	)
)	

	

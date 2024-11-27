;DEFINITION OF THE PACMAN FACT-------------------------------------
(deftemplate BLINKY
    (slot name (type SYMBOL) (default BLINKY))
    (slot distance (type NUMBER))
    (slot edible (type SYMBOL))
    (slot ghostEdibleTime (type NUMBER))
    (slot ghostLairTime (type NUMBER))
)

(deftemplate INKY
    (slot name (type SYMBOL) (default INKY))
    (slot distance (type NUMBER))
    (slot edible (type SYMBOL))
    (slot ghostEdibleTime (type NUMBER))
    (slot ghostLairTime (type NUMBER))
)

(deftemplate PINKY
    (slot name (type SYMBOL) (default PINKY))
    (slot distance (type NUMBER))
    (slot edible (type SYMBOL))
    (slot ghostEdibleTime (type NUMBER))
    (slot ghostLairTime (type NUMBER))
)

(deftemplate SUE
    (slot name (type SYMBOL) (default SUE))
    (slot distance (type NUMBER))
    (slot edible (type SYMBOL))
    (slot ghostEdibleTime (type NUMBER))
    (slot ghostLairTime (type NUMBER))
)

(deftemplate PACMAN
    (slot position (type NUMBER))
    (slot last-move (type SYMBOL))
    (slot neightbour-nodes (type NUMBER))
)

(deftemplate NEAREST-GHOST
    (slot name (type SYMBOL))
    (slot distance (type NUMBER))
    (slot edible (type SYMBOL))
    (slot ghostEdibleTime (type NUMBER))
)

(deftemplate NEAREST-EDIBLE-GHOST
    (slot name (type SYMBOL))
    (slot distance (type NUMBER))
    (slot edible (type SYMBOL))
    (slot ghostEdibleTime (type NUMBER))
)

(deftemplate PILL
    (slot distance (type NUMBER))
    (slot index (type NUMBER))
)

(deftemplate POWERPILL
    (slot distance (type NUMBER))
    (slot index (type NUMBER))
)

(deftemplate NEAREST-PILL
    (slot distance (type NUMBER))
    (slot index (type NUMBER))
)

(deftemplate NEAREST-POWERPILL
    (slot distance (type NUMBER))
    (slot index (type NUMBER))
)




;FUNCIONES -------------------------------------------------------


;DEFINITION OF THE ACTION FACT-------------------------------------



(deftemplate ACTION
    (slot id) (slot info (default "")) (slot priority (type NUMBER)) ; mandatory slots
    (slot runawaystrategypacman (type SYMBOL)) ; Extra slot for the runaway action
    (slot chasestrategypacman (type SYMBOL)) ; Extra slot for the chase action
    (slot chasepills (type SYMBOL)); Extra slot for the chase pills action
    (slot chasepowerpills (type SYMBOL)); Extra slot for the chase powerpills action
    (slot moverandom (type SYMBOL)); Extra slot for the move random action
    (slot avoidcorner (type SYMBOL)); Extra slot for avoid corners
    (slot movetocenter (type SYMBOL)); Extra slot for move to center
) 


;ACTIONES GENERALES

;RELACIONADO CON PILLLS (4 REGLAS)----------------------------------------

;1
(defrule find-nearest-pill ;Buscamos la pill mas cercana
    ?p1 <- (PILL (distance ?d1) (index ?index))
    (not (PILL (distance ?d2&:(< ?d2 ?d1))))
    =>
    (assert (NEAREST-PILL (distance ?d1) (index ?index))) ;Añadimos un hecho con la distancia de la pill mas cercana
)
 
;2
(defrule find-nearest-powerpill ;Buscamos la powerpill mas cercana
    ?p1 <- (POWERPILL (distance ?d1) (index ?index))
    (not (POWERPILL (distance ?d2&:(< ?d2 ?d1))))
    =>
    (assert (NEAREST-POWERPILL (distance ?d1) (index ?index))) ;Añadimos un hecho con la distancia de la power pill mas cercana
)

;3 COMER PILL SI NO HAY NINGUN FANTASMA CERCA - PRIORIDAD 10
(defrule eat-pill-if-no-ghost ;Comer pill si no hay ningun fantasma cerca
    (not (NEAREST-GHOST)) ;Indicar que no hay ningun hecho asetado de fantasma
    (not (NEAREST-EDIBLE-GHOST)) ;Indicar que no hay ningun hecho asetado de fantasma comestible
    ?p <- (NEAREST-PILL (distance ?pd) (index ?index))
    =>
    (assert 
		(ACTION (id pacmanChasePills) (info (str-cat "Pacman esta seguro o lejos de fantasmas comestibles ---> Comer pills " ?pd)) (priority 60)
			(chasepills ?index)
		)
	)
)

;4 COMER POWERPILL SI HAY FANTASMA NO COMESTIBLE CERCA - PRIORIDAD 80
(defrule chase-powerpill-if-ghost-near ;Si tenemos un fantasma no comestible cerca, comemos la powerpill
    (NEAREST-POWERPILL (distance ?ppd) (index ?index))
    (NEAREST-GHOST (distance ?d))
    (test (and (< ?ppd ?d) (neq ?ppd -1) (neq ?index -1)))
    =>
    (assert 
        (ACTION (id pacmanChasePowerPill) (info "Fantasma no comestible cerca pero powerpill mas cerca ---> Comer PowerPill") (priority 110)
            (chasepowerpills ?index)
        )	
    )
)



;RELACIONADO CON FANTASMAS (8 REGLAS)----------------------------------------

;5
(defrule find-nearest-ghost ;Buscamos el fantasma no comestible mas cercano
    ?b <- (BLINKY (distance ?bd) (edible FALSE) (ghostEdibleTime ?bget))
    ?i <- (INKY (distance ?id) (edible FALSE) (ghostEdibleTime ?iget))
    ?p <- (PINKY (distance ?pd) (edible FALSE) (ghostEdibleTime ?pget))
    ?s <- (SUE (distance ?sd) (edible FALSE) (ghostEdibleTime ?sget))
    (test (or (<= ?bd 75) (<= ?id 75) (<= ?pd 75) (<= ?sd 75)))
    =>
    (bind ?nearest-name "NONE")
    (bind ?nearest-distance 10000)
    (bind ?nearest-time -1)
    (if (and (<= ?bd 75) (<= ?bd ?nearest-distance)) then
        (bind ?nearest-name "BLINKY")
        (bind ?nearest-distance ?bd)
        (bind ?nearest-time ?bget))
    (if (and (<= ?id 75) (<= ?id ?nearest-distance)) then
        (bind ?nearest-name "INKY")
        (bind ?nearest-distance ?id)
        (bind ?nearest-time ?iget))
    (if (and (<= ?pd 75) (<= ?pd ?nearest-distance)) then
        (bind ?nearest-name "PINKY")
        (bind ?nearest-distance ?pd)
        (bind ?nearest-time ?pget))
    (if (and (<= ?sd 75) (<= ?sd ?nearest-distance)) then
        (bind ?nearest-name "SUE")
        (bind ?nearest-distance ?sd)
        (bind ?nearest-time ?sget))
    (if (neq ?nearest-name "NONE") then
        (assert (NEAREST-GHOST (name ?nearest-name) (distance ?nearest-distance) (edible FALSE) (ghostEdibleTime ?nearest-time))))
)

;6 HUIR DEL FANTASMA MAS CERCANO - PRIORIDAD 90
(defrule runaway-from-nearest-ghost ;Una vez sabemos cual es el fantasma mas cercano, huir de el
    (NEAREST-GHOST (name ?name) (distance ?d) (edible FALSE))
    =>
    (assert (ACTION (id pacmanRunAway) (info (str-cat "Run away from " ?name)) (priority 100) 
        (runawaystrategypacman ?name)))
)

;7 
(defrule find-nearest-edible-ghost ;Buscamos el fantasma comestible mas cercano. Comparamos las distancias de todos los fantasmas con los demas y lo guardamos en una variable
    ?b <- (BLINKY (distance ?bd) (edible ?be) (ghostEdibleTime ?bget))
    ?i <- (INKY (distance ?id) (edible ?ie) (ghostEdibleTime ?iget))
    ?p <- (PINKY (distance ?pd) (edible ?pe) (ghostEdibleTime ?pget))
    ?s <- (SUE (distance ?sd) (edible ?se) (ghostEdibleTime ?sget))

    (test (or (and (<= ?bd 75) (eq ?be TRUE)) (and (<= ?id 75) (eq ?ie TRUE)) (and (<= ?pd 75) (eq ?pe TRUE)) (and (<= ?sd 75) (eq ?se TRUE))))
    =>
    (bind ?nearest-name "NONE")
    (bind ?nearest-distance 10000)
    (bind ?nearest-time -1)
    (if (and (<= ?bd 75) (<= ?bd ?nearest-distance) (eq ?be TRUE)) then
        (bind ?nearest-name "BLINKY")
        (bind ?nearest-distance ?bd)
        (bind ?nearest-time ?bget))
    (if (and (<= ?id 75) (<= ?id ?nearest-distance) (eq ?ie TRUE)) then
        (bind ?nearest-name "INKY")
        (bind ?nearest-distance ?id)
        (bind ?nearest-time ?iget))
    (if (and (<= ?pd 75) (<= ?pd ?nearest-distance) (eq ?pe TRUE)) then
        (bind ?nearest-name "PINKY")
        (bind ?nearest-distance ?pd)
        (bind ?nearest-time ?pget))
    (if (and (<= ?sd 75) (<= ?sd ?nearest-distance) (eq ?se TRUE)) then
        (bind ?nearest-name "SUE")
        (bind ?nearest-distance ?sd)
        (bind ?nearest-time ?sget))
    (if (neq ?nearest-name "NONE") then
        (assert (NEAREST-EDIBLE-GHOST (name ?nearest-name) (distance ?nearest-distance) (edible TRUE) (ghostEdibleTime ?nearest-time))))
)   

;8 PERSEGUIR FANTASMA MAS CERCANO - PRIORIDAD 40
(defrule chase-nearest-edible-ghost ;Perseguir al fantasma mas cercano
    (NEAREST-EDIBLE-GHOST (name ?name) (distance ?d) (edible TRUE))
    =>
    (assert (ACTION (id pacmanChase) (info (str-cat "Chase " ?name)) (priority 90) 
        (chasestrategypacman ?name)))
)

;9 SI HAY FANTASMAS DE LOS DOS TIPOS, HUIR DEL NO COMESTIBLE QUE ESTA MAS CERCA QUE EL COMESTIBLE - PRIORIDAD 70
(defrule runaway-from-ghost-although-edible-ghosts 
    (NEAREST-EDIBLE-GHOST (name ?name) (distance ?d) (edible TRUE))
    (NEAREST-GHOST (name ?n) (distance ?d2) (edible FALSE))
    (test (< ?d2 ?d))
    =>
    (assert (ACTION (id pacmanRunAway) (info "Fantasma no comestible mas cerca que ocmestible ---> HUIR ") (priority 70) 
        (runawaystrategypacman ?n)))
)

;10 SI HAY FANTASMAS DE LOS DOS TIPOS, PERSEGUIR AL COMESTIBLE QUE ESTA MAS CERCA QUE EL NO COMESTIBLE - PRIORIDAD 60
(defrule chase-edible-ghost-although-non-edible-ghosts 
    (NEAREST-EDIBLE-GHOST (name ?name) (distance ?d))
    (NEAREST-GHOST (name ?n) (distance ?d2))
    (test (and (> ?d2 75) (< ?d ?d2)))
    =>
    (assert (ACTION (id pacmanChase) (info "Fantamas comestible más cerca que fantasma no comestible ---> PERSEGUIR") (priority 60) 
        (chasestrategypacman ?name)))

)

;11 SI NO HAY POWER PILLS CERCA, HUIR DE FANTASMAS- PRIORIDAD 70
(defrule run-away-when-no-power-pills ;Si no hay power pills, huir de los fantasmas no comestibles
    (NEAREST-GHOST (name ?n) (distance ?d))
    (not (POWERPILL (index ?i) (distance ?pd)))
    =>
    (assert (ACTION (id pacmanRunAway) (info "Fantasma no comestible cerca y no hay píldoras de poder ---> HUIR") (priority 70) 
        (runawaystrategypacman ?n)))
)

;12 FANTASMAS COMESTIBLES CERCA CON POCO TIMEPO E - PRIORIDAD 80
(defrule edible-ghost-near-without-edible-time ;Si hay un fantasma comestible cerca pero con poco tiempo, huir de el
    (NEAREST-EDIBLE-GHOST (distance ?d) (name ?name) (edible ?e) (ghostEdibleTime ?get))
    (test (and  (eq ?e TRUE) (< ?get 30)))
    =>
    (assert 
        (ACTION (id pacmanRunAway) (info "Fantasma comestible pero con poco timepo ---> HUIR") (priority 90)
            (runawaystrategypacman ?name)
        )	
    )
)

;13 PERSEGUIR MUTIPLES FANTASMAS COMESTIBLES - PRIORIDAD 70
(defrule chase-multiple-edible-ghosts
    (NEAREST-EDIBLE-GHOST (name ?name1) (distance ?d1))
    (NEAREST-EDIBLE-GHOST (name ?name2) (distance ?d2))
    (test (neq ?name1 ?name2))
    =>
    (assert 
        (ACTION (id pacmanChase) (info (str-cat "Perseguir múltiples fantasmas comestibles: " ?name1 " y " ?name2)) (priority 75)
            (chasestrategypacman ?name1)
        )
    )
)


;14 MOVERSE ALEATORIAMENTE SI NO HAY NADA CERCA QUE EVALUAR - PRIORIDAD 5
(defrule move-randomly-if-no-ghosts-or-pills
    (not (NEAREST-GHOST))
    (not (NEAREST-EDIBLE-GHOST))
    (not (PILL))
    (not (POWERPILL))
    =>
    (assert
        (ACTION (id pacmanRandomMove) (info "Movimiento aleatorio") (priority 75)
            (moverandom "random")
        )
    )
)

;15 MOVERSE HACIA EL CENTRO DEL LABERINTO SI NO HAY OTROS OBJETIVOS (esto se debe a que hay mas posibles caminos y por tanto es mas seguro) - PRIORIDAD 10
(defrule move-towards-center-if-no-other-goals
    (not (NEAREST-GHOST))
    (not (NEAREST-EDIBLE-GHOST))
    (not (PILL))
    (not (POWERPILL))
    (PACMAN (position ?pos))
    =>
    (assert 
        (ACTION (id pacmanMoveToCenter) (info "Moverse hacia el centro del laberinto si no hay otros objetivos") (priority 10)
            (movetocenter "center")
        )
    )
)

;16 EVITAR ESQUINAS SI NO HAY PILDORAS O FANTASMAS COMESTIBLES CERCA - PRIORIDAD 20
(defrule avoid-corners-if-no-pills-or-edible-ghosts
    (not (PILL (distance ?pd)))
    (not (POWERPILL (distance ?ppd)))
    (not (NEAREST-EDIBLE-GHOST))
    (PACMAN (neightbour-nodes ?nn))
    (test (< ?nn 2))
    =>
    (assert 
        (ACTION (id pacmanAvoidCorner) (info "Evitar esquinas si no hay píldoras o fantasmas comestibles cerca") (priority 20)
            (avoidcorner "corner")
        )
    )
)

;17 EVITAR POWERPILLS SI FANTASMAS NO COMESTIBLES CERCA O ESTAN EN LA JAULA - PRIORIDAD 30
(defrule avoid-powerpills-if-ghosts-near-or-in-lair
    
    (NEAREST-GHOST (name ?n) (distance ?d))
    (POWERPILL (distance ?pd))
    (test (or (< ?d 75) (= ?pd -1)))
    =>
    (assert 
        (ACTION (id pacmanAvoidPowerPill) (info "Evitar powerpills si hay fantasmas no comestibles cerca o estan en la jaula") (priority 30)
            (avoidcorner "corner")
        )
    )
)

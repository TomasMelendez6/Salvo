package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private Date joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> ships;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Salvo> salvoes;



    //Constructors
    public GamePlayer() {
    }

    public GamePlayer(Date joinDate, Player player, Game game) {
        this.joinDate = joinDate;
        this.player = player;
        this.game = game;
    }


    //Getters
    public long getId() {
        return id;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    @JsonIgnore
     public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }


   //Methods
    public void addShip(Ship ship) {
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvoes.add(salvo);
    }

    //Genero un DTO para GP que incluye los datos del usuario
    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
        return dto;
    }

    //Lista de DTO de cada barco para cada GP
    public List<Map<String, Object>> getAllShips() {
        return ships
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList());
    }
    /*DTO para obtener datos de el game correspondente al gamePlayer que utiliza el metodo,
    * brinda informacion sobre ambos gp relacionados al game en cuestion,
    * tambien contiene dto de los ships gp principal y sus salvoes.
    * */
    public Map<String, Object> makeGamePlayerDTO2(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", game.getAllGamePlayers());
        dto.put("ships", getAllShips());
        dto.put("salvoes", game.getAllSalvoesFromGamePlayers());
        dto.put("hits", "");//makeHitsDTO());
        return dto;

    }
/*
    private Map<String, Object> makeHitsDTO() {
        GamePlayer self = this;
        GamePlayer opponent = getOpponent();
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (opponent != null){
            dto.put("self", self.getAllSelfHits(opponent));
            dto.put("opponent", opponent.getAllSelfHits(self));
        }
        else {
            dto.put("self", "");
            dto.put("opponent", "");
        }
        return dto;
    }

    private GamePlayer getOpponent() {
        GamePlayer opponent = null;
        for (GamePlayer gp: game.getGamePlayers()) {
            if (gp.getId() != id){
                opponent = gp;
            }
        }
        return opponent;
    }

    public List<Map<String, Object>> getAllSelfHits(GamePlayer opponent) {
        return salvoes
                .stream()
                .map(salvo -> makeSalvoDTO(opponent, salvo))
                .collect(Collectors.toList());
    }

    private Map<String, Object> makeSalvoDTO(GamePlayer opponent, Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        int contCarri = 0, contBattle = 0, contSub = 0, contDest = 0, contPat = 0;
        //int contCarri2 = 0, contBattle2 = 0, contSub2 = 0, contDest2 = 0, contPat2 = 0;
        List<String> listAux = null;
        dto.put("turn", salvo.getTurno());
        for (String location: salvo.getSalvoLocations()) {
            for (Ship ship: opponent.getShips()) {
                for (String location2: ship.getShipLocations()) {
                    listAux.add(location);
                    if (location.equals(location2)){


                        REVISAR FUNCION DE FEDE Y JUANSE PARA SACAR ESTOS FOREACHs ASQUEROSOS.



                        switch (ship.getType()){
                            case "Carrier":
                                contCarri ++;
                                break;
                            case "Battleship":
                                contBattle ++;
                                break;
                            case "Submarine":
                                contSub ++;
                                break;
                            case "Destroyer":
                                contDest ++;
                                break;
                            case "Patrol":
                                contPat ++;
                                break;
                        }
                    }
                }
            }
        }
        dto.put("hitLocations", listAux);
        dto.put("damages", "makeDamageDTO(contCarri....)");
        return dto;
    }

    public Map<String, Object> makerDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        for (String location: salvo.getSalvoLocations()) {
            if(ships.stream().map(ship -> ))
        }
        return dto;
    }

 */




}

package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

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

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gpid", this.getId());
        dto.put("id", this.getPlayer().getId());
        dto.put("name", this.getPlayer().getId());
        return dto;
    }


    public List<Map<String, Object>> getAllShips() {
        return ships
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList());
    }

    public Map<String, Object> makeGamePlayerDTO2() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.game.getId());
        dto.put("created", this.game.getCreationDate());
        dto.put("gamePlayers", this.game.getAllGamePlayers(this.game.getGamePlayers()));
        dto.put("ships", this.getAllShips());
        dto.put("salvoes", this.game.getAllSalvoesFromGamePlayers());
        return dto;

    }

    public void addShip(Ship ship) {
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvoes.add(salvo);
    }

}

package com.codeoftheweb.salvo.models;

import com.codeoftheweb.salvo.models.GamePlayer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Salvo {
    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoLocations")
    private Set<String> salvoLocations = new HashSet<>();

    //Constructors
    public Salvo() {
    }

    public Salvo(int turno, GamePlayer gamePlayer, Set<String> salvosLocations) {
        this.turn = turno;
        this.gamePlayer = gamePlayer;
        this.salvoLocations = salvosLocations;
    }

    public Salvo(int turno, Set<String> salvoLocations) {
        this.turn = turno;
        this.salvoLocations = salvoLocations;
    }

    //Getters
    public long getId() {
        return id;
    }

    public int getTurno() {
        return turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Set<String> getSalvoLocations() {
        return salvoLocations;
    }

//Setters


    public void setTurn(int turn) {
        this.turn = turn;
    }
    public void setSalvoLocations(Set<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    //Methods
    public void addSalvoLocation(String salvoLocation) {
        salvoLocations.add(salvoLocation);
    }

    public void setGamePlayer(GamePlayer gp) {
        this.gamePlayer = gp;
    }

    //DTO de salvo
    public Map<String, Object> makeSalvoDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", getTurno());
        dto.put("player", getGamePlayer().getPlayer().getId());
        dto.put("locations", getSalvoLocations());
        return dto;
    }
}

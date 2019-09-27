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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoesLocations")
    private Set<String> salvoesLocations = new HashSet<>();

    //Constructors
    public Salvo() {
    }

    public Salvo(int turno, GamePlayer gamePlayer, Set<String> salvoesLocations) {
        this.turno = turno;
        this.gamePlayer = gamePlayer;
        this.salvoesLocations = salvoesLocations;
    }

    //Getters


    public long getId() {
        return id;
    }

    public int getTurno() {
        return turno;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Set<String> getSalvoesLocations() {
        return salvoesLocations;
    }

    //Methods

    public void addSalvoLocation(String salvoLocation) {
        salvoesLocations.add(salvoLocation);
    }

    public Map<String, Object> makeSalvoDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurno());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("salvoLocations", this.getSalvoesLocations());
        return dto;
    }

}

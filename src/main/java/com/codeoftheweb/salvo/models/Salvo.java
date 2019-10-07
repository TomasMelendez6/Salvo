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

    private int turno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoLocations")
    private Set<String> salvoLocations = new HashSet<>();

    //Constructors
    public Salvo() {
    }

    public Salvo(int turno, GamePlayer gamePlayer, Set<String> salvoesLocations) {
        this.turno = turno;
        this.gamePlayer = gamePlayer;
        this.salvoLocations = salvoesLocations;
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
        return salvoLocations;
    }

    //Methods
    public void addSalvoLocation(String salvoLocation) {
        salvoLocations.add(salvoLocation);
    }

    //DTO de salvo
    public Map<String, Object> makeSalvoDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurno());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.getSalvoesLocations());
        return dto;
    }

}

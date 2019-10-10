package com.codeoftheweb.salvo.models;

import com.codeoftheweb.salvo.models.GamePlayer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {
    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="locations")
    private Set<String> locations = new HashSet<>();

    //Getters
    public Ship() {
    }

    public Ship(GamePlayer gamePlayer, String type, Set<String> shipLocations) {
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.locations = shipLocations;
    }

    public Ship(String type, Set<String> shipLocations) {
        this.type = type;
        this.locations = shipLocations;
    }
    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Set<String> getLocations() {
        return locations;
    }

    //Setters


    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    //Metodos
    public void addShipLocation(String shipLocation) {
        locations.add(shipLocation);
    }

    //DTO del ship
    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", getType());
        dto.put("locations", getLocations());
        return dto;
    }


}
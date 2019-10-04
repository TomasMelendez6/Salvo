package com.codeoftheweb.salvo.models;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {
    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;        //los tres @ hacen que el id sea auto generado con la instanciacion

    private String userName;

    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> scores;



    //Constructores
    public Player() {
    }

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    //Getters
    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public String getPassword() {
        return password;
    }

    //Setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //Methods
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score) {
        scores.add(score);
    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }
/*
       //Codigo hecho para /api/leaderBoard, no se borro por si se llega a necesitar

    //DTO para agregar informacion de un player a la tabla de leaderboard.
    public Map<String, Object> makePlayerDTO2() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Map<String, Object> score = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("email", this.userName);
        dto.put("score", score);
            score.put("total", this.getTotal());
            score.put("won", this.getByResult(1));
            score.put("lost", this.getByResult(0));
            score.put("tied", this.getByResult(0.5));
        return dto;
    }

    private double getByResult(double result) {
        return scores.stream().filter(score -> score.getScore() == result)
                .map(score -> score.getScore()).count();
    }

    private double getTotal() {
        List<Double> list = scores.stream().map(score -> score.getScore()).collect(Collectors.toList());
        return list.stream().reduce((double) 0, (subtotal, score) -> subtotal + score);
    }
*/
}

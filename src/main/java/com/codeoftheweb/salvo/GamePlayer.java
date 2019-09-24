package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

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

    //Constructor
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
}

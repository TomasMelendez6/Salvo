package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;



    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @RequestMapping("/games")
    public List<Map<String, Object>> getAllGames() {
        return gameRepo.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getSomething(@PathVariable Long gamePlayerId){
        //Game game = gamePlayerRepo.findById(gamePlayerId).get().getGame();
        return gamePlayerRepo
                .findById(gamePlayerId)
                .get()
                .makeGamePlayerDTO2();
    }





}
/*
    @RequestMapping("/players")
    public List<Long> getAllPlayers() {
        return playerRepo.findAll()
                .stream()
                .map(player -> player.getId())
                .collect(Collectors.toList());
    }

    @RequestMapping("/gamePlayers")
    public List<Long> getAllGamePlayers() {
        return gamePlayerRepo.findAll()
                .stream()
                .map(gamePlayer -> gamePlayer.getId())
                .collect(Collectors.toList());
    }
}
 */


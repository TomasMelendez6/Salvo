package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoRestController {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @RequestMapping("/games")
    public List<Map<String, Object>> getAllGames() {
        return gameRepo.findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(Collectors.toList());
    }

    private Map<String, Object> makeGameDTO(Game game){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id:", game.getId());
        dto.put("Date:", game.getCreationDate());
        dto.put("gamePlayer:", getAllGamePlayers(game.getGamePlayers()));
        return dto;
    }

    private List<Map<String, Object>> getAllGamePlayers(Set<GamePlayer> gamePlayers){
        return gamePlayers
                .stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(Collectors.toList());
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id:", gamePlayer.getId());
        dto.put("Date:", gamePlayer.getJoinDate());
        return dto;
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

 */
}

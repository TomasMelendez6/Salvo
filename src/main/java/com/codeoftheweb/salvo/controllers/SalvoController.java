package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
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


package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

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

    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> getLeaderboard() {
        return playerRepo.findAll()
                .stream()
                .map(player -> player.makePlayerDTO2())
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String userName, @RequestParam String password) {

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepo.findByUserName(userName) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepo.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}


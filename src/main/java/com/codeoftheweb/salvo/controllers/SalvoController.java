package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collector;
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
    public Map<String, Object> getAllGames2(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if(makeCurrentPlayerDTO(authentication) != null){
            dto.put("player", makeCurrentPlayerDTO(authentication));
        }
        else{
            dto.put("player", "Guest");
        }
        dto.put("games", getAllGames());
        return dto;
    }

    public Map<String, Object> makeCurrentPlayerDTO(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Player p =  getPlayerLogin(authentication);
        if(p != null){
            dto = p.makePlayerDTO();
        }
        else{
            dto = null;
        }
        return dto;
    }

    private Player getPlayerLogin(Authentication authentication) {
        List<Player> listPlayerLogin = new ArrayList<>();
        // List players login
        if (!isGuest(authentication)) {
            listPlayerLogin = playerRepo.findAll()
                    .stream()
                    .filter(player -> player.getUserName().equals(authentication.getName()))
                    .collect(Collectors.toList());
        }
        // Get player authentication
        if (listPlayerLogin.isEmpty()) {
            return null;
        } else {
            return listPlayerLogin.get(0);
        }

    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    public List<Map<String, Object>> getAllGames() {
        return gameRepo.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getSomething(@PathVariable Long gamePlayerId){
        return gamePlayerRepo
                .findById(gamePlayerId)
                .get()
                .makeGamePlayerDTO2();
    }
/*
    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> getLeaderboard() {
        return playerRepo.findAll()
                .stream()
                .map(player -> player.makePlayerDTO2())
                .collect(Collectors.toList());
    }

 */

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
                @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepo.findByUserName(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepo.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> newGame(Authentication authentication){
        Player plog = getPlayerLogin(authentication);
        if(plog == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Game g = gameRepo.save(new Game());
        GamePlayer gp = gamePlayerRepo.save(new GamePlayer(new Date(), plog, g));
        return new ResponseEntity<>(makeMap("gp", gp.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String gp, long id) {
        Map<String, Object> map = new HashMap<>();
        map.put(gp, id);
        return map;
    }


}


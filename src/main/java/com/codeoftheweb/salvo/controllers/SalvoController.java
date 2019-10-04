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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {
    //attributes
    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    //Methods
    /*DTO of player information
    *also contains the information from all the games
    */
    @RequestMapping("/games")
    public Map<String, Object> getAllGamesCurrent(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if(!isGuest(authentication)){
            dto.put("player", playerRepo.findByUserName(authentication.getName()).makePlayerDTO());
        }
        else{
            dto.put("player", "Guest");
        }
        dto.put("games", getAllGames());
        return dto;
    }

    //Method to verify if the user is User o Guest.
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //List of DTOs of all the games.
    public List<Map<String, Object>> getAllGames() {
        return gameRepo.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList());
    }
/*
    //DTO of the GP with the id equal to the URL parameter
    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getSomething(@PathVariable Long gamePlayerId){
        return gamePlayerRepo
                .findById(gamePlayerId)
                .get()
                .makeGamePlayerDTO2();
    }
*/

    //DTO del gp con id igual al id que me pasan si y solo si el que busca esa URL tiene permiso de ver
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(Authentication authentication, @PathVariable long gamePlayerId){
        GamePlayer gp = gamePlayerRepo.findById(gamePlayerId).get();
        if(authentication.getName() == gp.getPlayer().getUserName()) {
            return ResponseEntity.ok().body(gp.makeGamePlayerDTO2());
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
/*
    //Metodo reemplazado con codigo de frontEnd
    //DTO para la tabla de posiciones
    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> getLeaderboard() {
        return playerRepo.findAll()
                .stream()
                .map(player -> player.makePlayerDTO2())
                .collect(Collectors.toList());
    }

 */

    //method to register a new player. it will need a username and a psw.
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

    //Method for the "Create game" button in the front end.
    //this method will verify the player credentials.
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame(Authentication authentication){
        if(isGuest(authentication)){
            return new ResponseEntity<>("error", HttpStatus.UNAUTHORIZED);
        }
        Date d1 = new Date();
        Player plog = playerRepo.findByUserName(authentication.getName());
        Game g = gameRepo.save(new Game(d1));
        GamePlayer gp = gamePlayerRepo.save(new GamePlayer(d1, plog, g));
        return new ResponseEntity<>(makeMap("gpid", gp.getId()), HttpStatus.CREATED);
    }

    //DTO to confirm the creation game
    private Map<String, Object> makeMap(String key, long id) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, id);
        return map;
    }

    //Method for the "Join game" button in the front end.
    //this method will verify the player credentials.
    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<String> joinGame(Authentication authentication, @PathVariable long gameId){
        if(isGuest(authentication)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Game cg = gameRepo.findById(gameId).get();
        if (cg == null){
            return new ResponseEntity<>("No such game", HttpStatus.FORBIDDEN);
        }
        if(cg.getGamePlayers().stream().count() < 2){
            return new ResponseEntity<>("Game is full", HttpStatus.FORBIDDEN);
        }
        Date d1 = new Date();
        Player plog = playerRepo.findByUserName(authentication.getName());
        GamePlayer gp = gamePlayerRepo.save(new GamePlayer(d1, plog, cg));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}


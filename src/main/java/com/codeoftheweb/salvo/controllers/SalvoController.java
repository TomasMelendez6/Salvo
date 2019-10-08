package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
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

    @Autowired
    private ShipRepository shipRepo;

    @Autowired
    private SalvoRepository salvoRepo;

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

    //DTO del gp con id igual al id que me pasan si y solo si el que busca esa URL tiene permiso de ver
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(Authentication authentication, @PathVariable long gamePlayerId){
        GamePlayer gp = gamePlayerRepo.findById(gamePlayerId).get();
        if(authentication.getName() == gp.getPlayer().getUserName()) {
            return ResponseEntity.ok().body(gp.makeGamePlayerDTO2(authentication));
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

    //mathod to make a DTO with a key and a value
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    //Method for the "Join game" button in the front end.
    //this method will verify the player credentials.
    @RequestMapping(path = "/game/{gameid}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(Authentication authentication, @PathVariable long gameid){
        if(isGuest(authentication)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Game cg = gameRepo.findById(gameid).get();
        if (!gameRepo.findById(gameid).isPresent()){
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }
        if(cg.getAllGamePlayers().toString().contains(authentication.getName())){
            return new ResponseEntity<>("You can't play against yourself", HttpStatus.FORBIDDEN);
        }
        if(cg.getGamePlayers().stream().count() > 1){
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }
        Date d1 = new Date();
        Player pLog = playerRepo.findByUserName(authentication.getName());
        GamePlayer gp = gamePlayerRepo.save(new GamePlayer(d1, pLog, cg));
        return new ResponseEntity<>(makeMap("gpid", gp.getId()), HttpStatus.CREATED);
    }

    //Method for the "add ships" button in the front end.
    //this method will verify the player credentials.
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships" , method = RequestMethod.POST)
    public ResponseEntity<Object> addShips(Authentication authentication,
                                           @PathVariable long gamePlayerId,
                                            @RequestBody Set<Ship> ships){
        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "there is no current user logged in"), HttpStatus.UNAUTHORIZED);
        }
        GamePlayer gp = gamePlayerRepo.findById(gamePlayerId).orElse(null);
        if(gp == null){
            return new ResponseEntity<>(makeMap("error", "there is no game player with the given ID"), HttpStatus.UNAUTHORIZED);
        }
        if(gp.getPlayer().getId() != playerRepo.findByUserName(authentication.getName()).getId()){
            return new ResponseEntity<>(makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        }
        if(!gp.getShips().isEmpty()){      //verifying if the gp has all the ships that the game admit
            return new ResponseEntity<>(makeMap("error", "the user already has ships placed"), HttpStatus.FORBIDDEN );
        }
        for (Ship ship:ships){
            ship.setGamePlayer(gp);
            shipRepo.save(ship);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    //Method for the "add salvos" button in the front end.
    //this method will verify the player credentials.
    @RequestMapping(path = "/games/players/{gamePlayerId}/salvos" , method = RequestMethod.POST)
    public ResponseEntity<Object> addSalvos(Authentication authentication,
                                           @PathVariable long gamePlayerId,
                                           @RequestBody Salvo salvo){
        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "there is no current user logged in"), HttpStatus.UNAUTHORIZED);
        }
        GamePlayer gp = gamePlayerRepo.findById(gamePlayerId).orElse(null);
        if(gp == null){
            return new ResponseEntity<>(makeMap("error", "there is no game player with the given ID"), HttpStatus.UNAUTHORIZED);
        }
        if(gp.getPlayer().getId() != playerRepo.findByUserName(authentication.getName()).getId()){
            return new ResponseEntity<>(makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        }
        Set<Salvo> salvoes = gp.getSalvoes();
        for (Salvo salvo1:salvoes) {
            if(salvo.getTurno() == salvo1.getTurno()){      //Verifying if this turn already has salvos.
                return new ResponseEntity<>(makeMap("error", "the user already has submitted a salvo for the turn listed"), HttpStatus.FORBIDDEN );
            }
        }
        salvo.setGamePlayer(gp);
        salvoRepo.save(salvo);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }
}


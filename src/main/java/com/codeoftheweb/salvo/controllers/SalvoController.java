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

    @Autowired
    private ScoreRepository scoreRepo;

    //Methods
    /*DTO of player information
    *also contains the information from all the games
    */
    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication){
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

    /* GamePlayer DTO from the gp with de id equals to gamePlayerId
     * the method verifies if the current user can see the data.
     */
    //DTO del gp con id igual al id que me pasan si y solo si el que busca esa URL tiene permiso de ver
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(Authentication authentication, @PathVariable long gamePlayerId){
        GamePlayer gp = gamePlayerRepo.findById(gamePlayerId).get();
        if(authentication.getName() == gp.getPlayer().getUserName()) {
            return ResponseEntity.ok().body(makeGameViewDTO(authentication, gp));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //method to register a new player. it will need a username and a psw.
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
                @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }

        if (playerRepo.findByUserName(email) !=  null) {
            return new ResponseEntity<>(makeMap("error", "Name already in use"), HttpStatus.FORBIDDEN);
        }

        playerRepo.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //Method for the "Create game" button in the front end.
    //this method will verify the player credentials.
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame(Authentication authentication){
        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "is guest"), HttpStatus.UNAUTHORIZED);
        }
        Date d1 = new Date();
        Player plog = playerRepo.findByUserName(authentication.getName());
        Game g = gameRepo.save(new Game(d1));
        GamePlayer gp = gamePlayerRepo.save(new GamePlayer(d1, plog, g));
        return new ResponseEntity<>(makeMap("gpid", gp.getId()), HttpStatus.CREATED);
    }

    //Method for the "Join game" button in the front end.
    //this method will verify the player credentials.
    @RequestMapping(path = "/game/{gameid}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(Authentication authentication, @PathVariable Long gameid){
        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "is guest"), HttpStatus.UNAUTHORIZED);
        }
        Game cg = gameRepo.findById(gameid).get();
        if (!gameRepo.findById(gameid).isPresent()){
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }
        if(getAllGamePlayersDTO(cg.getGamePlayers()).toString().contains(authentication.getName())){
            return new ResponseEntity<>(makeMap("error", "You can't play against yourself"), HttpStatus.FORBIDDEN);
        }
        if(cg.getGamePlayers().size() > 1){
            System.out.println(cg.getGamePlayers().size());
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
                                            @RequestBody List<Ship> ships){
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
        return new ResponseEntity<>(makeMap("OK", "OK"), HttpStatus.CREATED);

    }

    //Method for the "Fire Salvo!" button in the front end.
    //this method will verify the player credentials.
    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes" , method = RequestMethod.POST)
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
        if(salvo.getSalvoLocations().size() > 5){
            return new ResponseEntity<>(makeMap("error", "you cant fire more than 5 salvoes"), HttpStatus.UNAUTHORIZED);
        }
        Set<Salvo> salvoes = gp.getSalvoes();
        for (Salvo salvo1:salvoes) {
            if(salvo.getTurno() == salvo1.getTurno() || gp.getSalvoes().size() > getOpponent(gp).getSalvoes().size()){      //Verifying if this turn already has salvos.
                return new ResponseEntity<>(makeMap("error", "the user already has submitted a salvo for the turn listed"), HttpStatus.FORBIDDEN );
            }
        }
        salvoRepo.save(new Salvo(salvoes.size()+1, gp, salvo.getSalvoLocations()));
        return new ResponseEntity<>(makeMap("OK", "you can fire salvoes"), HttpStatus.CREATED);

    }



    //----------------------------------------------ListDTO-------------------------------------------------------------
    //ship list DTO
    public List<Map<String, Object>> getAllShips(Set<Ship> ships) {
        return orderShips(ships)
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList());
    }

    //To get a list of DTOs with all hits for each ship in every turn.
    public List<Map<String, Object>> getAllHits(GamePlayer gp) {
        List<Map<String, Object>> finalDto = new ArrayList<>();
        int acumCarri = 0, acumBattle = 0, acumSub = 0, acumDest = 0, acumPat = 0;
        List<Salvo> salvoes = orderSalvoes(gp.getSalvoes());
        for (Salvo salvo: salvoes) {
            List<String> hitLocations = new ArrayList<>();
            int contCarri = 0, contBattle = 0, contSub = 0, contDest = 0, contPat = 0;
            for (Ship ship: getOpponent(gp).getShips()) {
                List<String> similar = new ArrayList<>(salvo.getSalvoLocations());
                similar.retainAll(ship.getLocations());
                int equals = similar.size();
                if (equals != 0){
                    hitLocations.addAll(similar);
                    switch (ship.getType()){
                        case "carrier":
                            contCarri += equals;
                            acumCarri += equals;
                            break;
                        case "battleship":
                            contBattle += equals;
                            acumBattle += equals;
                            break;
                        case "submarine":
                            contSub += equals;
                            acumSub += equals;
                            break;
                        case "destroyer":
                            contDest += equals;
                            acumDest += equals;
                            break;
                        case "patrolboat":
                            contPat += equals;
                            acumPat += equals;
                            break;
                    }
                }
            }
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("turn", salvo.getTurno());
            dto.put("hitLocations", hitLocations);
            dto.put("damages", makeDamageDTO(contCarri, contBattle, contSub, contDest, contPat, acumCarri, acumBattle, acumSub, acumDest, acumPat));
            dto.put("missed", salvo.getSalvoLocations().size() - hitLocations.size());
            finalDto.add(dto);
        }
        return finalDto;
    }

    //List of games DTO.
    public List<Map<String, Object>> getAllGames() {
        return gameRepo.findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(Collectors.toList());
    }

    //list of GamePlayer DTOs.
    public List<Map<String, Object>> getAllGamePlayersDTO(Set<GamePlayer> gamePlayers) {
        return orderGamePlayers(gamePlayers)
                .stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(Collectors.toList());
    }

    //List of Score DTO for each GamePlayer for an specific game
    public List<Map<String, Object>> getAllScoresFromGamePlayersDTO(Set<Score> scores) {
        return scores
                .stream()
                .map(score -> score.makeScoreDTO())
                .collect(Collectors.toList());

    }

    //List of all Salvoes DTOs for both GamePlayer in a game
    public List<Map<String, Object>> getAllSalvoesFromGamePlayersDTO(Set<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .flatMap(gamePlayer -> orderSalvoes(gamePlayer.getSalvoes()).stream())
                .map(salvo -> salvo.makeSalvoDTO())
                .collect(Collectors.toList());
    }


    //----------------------------------------------DTO-----------------------------------------------------------------
    //mathod to make a DTO with a key and a value
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /*DTO to get the data of the logged gamePlayer,
     * bring information from both gamePlayer in the same game,
     * also contain the ship and salvo dto of the logged gamePlayer.
     * */
    public Map<String, Object> makeGameViewDTO(Authentication authentication, GamePlayer gp) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gp.getGame().getId());
        dto.put("created", gp.getGame().getCreationDate());
        dto.put("gameState", getGameState(gp));
        dto.put("gamePlayers", getAllGamePlayersDTO(gp.getGame().getGamePlayers()));
        dto.put("ships", getAllShips(gp.getShips()));
        dto.put("salvoes", getAllSalvoesFromGamePlayersDTO(gp.getGame().getGamePlayers()));
        dto.put("hits", makeHitsDTO(gp));
        return dto;

    }

    //GamePlayer DTO with info about the corresponding player
    public Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> gpDto = new LinkedHashMap<String, Object>();
        gpDto.put("id", gamePlayer.getId());
        gpDto.put("player", gamePlayer.getPlayer().makePlayerDTO());
        return gpDto;
    }

    //DTO of the hits for self and opponent hits
    public Map<String, Object> makeHitsDTO(GamePlayer gamePlayer) {
        GamePlayer opponent = getOpponent(gamePlayer);
        Map<String, Object> hits = new LinkedHashMap<>();
        if (opponent != null){
            hits.put("self", getAllHits(opponent));
            hits.put("opponent", getAllHits(gamePlayer));
        }
        else {
            hits.put("self", new ArrayList<>());
            hits.put("opponent", new ArrayList<>());
        }
        return hits;
    }

    //DTO of the damage of each ship
    private Map<String, Object> makeDamageDTO(int contCarri, int contBattle, int contSub, int contDest, int contPat,
                                              int acumCarri, int acumBattle, int acumSub, int acumDest, int acumPat) {
        Map<String, Object> dmg = new LinkedHashMap<>();
        dmg.put("carrierHits", contCarri);
        dmg.put("battleshipHits", contBattle);
        dmg.put("submarineHits", contSub);
        dmg.put("destroyerHits", contDest);
        dmg.put("patrolboatHits", contPat);
        dmg.put("carrier", acumCarri);
        dmg.put("battleship", acumBattle);
        dmg.put("submarine", acumSub);
        dmg.put("destroyer", acumDest);
        dmg.put("patrolboat", acumPat);
        return dmg;
    }

    //Game DTO.
    public Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", getAllGamePlayersDTO(game.getGamePlayers()));
        dto.put("scores", getAllScoresFromGamePlayersDTO(game.getScores()));
        return dto;
    }


    //----------------------------------------------GamePlayer----------------------------------------------------------
    /*method that return a String object with the state of the current game.
     *the game of the logged player.
     */
    private String getGameState(GamePlayer gp) {
        final int TOTAL_SHIP = 17, MAX_SCORES = 2;
        GamePlayer opp = getOpponent(gp);

        if(gp.getShips().isEmpty()){
            return "PLACESHIPS";
        }
        if(opp == null){
            return "WAITINGFOROPP";
        }

        int selfSalvoes = gp.getSalvoes().size(), oppSalvoes = opp.getSalvoes().size();
        if(selfSalvoes <= oppSalvoes && !gameOver(gp, opp, TOTAL_SHIP) && opp.getShips().size() != 0){// && !gameOver(gp)){
            return "PLAY";
        }
        if(gameOver(gp, opp, TOTAL_SHIP)){
            int totOpp = getTotal(opp), totSelf = getTotal(gp);
            if(totOpp == TOTAL_SHIP && totSelf < TOTAL_SHIP){
                if(gp.getGame().getScores().size() < MAX_SCORES){
                    scoreRepo.save(new Score(gp.getGame(), gp.getPlayer(), 1,new Date()));
                }
                return "WON";
            }

            if(totOpp == TOTAL_SHIP && totSelf == TOTAL_SHIP){
                if(gp.getGame().getScores().size() < MAX_SCORES){
                    scoreRepo.save(new Score(gp.getGame(), gp.getPlayer(), 0.5,new Date()));
                }
                return "TIE";
            }
            if(gp.getGame().getScores().size() < MAX_SCORES){
                scoreRepo.save(new Score(gp.getGame(), gp.getPlayer(), 0,new Date()));
            }
            return "LOST";
        }
        return "WAIT";      //selfSalvoes > oppSalvoes ---> opponent turn!

    }

    //method tah verifies if the game is over.
    private boolean gameOver(GamePlayer gp, GamePlayer opp, int largo) {
        if(gp.getSalvoes().size() == opp.getSalvoes().size()){
            if(getTotal(gp) == largo || getTotal(opp) == largo){
                return true;
            }
        }
        return false;
    }

    //method to get the total hit in the current player ships from the opponent salvoes
    private int getTotal(GamePlayer gp) {
        GamePlayer opp = getOpponent(gp);
        List<String> ships = new ArrayList<>();
        List<String> oppSalvoes = new ArrayList<>();
        for (Ship ship: gp.getShips()) {
            ships.addAll(ship.getLocations());
        }
        for (Salvo salvo: opp.getSalvoes()) {
            oppSalvoes.addAll(salvo.getSalvoLocations());
        }

        ships.retainAll(oppSalvoes);
        return ships.size();
    }

    //Method to get the opponent gamePlayer of the current one
    private GamePlayer getOpponent(GamePlayer gamePlayer) {
        GamePlayer opponent = null;
        for (GamePlayer gp: gamePlayer.getGame().getGamePlayers()) {
            if (gp.getId() != gamePlayer.getId()){
                opponent = gp;
            }
        }
        return opponent;
    }


    //-------------------------------------------------------Player-----------------------------------------------------
    //Method to verify if the user is User o Guest.
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //-------------------------------------------------------Order------------------------------------------------------
    //method to order salvoes by turn
    private List<Salvo> orderSalvoes(Set<Salvo> salvoes){
        return salvoes
                .stream()
                .sorted(Comparator.comparing(Salvo::getTurno))
                .collect(Collectors.toList());
    }

    //to order GamePlayer by gpId
    private List<GamePlayer> orderGamePlayers(Set<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .sorted(Comparator.comparing(GamePlayer::getId))
                .collect(Collectors.toList());
    }

    //to order ships by shipId
    private List<Ship> orderShips(Set<Ship> ships) {
        return  ships
                .stream()
                .sorted(Comparator.comparing(Ship::getId))
                .collect(Collectors.toList());
    }
}


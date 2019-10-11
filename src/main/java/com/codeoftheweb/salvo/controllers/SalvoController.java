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

    /*DTO to get the data of the logged gamePlayer,
     * bring information from both gamePlayer in the same game,
     * also contain the ship and salvo dto of the logged gamePlayer.
     * */
    public Map<String, Object> makeGameViewDTO(Authentication authentication, GamePlayer gp) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gp.getGame().getId());
        dto.put("created", gp.getGame().getCreationDate());
        dto.put("gameState", gatGameState(gp));
        dto.put("gamePlayers", getAllGamePlayersDTO(gp.getGame().getGamePlayers()));
        dto.put("ships", getAllShips(gp.getShips()));
        dto.put("salvoes", getAllSalvoesFromGamePlayersDTO(gp.getGame().getGamePlayers()));
        dto.put("hits", makeHitsDTO(gp));
        return dto;

    }
    /*DTO to get the state of the current game.
    *the game of the logged player.
     */
    private String gatGameState(GamePlayer gp) {
        final int TOTAL_SHIP = 17;
        GamePlayer opp = getOpponent(gp);


        if(gp.getShips().isEmpty()){
            return "PLACESHIPS";
        }
        if(opp == null){
            return "WAITINGFOROPP";
        }
        if(opp.getShips().isEmpty()){
            return "WAIT";
        }

        int selfSalvoes = gp.getSalvoes().size(), oppSalvoes = opp.getSalvoes().size();
        if(selfSalvoes <= oppSalvoes && !gameOver(gp, opp, TOTAL_SHIP)){// && !gameOver(gp)){
            return "PLAY";
        }
        if (selfSalvoes > oppSalvoes && !gameOver(gp, opp, TOTAL_SHIP)){
            return "WAIT";
        }

        int totOpp = getTotal(opp), totSelf = getTotal(gp);
        if(totOpp == TOTAL_SHIP && totSelf < TOTAL_SHIP){
            return "WON";
        }

        if(totOpp == TOTAL_SHIP && totSelf == TOTAL_SHIP){
            return "TIE";
        }
        return "LOST";

    }

    private boolean gameOver(GamePlayer gp, GamePlayer opp, int largo) {
        if(getTotal(gp) == largo || getTotal(opp) == largo){
            return true;
        }
        return false;
    }

    private int getTotal(GamePlayer gp) {
        GamePlayer opp = getOpponent(gp);
        List<String> ships = new ArrayList<>();
        List<String> salvoes = new ArrayList<>();
        for (Ship ship: gp.getShips()) {
            ships.addAll(ship.getLocations());
        }
        for (Salvo salvo: getOpponent(gp).getSalvoes()) {
            salvoes.addAll(salvo.getSalvoLocations());
        }

        ships.retainAll(salvoes);
        int total = ships.size();
        return total;
    }

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
        if(getAllGamePlayersDTO(cg.getGamePlayers()).toString().contains(authentication.getName())){
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
        return new ResponseEntity<>(makeMap("OK", "OK"), HttpStatus.CREATED);

    }

    //Method for the "add salvos" button in the front end.
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
        Set<Salvo> salvoes = gp.getSalvoes();
        for (Salvo salvo1:salvoes) {
            if(salvo.getTurno() == salvo1.getTurno() || gp.getSalvoes().size() > getOpponent(gp).getSalvoes().size()){      //Verifying if this turn already has salvos.
                return new ResponseEntity<>(makeMap("error", "the user already has submitted a salvo for the turn listed"), HttpStatus.FORBIDDEN );
            }
        }
        salvoRepo.save(new Salvo(salvoes.size()+1, gp, salvo.getSalvoLocations()));
        return new ResponseEntity<>(makeMap("OK", "you can fire salvoes"), HttpStatus.CREATED);

    }


    //----------------------------------------------GamePlayer DTOs----------------------------------------------

    //Lista de DTO de cada barco para cada GP
    public List<Map<String, Object>> getAllShips(Set<Ship> ships) {
        return ships
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList());
    }

    //Genero un DTO para GP que incluye los datos del usuario
    public Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> gpDto = new LinkedHashMap<String, Object>();
        gpDto.put("id", gamePlayer.getId());
        gpDto.put("player", gamePlayer.getPlayer().makePlayerDTO());
        return gpDto;
    }

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

    private GamePlayer getOpponent(GamePlayer gamePlayer) {
        GamePlayer opponent = null;
        for (GamePlayer gp: gamePlayer.getGame().getGamePlayers()) {
            if (gp.getId() != gamePlayer.getId()){
                opponent = gp;
            }
        }
        return opponent;
    }

    public List<Map<String, Object>> getAllHits(GamePlayer gp) {
        List<Map<String, Object>> finalDto = new ArrayList<>();
        int acumCarri = 0, acumBattle = 0, acumSub = 0, acumDest = 0, acumPat = 0;
        for (Salvo salvo: gp.getSalvoes()) {
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


    //-------------------------------------------------------Game DTOs-------------------------------------

    //List of DTOs of all the games.
    public List<Map<String, Object>> getAllGames() {
        return gameRepo.findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(Collectors.toList());
    }

    //Genero un DTO de game listarlo en el controller y obtener toda la info de todos los games.
    public Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", getAllGamePlayersDTO(game.getGamePlayers()));
        dto.put("scores", getAllScoresFromGamePlayersDTO(game.getScores()));
        return dto;
    }

    //lista de DTO de todos los GP para cada game
    public List<Map<String, Object>> getAllGamePlayersDTO(Set<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(Collectors.toList());
    }

    //Lista de DTO de todos los puntajes de cada GP de cada game
    public List<Map<String, Object>> getAllScoresFromGamePlayersDTO(Set<Score> scores) {
        return scores
                .stream()
                .map(score -> score.makeScoreDTO())
                .collect(Collectors.toList());

    }

    //Lista de Data Transfer Object de todos los salvoes correspondientes a cada GP de cada Game.
    public List<Map<String, Object>> getAllSalvoesFromGamePlayersDTO(Set<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .flatMap(gamePlayer -> gamePlayer.getSalvoes().stream())
                .map(salvo -> salvo.makeSalvoDTO())
                .collect(Collectors.toList());
    }



}


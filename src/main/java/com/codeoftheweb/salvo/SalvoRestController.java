package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

    /*
    @RequestMapping("/games")
    public List<Long> getAllGames() {
        return gameRepo.findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList());
    }
*/
    /*
    @RequestMapping("/games")
    public Map<Long, Date> getAllGames() {
        return {gameRepo.findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList()),
                gameRepo.findAll()
                        .stream()
                        .map(game -> game.getCreationDate())
                        .collect(Collectors.toList())
        };
    }

     */
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

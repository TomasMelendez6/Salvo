package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    List<Game> findByJoinDate(Date joinDate);
}

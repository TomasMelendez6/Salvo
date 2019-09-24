package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByCreationDate(Date creationDate);
}

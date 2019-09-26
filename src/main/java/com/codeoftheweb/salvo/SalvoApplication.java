package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository) {
		return (args) -> {
			Player p1 = new Player("j.bauer@ctu.gov");
			Player p2 = new Player("c.obrian@ctu.gov");
			Player p3 = new Player("kim_bauer@gmail.com");
			Player p4 = new Player("t.almeida@ctu.gov");
			playerRepository.saveAll(Arrays.asList(p1, p2, p3, p4));

			Date d1 = new Date();
			Date d2 = Date.from(d1.toInstant().plusSeconds(3600));
			Date d3 = Date.from(d1.toInstant().plusSeconds(7200));
			Date d4 = Date.from(d1.toInstant().plusSeconds(10800));
			Date d5 = Date.from(d1.toInstant().plusSeconds(14400));
			Date d6 = Date.from(d1.toInstant().plusSeconds(18000));
			Date d7 = Date.from(d1.toInstant().plusSeconds(21600));
			Date d8 = Date.from(d1.toInstant().plusSeconds(25200));

			Game g1 = new Game(d1);
			Game g2 = new Game(d2);
			Game g3 = new Game(d3);
			Game g4 = new Game(d4);
			Game g5 = new Game(d5);
			Game g6 = new Game(d6);
			Game g7 = new Game(d7);
			Game g8 = new Game(d8);
			gameRepository.saveAll(Arrays.asList(g1, g2, g3, g4, g5, g6, g7, g8));

			GamePlayer gp1 = new GamePlayer(d1, p1, g1);
			GamePlayer gp2 = new GamePlayer(d1, p2, g1);
			GamePlayer gp3 = new GamePlayer(d2, p1, g2);
			GamePlayer gp4 = new GamePlayer(d2, p2, g2);
			GamePlayer gp5 = new GamePlayer(d3, p2, g3);
			GamePlayer gp6 = new GamePlayer(d3, p4, g3);
			GamePlayer gp7 = new GamePlayer(d4, p2, g4);
			GamePlayer gp8 = new GamePlayer(d4, p1, g4);
			GamePlayer gp9 = new GamePlayer(d5, p4, g5);
			GamePlayer gp10 = new GamePlayer(d5, p1, g5);
			GamePlayer gp11 = new GamePlayer(d6, p3, g6);
			GamePlayer gp12 = new GamePlayer(d7, p4, g7);
			GamePlayer gp13 = new GamePlayer(d8, p3, g8);
			GamePlayer gp14 = new GamePlayer(d8, p4, g8);
			gamePlayerRepository.saveAll(Arrays.asList(gp1, gp2, gp3,
					gp4, gp5, gp6, gp7, gp8, gp9, gp10, gp11, gp12, gp13, gp14));

			Ship s1 = new Ship(gp1, "Destroyer", new HashSet<>(Arrays.asList("H2", "H3", "H4")));
			Ship s2 = new Ship(gp1, "Submarine", new HashSet<>(Arrays.asList("E1", "F1", "G1")));
			Ship s3 = new Ship(gp1, "Patrol Boat", new HashSet<>(Arrays.asList("B4","B5")));
			Ship s4 = new Ship(gp2, "Destroyer", new HashSet<>(Arrays.asList("B5", "C5", "D5")));
			Ship s5 = new Ship(gp2, "Patrol Boat", new HashSet<>(Arrays.asList("F1", "F2")));

			Ship s6 = new Ship(gp3, "Destroyer", new HashSet<>(Arrays.asList("B5", "C5", "D5")));
			Ship s7 = new Ship(gp3, "Patrol Boat", new HashSet<>(Arrays.asList("C6", "C7")));
			Ship s8 = new Ship(gp4, "Submarine", new HashSet<>(Arrays.asList("A2", "A3", "A4")));
			Ship s9 = new Ship(gp4, "Patrol Boat", new HashSet<>(Arrays.asList("G6", "H6")));

			Ship s10 = new Ship(gp5, "Destroyer", new HashSet<>(Arrays.asList("B5", "C5", "D5")));
			Ship s11 = new Ship(gp5, "Patrol Boat", new HashSet<>(Arrays.asList("C6", "C7")));
			Ship s12 = new Ship(gp6, "Submarine", new HashSet<>(Arrays.asList("A2", "A3", "A4")));
			Ship s13 = new Ship(gp6, "Patrol Boat", new HashSet<>(Arrays.asList("G6", "H6")));

			Ship s14 = new Ship(gp7, "Destroyer", new HashSet<>(Arrays.asList("B5", "C5", "D5")));
			Ship s15 = new Ship(gp7, "Patrol Boat", new HashSet<>(Arrays.asList("C6", "C7")));
			Ship s16 = new Ship(gp8, "Submarine", new HashSet<>(Arrays.asList("A2", "A3", "A4")));
			Ship s17 = new Ship(gp8	, "Patrol Boat", new HashSet<>(Arrays.asList("G6", "H6")));

			Ship s18 = new Ship(gp9, "Destroyer", new HashSet<>(Arrays.asList("B5", "C5", "D5")));
			Ship s19 = new Ship(gp9, "Patrol Boat", new HashSet<>(Arrays.asList("C6", "C7")));
			Ship s20 = new Ship(gp10, "Submarine", new HashSet<>(Arrays.asList("A2", "A3", "A4")));
			Ship s21 = new Ship(gp10, "Patrol Boat", new HashSet<>(Arrays.asList("G6", "H6")));

			Ship s22 = new Ship(gp11, "Destroyer", new HashSet<>(Arrays.asList("B5", "C5", "D5")));
			Ship s23 = new Ship(gp11, "Patrol Boat", new HashSet<>(Arrays.asList("C6", "C7")));

			Ship s24 = new Ship(gp13, "Destroyer", new HashSet<>(Arrays.asList("B5", "C5", "D5")));
			Ship s25 = new Ship(gp13, "Patrol Boat", new HashSet<>(Arrays.asList("C6", "C7")));
			Ship s26 = new Ship(gp14, "Submarine", new HashSet<>(Arrays.asList("A2", "A3", "A4")));
			Ship s27 = new Ship(gp14, "Patrol Boat", new HashSet<>(Arrays.asList("G6", "H6")));

			shipRepository.saveAll(Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8,
					s9, s10, s11, s12, s13, s14, s15, s16, s17, s18, s19, s20, s21,
					s22, s23, s24, s25, s26, s27));

		};
	}
}

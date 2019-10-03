package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
                                      ShipRepository shipRepository,
                                      SalvoRepository salvoRepository,
                                      ScoreRepository scoreRepository) {
        return (args) -> {
            Player p1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
            Player p2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
            Player p3 = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
            Player p4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
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
            Ship s3 = new Ship(gp1, "Patrol Boat", new HashSet<>(Arrays.asList("B4", "B5")));
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
            Ship s17 = new Ship(gp8, "Patrol Boat", new HashSet<>(Arrays.asList("G6", "H6")));

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

            Salvo sa1 = new Salvo(1, gp1, new HashSet<>(Arrays.asList("B5", "C5", "F1")));
            Salvo sa2 = new Salvo(1, gp2, new HashSet<>(Arrays.asList("B4", "B5", "B6")));
            Salvo sa3 = new Salvo(2, gp1, new HashSet<>(Arrays.asList("F2", "D5")));
            Salvo sa4 = new Salvo(2, gp2, new HashSet<>(Arrays.asList("E1", "H3", "A2")));

            Salvo sa5 = new Salvo(1, gp3, new HashSet<>(Arrays.asList("A2", "A4", "G6")));
            Salvo sa6 = new Salvo(1, gp4, new HashSet<>(Arrays.asList("B5", "D5", "C7")));
            Salvo sa7 = new Salvo(2, gp3, new HashSet<>(Arrays.asList("A3", "H6")));
            Salvo sa8 = new Salvo(2, gp4, new HashSet<>(Arrays.asList("C5", "C6")));

            Salvo sa9 = new Salvo(1, gp5, new HashSet<>(Arrays.asList("G6", "H6", "A4")));
            Salvo sa10 = new Salvo(1, gp6, new HashSet<>(Arrays.asList("H1", "H2", "H3")));
            Salvo sa11 = new Salvo(2, gp5, new HashSet<>(Arrays.asList("A2", "A3", "D8")));
            Salvo sa12 = new Salvo(2, gp6, new HashSet<>(Arrays.asList("E1", "F2", "G3")));

            Salvo sa13 = new Salvo(1, gp7, new HashSet<>(Arrays.asList("A3", "A4", "F7")));
            Salvo sa14 = new Salvo(1, gp8, new HashSet<>(Arrays.asList("B5", "C6", "H1")));
            Salvo sa15 = new Salvo(2, gp7, new HashSet<>(Arrays.asList("A2", "G6", "H6")));
            Salvo sa16 = new Salvo(2, gp8, new HashSet<>(Arrays.asList("C5", "C7", "D5")));

            Salvo sa17 = new Salvo(1, gp9, new HashSet<>(Arrays.asList("A1", "A2", "A3")));
            Salvo sa18 = new Salvo(1, gp10, new HashSet<>(Arrays.asList("B5", "B6", "C7")));
            Salvo sa19 = new Salvo(2, gp9, new HashSet<>(Arrays.asList("G6", "G7", "G8")));
            Salvo sa20 = new Salvo(2, gp10, new HashSet<>(Arrays.asList("C6", "D6", "E6")));
            Salvo sa21 = new Salvo(3, gp10, new HashSet<>(Arrays.asList("H1", "H8")));

            salvoRepository.saveAll(Arrays.asList(sa1, sa2, sa3, sa4, sa5, sa6, sa7, sa8, sa9, sa10,
                    sa11, sa12, sa13, sa14, sa15, sa16, sa17, sa18, sa19, sa20, sa21));

            Score sc1 = new Score(g1, p1, 1, d2);
            Score sc2 = new Score(g1, p2, 0, d2);

            Score sc3 = new Score(g2, p1, 0.5, d3);
            Score sc4 = new Score(g2, p2, 0.5, d3);

            Score sc5 = new Score(g3, p2, 1, d4);
            Score sc6 = new Score(g3, p4, 0, d4);

            Score sc7 = new Score(g4, p2, 0.5, d5);
            Score sc8 = new Score(g4, p1, 0.5, d5);

            scoreRepository.saveAll(Arrays.asList(sc1, sc2, sc3, sc4, sc5,
                    sc6, sc7, sc8));

        };
    }
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName -> {
            Player player = playerRepository.findByUserName(inputName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }

}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
            .antMatchers("/web/**").permitAll()
            .antMatchers("/api/games").permitAll()
            .antMatchers("/api/leaderBoard").permitAll()
            .antMatchers("/api/players").permitAll()
            .antMatchers("/**").hasAuthority("USER");
		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");
		http.logout().logoutUrl("/api/logout");


        http.csrf().disable();
		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}


}

package com.user;

import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.entity.User;
import com.user.repository.UserRepository;

@SpringBootApplication
public class UserMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepository rep) {
		return args -> {

			String[] villes = { "Tanger", "Tétouan", "Chefchaouen", "Asilah" };
			int nbrAdmin = 1;
			int nbrCitoyen = 800;
			int nbrVaccinations = 400;

			PasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
			User u = new User();
			Random rnd = new Random();
			
			int i=0;
			for (i = 1; i <= nbrAdmin; i++) {
				u = new User("" + i, null, null, null, null, null, null, null, null, "admin@" + i,
						bcryptEncoder.encode("pass-" + i), "admin");
				rep.save(u);
			}
			for (int j = 1; j <= nbrCitoyen+nbrVaccinations; j++) {
				u = new User("" + i, "nom-" + j, "prenom-" + j, "cin-" + j, (rnd.nextInt(2) == 0 ? "Homme" : "Femme"),
						rnd.nextInt(83) + 18 + "", "06123456789", null, null, "citoyen@" + j,
						bcryptEncoder.encode("pass-" + j), "citoyen");
				rep.save(u);
				i++;
			}
			for (int j = 1; j <= villes.length; j++) {
				u = new User("" + i, null, null, null, null, null, null, "adresse " + villes[j-1], villes[j-1],
						"centre@" + j , bcryptEncoder.encode("pass-" + j), "centre");
				rep.save(u);
				i++;
			}
		};
	}
}

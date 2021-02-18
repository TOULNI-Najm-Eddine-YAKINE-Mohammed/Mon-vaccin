package com.vaccination;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.user.entity.User;
import com.vaccination.entity.Vaccination;
import com.vaccination.repository.VaccinationRepository;

@SpringBootApplication
public class VaccinationMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaccinationMicroserviceApplication.class, args);
	}
	
	@Bean public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public CommandLineRunner demoData(VaccinationRepository rep) {
		return args -> {
			
			int nbrAdmin = 1;
			int nbrVaccinations = 400;
			int nbrCitoyen = 800;
			
			String[] villes = { "Tanger", "Tétouan", "Chefchaouen", "Asilah" };
			int[] vaccVil = {120,80,110,90};

			PasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
			List<User> centres = new ArrayList<>();
			int j = nbrAdmin+nbrCitoyen+nbrVaccinations+1;
			Random rnd = new Random();
			
			for (int i = 1; i <= villes.length; i++) {
				User u = new User("" + j, null, null, null, null, null, null, "adresse " + villes[i-1], villes[i-1],
						"centre@" + i, bcryptEncoder.encode("pass-" + i), "centre");
				j++;
				centres.add(u);
			}
			for (int i = 1; i < vaccVil.length; i++) {
				vaccVil[i]+=vaccVil[i-1];
			}
			for (int i = 1; i <= nbrVaccinations; i++) {
				User citoyen = new User("" + (i+nbrAdmin) , "nom-" + i, "prenom-" + i, "cin-" + i, (rnd.nextInt(2) == 0 ? "Homme" : "Femme"),
						rnd.nextInt(83) + 18 + "", "06123456789", null, null, "citoyen@" + i,
						bcryptEncoder.encode("pass-" + i), "citoyen");
				User centre = new User();
				int k = i-1;
				if(k<vaccVil[0]) {
					centre = centres.get(0);
				} else if(k>=vaccVil[0] && k<vaccVil[1]) {
					centre = centres.get(1);
				} else if(k>=vaccVil[1] && k<vaccVil[2]) {
					centre = centres.get(2);
				} else if(k>=vaccVil[2] && k<vaccVil[3]) {
					centre = centres.get(3);
				}
				String pattern = "dd-MM-yyyy_HH:mm";
				SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, Locale.FRANCE);
				String date = simpleDateFormat.format(new Date());
				if(!rep.existsByCitoyenCin(citoyen.getCin())) {
					Vaccination vac = new Vaccination(""+i, date, citoyen, centre);
					rep.save(vac);
				}
			}
		};
	}
}

package com.rdv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.rdv.entity.Rdv;
import com.rdv.repository.RdvRepository;
import com.user.entity.User;

@SpringBootApplication
public class RdvMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RdvMicroserviceApplication.class, args);
	}
	
	@Bean public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public CommandLineRunner demoData(RdvRepository rep) {
		return args -> {
			
			int nbrAdmin = 1;
			int nbrVaccinations = 400;
			int nbrCitoyen = 800;
			
			String[] villes = { "Tanger", "Tétouan", "Chefchaouen", "Asilah" };
			String[] heures = { "_10:00-12:00", "_12:00-14:00", "_14:00-16:00"};
			int[] nbrsInV1 = {80,30,70,10,20,30,70};//300
			int[] nbrsInV2 = {20,30,40,10,50,40,20};//250
			int[] nbrsInV3 = {20,10,30,20,20,10,30};//230
			int[] nbrsInV4 = {20,10,20,20,30,20,20};//220
			
			PasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
			List<User> centres = new ArrayList<>();
			List<String> dates = new ArrayList<>();
			Random rnd = new Random();
			int j = nbrAdmin+nbrCitoyen+nbrVaccinations+1;
			
			for (int i = 1; i <= villes.length; i++) {
				User u = new User("" + j, null, null, null, null, null, null, "adresse " + villes[i-1], villes[i-1],
						"centre@" + i, bcryptEncoder.encode("pass-" + i), "centre");
				j++;
				centres.add(u);
			}
			int tt1 = 0;
			int tt2 = 0;
			int tt3 = 0;
			int tt4 = 0;
			for(int i=0; i<7; i++) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, +i);
				String pattern = "dd-MM-yyyy";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.FRANCE);
				String d = simpleDateFormat.format(cal.getTime());
				dates.add(d);
				tt1 += nbrsInV1[i];
				tt2 += nbrsInV2[i];
				tt3 += nbrsInV3[i];
				tt4 += nbrsInV4[i];
			}
			tt1 += nbrVaccinations;
			tt2 += tt1;
			tt3 += tt2;
			tt4 += tt3;

			nbrsInV1[0]+=nbrVaccinations;
			nbrsInV2[0]+=tt1;
			nbrsInV3[0]+=tt2;
			nbrsInV4[0]+=tt3;
			for(int i=1; i<7; i++) {
				nbrsInV1[i]+=nbrsInV1[i-1];
				nbrsInV2[i]+=nbrsInV2[i-1];
				nbrsInV3[i]+=nbrsInV3[i-1];
				nbrsInV4[i]+=nbrsInV4[i-1];
			}
			int i = 0;
			for (i = nbrVaccinations+nbrAdmin+1; i <= nbrCitoyen+nbrVaccinations+nbrAdmin; i++) {
				User citoyen = new User("" + i, "nom-" + (i-nbrAdmin), "prenom-" + (i-nbrAdmin), "cin-" + (i-nbrAdmin), (rnd.nextInt(2) == 0 ? "Homme" : "Femme"),
						rnd.nextInt(83) + 18 + "", "06123456789", null, null, "citoyen@" + (i-nbrAdmin),
						bcryptEncoder.encode("pass-" + (i-nbrAdmin)), "citoyen");
				User centre = new User();
				String date = "";
				int k = i-nbrAdmin-1;
				if(k<tt1) {
					if(k<nbrsInV1[0]) {
						date = dates.get(0);
					} else if(i>=nbrsInV1[0] && k<nbrsInV1[1]) {
						date = dates.get(1);
					} else if(k>=nbrsInV1[1] && k<nbrsInV1[2]) {
						date = dates.get(2);
					} else if(k>=nbrsInV1[2] && k<nbrsInV1[3]) {
						date = dates.get(3);
					} else if(k>=nbrsInV1[3] && k<nbrsInV1[4]) {
						date = dates.get(4);
					} else if(k>=nbrsInV1[4] && k<nbrsInV1[5]) {
						date = dates.get(5);
					} else if(k>=nbrsInV1[5] && k<nbrsInV1[6]) {
						date = dates.get(6);
					}
					centre = centres.get(0);
				} else if(k>=tt1 && k<tt2) {
					if(k<nbrsInV2[0]) {
						date = dates.get(0);
					} else if(k>=nbrsInV2[0] && k<nbrsInV2[1]) {
						date = dates.get(1);
					} else if(k>=nbrsInV2[1] && k<nbrsInV2[2]) {
						date = dates.get(2);
					} else if(k>=nbrsInV2[2] && k<nbrsInV2[3]) {
						date = dates.get(3);
					} else if(k>=nbrsInV2[3] && k<nbrsInV2[4]) {
						date = dates.get(4);
					} else if(k>=nbrsInV2[4] && k<nbrsInV2[5]) {
						date = dates.get(5);
					} else if(k>=nbrsInV2[5] && k<nbrsInV2[6]) {
						date = dates.get(6);
					}
					centre = centres.get(1);
				} else if(k>=tt2 && k<tt3) {
					if(k<nbrsInV3[0]) {
						date = dates.get(0);
					} else if(k>=nbrsInV3[0] && k<nbrsInV3[1]) {
						date = dates.get(1);
					} else if(k>=nbrsInV3[1] && k<nbrsInV3[2]) {
						date = dates.get(2);
					} else if(k>=nbrsInV3[2] && k<nbrsInV3[3]) {
						date = dates.get(3);
					} else if(k>=nbrsInV3[3] && k<nbrsInV3[4]) {
						date = dates.get(4);
					} else if(k>=nbrsInV3[4] && k<nbrsInV3[5]) {
						date = dates.get(5);
					} else if(k>=nbrsInV3[5] && k<nbrsInV3[6]) {
						date = dates.get(6);
					}
					centre = centres.get(2);
				} else if(k>=tt3 && k<tt4) {
					if(k<nbrsInV4[0]) {
						date = dates.get(0);
					} else if(k>=nbrsInV4[0] && k<nbrsInV4[1]) {
						date = dates.get(1);
					} else if(k>=nbrsInV4[1] && k<nbrsInV4[2]) {
						date = dates.get(2);
					} else if(k>=nbrsInV4[2] && k<nbrsInV4[3]) {
						date = dates.get(3);
					} else if(k>=nbrsInV4[3] && k<nbrsInV4[4]) {
						date = dates.get(4);
					} else if(k>=nbrsInV4[4] && k<nbrsInV4[5]) {
						date = dates.get(5);
					} else if(k>=nbrsInV4[5] && k<nbrsInV4[6]) {
						date = dates.get(6);
					}
					centre = centres.get(3);
				}
				
				int rndm = rnd.nextInt(3);
				if(rndm == 0)
					date += heures[0];
				else if(rndm == 1)
					date += heures[1];
				else if(rndm == 2)
					date += heures[2];
				
				Rdv rdv = new Rdv(""+(i-(nbrVaccinations+nbrAdmin)), date, citoyen, centre);
				rep.save(rdv);
			}
		};
	}
}

import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminService } from '../services/admin/admin.service';
import { UtilService } from '../services/util/util.service';


declare var statistiques1;
declare var statistiques2;
declare var dates;


@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})

export class AdminComponent implements OnInit {

  today: number = Date.now();

  pressAjouter: boolean;

  error: boolean;
  success: boolean;
  submit: boolean;
  message: string;

  nbrRdv: number;
  nbrVaccinations: number;
  nbrCentres: number;

  constructor(
    private router: Router,
    private utilService: UtilService,
    private adminService: AdminService
  ) { }

  ngOnInit(): void {
    if (localStorage.getItem('type') != "admin")
      this.router.navigateByUrl('/authentification');
    this.adminService.getCentres().subscribe(data => {
      this.nbrCentres = data.length;
    }
    );
    this.adminService.getVaccinations().subscribe(data => {
      this.nbrVaccinations = data.length;
    }
    );
    this.utilService.getRdvs().subscribe(data => {
      this.nbrRdv = data.length;
    }
    );
    this.setData();
    this.callNavbar();
    this.pressAjouter = false;
  }

  villes = [];
  semaine = [];
  rdvVilles = [];
  rdvSemain = [];
  vaccinationVilles = [];

  form = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.email,
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(4)
    ]),
    adresse: new FormControl('', [
      Validators.required,
    ]),
    ville: new FormControl('', [
      Validators.required,
    ]),
  });

  addCentre() {
    this.submit = true;
    setTimeout(() => {
      this.adminService.addCentre(this.form.value).subscribe(
        data => {
          this.message = "Centre ajouté avec succès";
          this.submit = false;
          this.error = false;
          this.success = true;
          this.pressAjouter = false;
          this.form.reset();
          this.setData();
        }, e => {
          try {
            this.message = e["error"]["message"];
            this.message = this.message.substring(7, this.message.length - 1);
          } catch (error) {
            this.message = e["error"];
          }
          this.success = false;
          this.error = true;
          this.submit = false;
          this.pressAjouter = true;
        });
    }, 1000);
  }

  annuler() {
    this.pressAjouter = false;
    this.setData();
  }

  setData() {
    this.utilService.getVilles(localStorage.getItem('token')).subscribe(data => {
      this.villes = data;
      this.adminService.getRdvSemaine().subscribe(data => {
        this.rdvSemain =[];
        this.semaine = dates();
        this.semaine.forEach(element => {
          this.rdvSemain.push(data[element]);
        });
        this.adminService.getRdvNbrs().subscribe(data => {
          this.rdvVilles =[];
          this.villes.forEach(element => {
            this.rdvVilles.push(data[element]);
          });
          this.adminService.getVaccinationNbrs().subscribe(data => {
            this.vaccinationVilles = [];
            this.villes.forEach(element => {
              this.vaccinationVilles.push(data[element]);
            });
            this.callJs();
          });
        });
      });
    });
  }

  callJs() {
    statistiques1(this.villes, this.rdvVilles, this.vaccinationVilles);
    statistiques2(this.semaine , this.rdvSemain);
  }

  callNavbar() {
    this.utilService.renderNavbar();
  }

}

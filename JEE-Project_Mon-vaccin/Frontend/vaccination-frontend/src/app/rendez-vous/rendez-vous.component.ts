import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { RendezVousService } from '../services/rendez-vous/rendez-vous.service';
import { UtilService } from '../services/util/util.service';

declare function callDatePicker(data: any);
declare function jourValue(): string;

@Component({
  selector: 'app-rendez-vous',
  templateUrl: './rendez-vous.component.html',
  styleUrls: ['./rendez-vous.component.css']
})
export class RendezVousComponent implements OnInit {

  cin: string;
  hasRdv: boolean;
  hasVacc: boolean;
  pressAjouter: boolean;
  pressUpdate: boolean;

  error: boolean;
  success: boolean;
  submit: boolean;
  message: string;

  villeChoisie: boolean;
  dateChoisie: boolean;

  villes = [];
  disabledDates = [];
  disponiblesHeures = [];

  ville: string;

  constructor(
    private router: Router,
    private utilService: UtilService,
    private authService: AuthService,
    private rendezVousService: RendezVousService
  ) { }

  ngOnInit(): void {
    if (localStorage.getItem('type') != "citoyen")
      this.router.navigateByUrl('/authentification');
    this.utilService.getVilles(localStorage.getItem('token')).subscribe(data => {
      this.villes = data;
    });
    this.setCitoyenParameters();
    this.villeChoisie = false;
    this.dateChoisie = false;
    this.callNavbar();
  }

  form = new FormGroup({
    nom: new FormControl('', [
      Validators.required,
    ]),
    prenom: new FormControl('', [
      Validators.required,
    ]),
    cin: new FormControl('', [
      Validators.required,
    ]),
    sexe: new FormControl('', [
      Validators.required,
    ]),
    age: new FormControl('', [
      Validators.required,
      Validators.pattern("^[0-9]*$"),
      Validators.minLength(2),
      Validators.maxLength(3),
    ]),
    tel: new FormControl('', [
      Validators.required,
      Validators.pattern("^[0-9]*$"),
      Validators.minLength(10),
      Validators.maxLength(10),
    ]),
    ville: new FormControl('', [
      Validators.required,
    ]),
    date: new FormControl('', [

    ]),
    jour: new FormControl('', [

    ]),
    heure: new FormControl('', [
      Validators.required,
    ]),
  });

  form2 = new FormGroup({
    ville: new FormControl('', [
      Validators.required,
    ]),
    date: new FormControl('', [

    ]),
    jour: new FormControl('', [

    ]),
    heure: new FormControl('', [
      Validators.required,
    ]),
  });

  ajouterRdv() {
    if (this.form.invalid) {
      this.error = true;
      this.message = "informations incorrectes";
      return;
    }
    this.combineDate(this.form);
    this.submit = true;
    setTimeout(() => {
      this.rendezVousService.addRdv(this.form.value).subscribe(
        s => {
          this.message = "Rendez-vous enregistré avec succès";
          this.submit = false;
          this.error = false;
          this.success = true;
          this.setCitoyenParameters()
          this.pressAjouter = false;
          this.form.reset();
          this.dateChoisie = false;
          this.villeChoisie = false;
        },
        e => {
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
        }
      );
    }, 1000);
  }

  updateRdv() {
    if (this.form2.invalid) {
      this.error = true;
      this.message = "informations incorrectes";
      return;
    }
    this.submit = true;
    this.combineDate(this.form2);
    setTimeout(() => {
      this.rendezVousService.updateRdv(this.form2.value).subscribe(
        s => {
          this.message = "Rendez-vous modifié avec succès";
          this.submit = false;
          this.error = false;
          this.success = true;
          this.pressUpdate = false;
          this.form2.reset();
          this.dateChoisie = false;
        },
        e => {
          try {
            this.message = e["error"]["message"];
            this.message = this.message.substring(7, this.message.length - 1);
          } catch (error) {
            this.message = e["error"];
          }
          this.success = false;
          this.error = true;
          this.submit = false;
          this.pressUpdate = true;
        }
      );
    }, 1000);
  }

  deleteRdv() {
    this.submit = true;
    setTimeout(() => {
      this.rendezVousService.deleteRdv(this.cin).subscribe(
        data => {
          this.message = "Rendez-vous supprimé avec succès";
          this.submit = false;
          this.error = false;
          this.success = true;
          this.setCitoyenParameters();
        }
      );
    }, 1000);
  }

  setCitoyenParameters() {
    this.authService.authenticatedUser(localStorage.getItem('token')).subscribe(data => {
      this.cin = data['cin'];
      if (this.cin != null) {
        this.rendezVousService.hasRdv(this.cin).subscribe(data => {
          this.hasRdv = data['hasRdv'];
        });
        this.rendezVousService.hasVaccination(this.cin).subscribe(data => {
          this.hasVacc = data['vaccination'];
        });
      } else {
        this.hasRdv = false;
        this.hasVacc = false;
      }
    });
  }

  setDisabledDates(form: FormGroup) {
    this.rendezVousService.getDisabledDates(form.value["ville"]).subscribe(
      data => {
        this.disabledDates = data;
        this.villeChoisie = true;
        this.ville = form.value["ville"];
      }
    );
  }

  setDisponiblesHeures(form: FormGroup) {
    var dateJour = jourValue();
    setTimeout(function () {
      dateJour = jourValue();
      if (dateJour.trim() != "") {
        this.rendezVousService.getDisponiblesHeures(form.value["ville"], dateJour).subscribe(
          data => {
            this.dateChoisie = true;
            this.disponiblesHeures = data;
            this.combineDate(form);
          }
        );
      }
    }.bind(this), 300);
  }

  combineDate(form: FormGroup) {
    form.value["jour"] = jourValue();
    form.value['date'] = jourValue() + "_" + form.value['heure'];
  }

  callJs(data: any) {
    callDatePicker(data);
  }

  callNavbar() {
    this.utilService.renderNavbar();
  }
}

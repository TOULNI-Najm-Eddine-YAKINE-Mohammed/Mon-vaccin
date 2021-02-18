import { Component, OnInit } from '@angular/core';
import { UtilService } from '../services/util/util.service';
import { VaccinationsService } from '../services/vaccinations/vaccinations.service';
import { DatePipe } from '@angular/common';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-vaccinations',
  templateUrl: './vaccinations.component.html',
  styleUrls: ['./vaccinations.component.css']
})
export class VaccinationsComponent implements OnInit {

  p: number = 1;
  rdvs = [];
  rdvsToday: boolean;

  constructor(
    private vaccinationsService: VaccinationsService,
    private utilService: UtilService
  ) { }

  ngOnInit(): void {
    this.rdvsToday = false;
    this.callNavbar();
    this.setRdvs();
  }

  form = new FormGroup({
    cin: new FormControl('', [
      Validators.required,
    ]),
  });

  setRdvs() {
    this.utilService.getRdvs().subscribe(
      data => {
        this.rdvs = data;
      },
      error => {
        this.rdvs = [];
      }
    )
  }

  addVaccination(cin: String) {
    var cinKV = { 'cin': cin };
    this.vaccinationsService.addVaccination(cinKV).subscribe(
      data => {
        if (this.rdvsToday)
          this.getRdvsToday();
        else
          this.setRdvs();
      },
      error => {
        this.rdvs = [];
        this.rdvsToday = false;
      });
  }

  getRdvsToday() {
    const datepipe: DatePipe = new DatePipe('en-US');
    let today = datepipe.transform(new Date(), 'dd-MM-yyyy');
    this.vaccinationsService.getByDate(today).subscribe(
      data => {
        this.rdvs = data;
      }, error => {
        this.rdvs = [];
      });
  }

  getRdvByCin() {
    this.vaccinationsService.getByCin(this.form.value["cin"]).subscribe(
      data => {
        var array = [data];
        if (data == null)
          this.rdvs = data;
        else
          this.rdvs = array;
      }, error => {
        this.rdvs = [];
      }
    );
    this.form.reset();
  }

  callNavbar() {
    this.utilService.renderNavbar();
  }

}

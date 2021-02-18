import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { UtilService } from '../services/util/util.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {

  error: boolean;
  success: boolean;
  submit: boolean;
  message: string;

  constructor(
    private authService: AuthService,
    private router: Router,
    private utilService: UtilService
  ) { }

  ngOnInit() {
    this.authService.logout();
    this.error = false;
    this.success = false;
    this.message = "";
    this.callNavbar();
  }

  form = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.email,
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(4)
    ]),
  });

  validator(): boolean {
    if (this.form.controls['email'].invalid) {
      this.error = true;
      this.message = "Cet email est invalid";
      return true;
    }

    if (this.form.controls['password'].invalid) {
      this.error = true;
      this.message = "le mot de passe doit contenir 4 caractères";
      return true;
    }
  }

  register() {
    if (this.validator())
      return;
    this.submit = true;
    setTimeout(() => {
      this.authService.register(this.form.value).subscribe(
        s => {
          this.message = s;
          this.submit = false;
          this.error = false;
          this.success = true;
        },
        e => {
          this.message = e["error"];
          this.success = false;
          this.error = true;
          this.submit = false;
        }
      );
    }, 1000);
  }

  login() {
    if (this.validator())
      return;
    this.submit = true;
    setTimeout(() => {
      this.authService.login(this.form.value).subscribe(
        s => {
          this.error = false;
          this.success = false;
          this.submit = false;
          setTimeout(() => {
            if (localStorage.getItem('type') === "citoyen")
              this.router.navigateByUrl("/rendez-vous");
            else if (localStorage.getItem('type') === "centre")
              this.router.navigateByUrl("/vaccinations");
            else if (localStorage.getItem('type') === "admin")
              this.router.navigateByUrl("/admin");
          }, 1000);
        },
        e => {
          this.message = e["error"];
          this.success = false;
          this.error = true;
          this.submit = false;
        }
      );
    }, 1000);
  }

  callNavbar() {
    this.utilService.renderNavbar();
  }
}

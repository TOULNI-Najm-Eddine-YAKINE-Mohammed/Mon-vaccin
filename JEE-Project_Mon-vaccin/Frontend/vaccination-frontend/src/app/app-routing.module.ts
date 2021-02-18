import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminComponent } from './admin/admin.component';
import { AuthComponent } from './auth/auth.component';
import { RendezVousComponent } from './rendez-vous/rendez-vous.component';
import { VaccinationsComponent } from './vaccinations/vaccinations.component';


const routes: Routes = [
  {path: '', redirectTo: 'authentification', pathMatch: 'full' },
  {path: 'authentification', component: AuthComponent},
  {path: 'rendez-vous', component: RendezVousComponent},
  {path: 'vaccinations', component: VaccinationsComponent},
  {path: 'admin', component: AdminComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

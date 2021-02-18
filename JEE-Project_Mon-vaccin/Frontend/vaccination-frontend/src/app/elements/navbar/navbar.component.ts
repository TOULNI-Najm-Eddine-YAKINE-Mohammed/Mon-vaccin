import { Component, OnInit } from '@angular/core';
import { UtilService } from 'src/app/services/util/util.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  type : boolean;

  constructor(private eventEmitterService: UtilService) { }

  ngOnInit(): void {
    if (localStorage.getItem('type') != null)
      this.type = true;
    else {
      this.type = false;
    }

    if (this.eventEmitterService.subsVar==undefined) {
      this.eventEmitterService.subsVar = this.eventEmitterService.
      invokeNavbarInit.subscribe((name:string) => {
        this.ngOnInit();
      });
    }
  }



}

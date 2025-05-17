import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'home-page',
  imports: [],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss'
})
export class HomePageComponent {
  public constructor(public router : Router) {

  }

  public loadSignInPage() {
    this.router.navigate(['/sign-in']);
  }
}

import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private jwtToken: string = "";

  constructor(private router: Router) { }

  public setToken(token: string) {
    this.jwtToken = token;

    // Save the token in local storage
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.setItem("jwtToken", token);
    }
  }

  public getToken(): string {
    if (!this.jwtToken && typeof window !== 'undefined' && window.localStorage) {
      this.jwtToken = localStorage.getItem("jwtToken") || "";
    }

    return this.jwtToken;
  }

  public isAuthenticated(): boolean {
    return !!this.getToken();
  }

  public logout(): void {
    console.log("Logging user out.");

    this.jwtToken = '';
    localStorage.removeItem('jwtToken');

    this.router.navigate(['/sign-in']);
  }
}

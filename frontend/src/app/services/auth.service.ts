import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private jwtToken: string = "";

  constructor() { }

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
    this.jwtToken = '';
    localStorage.removeItem('jwtToken');
  }
}

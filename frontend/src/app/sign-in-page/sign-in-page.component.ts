import { Component, OnInit } from '@angular/core';
import { User, UserService } from '../services/user.service';
import { CppFile } from '../services/cpp-file.service';
import { CommonModule } from '@angular/common';
import { catchError, map, Observable, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-sign-in-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sign-in-page.component.html',
  styleUrl: './sign-in-page.component.scss',
  providers: [UserService, AuthService]
})
export class SignInPageComponent {
  public constructor(public userService: UserService, public authService: AuthService, public router: Router) { }

  usernameText: string = "";
  passwordText: string = "";

  public logIn() {
    console.log("Logging " + this.usernameText + " in...");

    // Create a user to send in the request body
    let newUser: User = new User(-1, this.usernameText, this.passwordText, []);

    // Tells the database to find the user
    this.userService.logInUser(newUser).subscribe({
      next: (requestBody: any) => {
        console.log(requestBody);
        // Set the JWT Token
        console.log("JWT Token being added: " + requestBody.token);
        this.authService.setToken(requestBody.token);
        // Navigate to the user page
        this.router.navigate(['/user/' + requestBody.user.id]);
      },
      error: (error) => {
        // TODO implement user not found error page in CSS / HTML
      }
    });
  }

  public signIn() {
    console.log("Signing " + this.usernameText + " in...");

    // Create a user to send in the request body
    let newUser: User = new User(-1, this.usernameText, this.passwordText, []);

    // Tells the database to add the user
    this.userService.addUser(newUser).subscribe({
      next: (requestBody: any) => {
        // Set the JWT Token
        this.authService.setToken(requestBody.token);
        // Navigate to the user page
        this.router.navigate(['/user/' + requestBody.user.id]);
      },
      error: (error) => {
        alert(error.message);
      }
    });
  }

  // ngOnInit() {
  //   console.log("Atttempting to fetch user from backend...");
  //   let newUser: User = new User(-1, "john stocking", "password",  []);
  //   let postUser: Observable<User> = this.userService.addUser(newUser);
  //   postUser.pipe(
  //     map((user: User) => {
  //       return user;
  //     }),
  //     catchError((error: any) => {
  //       console.log(error.message);
  //       alert(error.message || JSON.stringify(error));
  //       return throwError(() => error);
  //     })
  //   ).subscribe({
  //     next: (user: User) => {
  //       // Additional logic with the user if needed
  //     },
  //     error: (err) => {
  //       // Error already handled in catchError
  //       console.log('Error in subscribe:', err);
  //     }
  //   });

  //   console.log("Data fetch ended.");
  // }
}

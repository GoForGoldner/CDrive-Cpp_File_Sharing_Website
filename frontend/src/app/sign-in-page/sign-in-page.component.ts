import { Component, ElementRef, ViewChild } from '@angular/core';
import { User, UserService } from '../services/user.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-sign-in-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './sign-in-page.component.html',
  styleUrl: './sign-in-page.component.scss',
  providers: [UserService, AuthService]
})
export class SignInPageComponent {
  @ViewChild("usernameTakenError") usernameTakenErrorModal!: ElementRef<HTMLDialogElement>;
  @ViewChild("usernameShortError") usernameShortErrorModal!: ElementRef<HTMLDialogElement>;
  @ViewChild("passwordShortError") passwordShortErrorModel!: ElementRef<HTMLDialogElement>;
  @ViewChild("loginError") loginErrorModel!: ElementRef<HTMLDialogElement>;

  public loginForm: FormGroup;

  public constructor(public userService: UserService,
    public authService: AuthService,
    public router: Router,
    public formBuilder: FormBuilder) {

    // Creates the Form for the sign-in page
    this.loginForm = this.formBuilder.group({
      username: ['', [
        Validators.required,
        Validators.minLength(8)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8)
      ]]
    });
  }

  private addDialogListeners(): void {
    this.usernameTakenErrorModal.nativeElement.addEventListener("click", () => this.usernameTakenErrorModal.nativeElement.close());
    this.usernameShortErrorModal.nativeElement.addEventListener("click", () => this.usernameShortErrorModal.nativeElement.close());
    this.passwordShortErrorModel.nativeElement.addEventListener("click", () => this.passwordShortErrorModel.nativeElement.close());
    this.loginErrorModel.nativeElement.addEventListener("click", () => this.loginErrorModel.nativeElement.close());
  }

  public logIn() {
    const { username, password } = this.loginForm.value;

    console.log("Logging " + username + " with password " + password + " in...");

    this.addDialogListeners();

    // Create a user to send in the request body
    let newUser: User = new User(-1, username, password, []);

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
        if (error.status == 409) {
          console.log("Couldn't find user, showing error message.");
          this.loginErrorModel.nativeElement.showModal();
        } else {
          this.authService.logout();
          this.logIn();
        }
      }
    });
  }

  public signIn() {
    const { username, password } = this.loginForm.value;

    console.log("Signing " + username + " in...");

    this.addDialogListeners();

    // If the username or password is invalid
    if (this.loginForm.get('username')?.invalid) {
      console.log("Username invalid. Showing username error page.");
      this.usernameShortErrorModal.nativeElement.showModal();
      return;
    } else if (this.loginForm.get('password')?.invalid) {
      console.log("Password invalid. Showing password error page.");
      this.passwordShortErrorModel.nativeElement.showModal();
      return;
    }

    // Create a user to send in the request body
    let newUser: User = new User(-1, username, password, []);

    // Tells the database to add the user
    this.userService.addUser(newUser).subscribe({
      next: (requestBody: any) => {
        // Set the JWT Token
        this.authService.setToken(requestBody.token);
        // Navigate to the user page
        this.router.navigate(['/user/' + requestBody.user.id]);
      },
      error: (error) => {
        this.usernameTakenErrorModal.nativeElement.showModal();
      }
    });
  }
}

import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'user-icon',
  imports: [],
  templateUrl: './user-icon.component.html',
  standalone: true,
  styleUrl: './user-icon.component.scss'
})
export class UserIconComponent {
  public constructor(public authService: AuthService) {}
}

import {UserResponse} from './user-response';

export interface LoginResponse {
  user: UserResponse;
  token: string;
}

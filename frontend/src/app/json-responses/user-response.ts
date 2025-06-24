import {CppFileResponse} from './cpp-file-response';

export interface UserResponse {
  id: number;
  username: string;
  password: string;
  cpp_files: CppFileResponse[];
}

import {CodeEntryResponse} from './code-entry-response';

export interface CppFileResponse {
  filename: string;
  source_code: CodeEntryResponse[];
  id: number;
  user_id: number;
}

import {computed, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {finalize, Observable, tap} from 'rxjs';
import {environment} from '../../environments/environment';
import {CppFileResponse} from '../json-responses/cpp-file-response';
import {CodeEntryResponse} from '../json-responses/code-entry-response';

@Injectable({
  providedIn: 'root'
})
export class CppFileService {
  // C++ file properties
  public code = signal<CodeEntryResponse[]>([]);
  // Code sorted by the date with most recent being index 0
  public sortedCode = computed(() => {
    let sortedCodeEntries = this.code().slice();
    sortedCodeEntries.sort((a, b) => {
      return new Date(b.date).getTime() - new Date(a.date).getTime();
    });

    return sortedCodeEntries;
  });
  public filename = signal('');
  public readonly userId = signal(-1);
  readonly fileId = signal(-1);
  public readonly currentFile = computed(() => {
    if (!this.fileId()) return;

    return {
      filename: this.filename(),
      source_code: this.code(),
      id: this.fileId(),
      user_id: this.userId()
    } as CppFileResponse;
  });
  // Service related properties
  public readonly containsFile = signal(false);
  public readonly loading = signal(false);
  public readonly loadingMessage = signal('');
  private urlStart: string = environment.API_BASE_URL + "/api/cpp-files/";

  constructor(private http: HttpClient) {
  }

  /**
   * Loads a C++ file from the database.
   * @param cppFileId - The id of the C++ file in the database
   */
  loadCppFile(cppFileId: number): Observable<CppFileResponse> {
    let apiURL = this.urlStart + cppFileId;

    this.loading.set(true);
    this.loadingMessage.set("Getting File...");

    return this.http.get<CppFileResponse>(apiURL).pipe(
      tap(response => {
        this.containsFile.set(true);
        this.setFileData(response);
      }),
      finalize(() => {
        this.loading.set(false);
        this.loadingMessage.set("File Retrieved");
      })
    );
  }

  /**
   * Saves a C++ file into the database.
   */
  saveCppFile(): Observable<CppFileResponse> {
    let apiURL = this.urlStart + this.fileId();

    this.loading.set(true);
    this.loadingMessage.set("Saving File...");

    return this.http.patch<CppFileResponse>(apiURL, this.currentFile()).pipe(
      tap(response => this.setFileData(response)),
      finalize(() => {
        this.loading.set(false);
        this.loadingMessage.set("File Saved");
      })
    );
  }

  /**
   * Adds a new entry to the version history of the code.
   * @param newCode - A string representing the new code version
   */
  addCodeVersion(newCode: string): void {
    const newEntry: CodeEntryResponse = {
      code: newCode,
      date: new Date().toISOString().split('.')[0]
    }

    console.log("added: ");
    console.log(newEntry);

    this.code.update(currentCode => [...currentCode, newEntry]);
  }

  private setFileData(cppFileResponse: CppFileResponse) {
    this.code.set(cppFileResponse.source_code);
    this.filename.set(cppFileResponse.filename);
    this.userId.set(cppFileResponse.user_id);
    this.fileId.set(cppFileResponse.id);
  }
}

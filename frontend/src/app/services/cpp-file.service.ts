import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export class CppFile {
  constructor(public filename: string, public source_code: string, public id?: number) {
    this.id = id ?? -1;
  }
}

@Injectable({
  providedIn: 'root'
})
export class CppFileService {
  constructor(private http : HttpClient) { }

  private urlStart: string = environment.API_BASE_URL + "/api/cpp-files/";

  getCppFile(cppFileId: number): Observable<CppFile> {
    let apiURL = this.urlStart + cppFileId;
    
    return this.http.get<any>(apiURL).pipe(
      map((response: any) => {
        return new CppFile(response.filename, response.source_code, response.id);
      })
    );
  }

  saveCppFile(cppFile: CppFile): Observable<CppFile> {
    let apiURL = this.urlStart + cppFile.id;

    return this.http.patch<any>(apiURL, cppFile).pipe(
      map((response: CppFile) => {
        console.log("FILE THAT WAS RETURNED AFTER BEING SAVED: ");
        console.log(response);
        return response;
      })
    );
  }
}

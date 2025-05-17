import { Component, Injectable } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MonacoEditorModule } from 'ngx-monaco-editor-v2';
import { CppFile, CppFileService } from '../services/cpp-file.service';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
declare const monaco: any;

@Component({
  selector: 'code-editor',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MonacoEditorModule
  ],
  templateUrl: './code-editor.component.html',
  styleUrl: './code-editor.component.scss'
})
export class CodeEditorComponent {

  private userId: number;
  private fileId: number;

  public constructor(private cppFileService : CppFileService, private activatedRoute: ActivatedRoute) {
    this.userId = Number(activatedRoute.snapshot.params["userId"]);
    this.fileId = Number(activatedRoute.snapshot.params["fileId"]);
  }

  public editorOptions = { theme: 'vs-dark', language: 'cpp' };
  public code!: string;
  private cppFile!: CppFile;

  ngOnInit() {
    console.log("Atttempting to fetch data from backend...");

    let getCppFile : Observable<CppFile> = this.cppFileService.getCppFile(this.fileId);

    getCppFile.subscribe(
      (result : CppFile) => {
        console.log("The filename is: " + result.filename + " " + result.id);
        this.code = result.source_code;
        this.cppFile = result;
      }
    );

    console.log("ngoninit finished.")
  }

  saveButtonClick() {
    this.cppFile.source_code = this.code;
    
    console.log("File being sent: ");
    console.log(this.cppFile);

    this.cppFileService.saveCppFile(this.cppFile).subscribe({
      next: (file) => {
        console.log(file.filename + " was successfully saved!");
      }
    });
  }

  //when the run button is clicked, 
  // save the new code to the database
  // tell the backend to execute the code, 
  // and mb show any errors that the code may have
}


import { Component, ElementRef, ViewChild, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MonacoEditorModule } from 'ngx-monaco-editor-v2';
import { CppFile, CppFileService } from '../services/cpp-file.service';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { WebSocketService } from '../services/web-socket.service';
import { ChangeDetectorRef } from '@angular/core';
import { UserIconComponent } from "../user-icon/user-icon.component";
declare const monaco: any;

@Component({
  selector: 'code-editor',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MonacoEditorModule,
    UserIconComponent
  ],
  templateUrl: './code-editor.component.html',
  styleUrl: './code-editor.component.scss'
})
export class CodeEditorComponent implements OnInit, OnDestroy{

  @ViewChild("terminal") terminalRef!: ElementRef<HTMLDialogElement>;

  public terminalInput: string = "";
  private userId: number;
  private fileId: number;

  public editorOptions = { theme: 'vs-dark', language: 'cpp', fontSize: 20  };
  public code!: string;
  private cppFile!: CppFile;

  public constructor(private cppFileService: CppFileService,
    public webSocketService: WebSocketService,
    private router: Router,
    private cd: ChangeDetectorRef,
    private activatedRoute: ActivatedRoute) {
    this.userId = Number(activatedRoute.snapshot.params["userId"]);
    this.fileId = Number(activatedRoute.snapshot.params["fileId"]);
  }

  ngOnInit(): void {
    console.log("Atttempting to fetch data from backend...");

    let getCppFile: Observable<CppFile> = this.cppFileService.getCppFile(this.fileId);

    getCppFile.subscribe(
      (result: CppFile) => {
        console.log("The filename is: " + result.filename + " " + result.id);
        this.code = result.source_code;
        this.cppFile = result;
      }
    );

    console.log("ngoninit finished.")
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }

  public runButtonClick() {
    console.log("Run button clicked!");

    this.cppFile.source_code = this.code;

    console.log("File being sent: ");
    console.log(this.cppFile);

    this.cppFileService.saveCppFile(this.cppFile).subscribe({
      next: (file) => {
        this.webSocketService.output = "";
        this.webSocketService.executeFile(this.fileId);

        this.terminalRef.nativeElement.showModal();
        console.log(this.terminalRef.nativeElement);
      }
    });
    //this.webSocketService.disconnect();
  }

  public inputEntered() {
    this.webSocketService.sendInputMessage(this.terminalInput);
    this.terminalInput = "";
    this.cd.detectChanges();
  }

  public returnToUserPage() {
    this.router.navigate(["/user/", this.userId]);
  }
}


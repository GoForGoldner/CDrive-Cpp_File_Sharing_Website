import {ChangeDetectorRef, Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MonacoEditorModule} from 'ngx-monaco-editor-v2';
import {CppFileService} from '../services/cpp-file.service';
import {ActivatedRoute, Router} from '@angular/router';
import {WebSocketService} from '../services/web-socket.service';
import {UserIconComponent} from "../user-icon/user-icon.component";
import {RxStompState} from '@stomp/rx-stomp';

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
export class CodeEditorComponent implements OnInit {

  @ViewChild("terminal") terminalRef!: ElementRef<HTMLDialogElement>;
  public terminalInput = "";
  public ideCode = "";
  public editorOptions = {theme: 'vs-dark', language: 'cpp', fontSize: 20};
  protected readonly RxStompState = RxStompState;
  private cppFileService = inject(CppFileService);
  public readonly userId = this.cppFileService.userId;
  public readonly fileId = this.cppFileService.fileId;
  public filename = this.cppFileService.filename;
  public code = this.cppFileService.sortedCode;
  public loading = this.cppFileService.loading;
  public loadingMessage = this.cppFileService.loadingMessage;
  public cppFile = this.cppFileService.currentFile;

  constructor(
    public webSocketService: WebSocketService,
    private router: Router,
    private cd: ChangeDetectorRef,
    private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    console.log("Attempting to fetch data from backend...");
    const tempFileId = Number(this.activatedRoute.snapshot.params["fileId"]);

    this.cppFileService.loadCppFile(tempFileId).subscribe(response => {
      // Put the latest version code into the ide
      this.ideCode = this.code()?.at(0)?.code ?? "";
    });
  }

  public runButtonClick() {
    console.log("Run button clicked!");

    this.cppFileService.addCodeVersion(this.ideCode);

    console.log("File being sent: ");
    console.log(this.cppFile());

    this.cppFileService.saveCppFile().subscribe({
      next: (file) => {
        console.log("✅ File Saved!");

        // Clear the previous terminal window
        this.webSocketService.output = "";
        this.webSocketService.executeFile(this.fileId());

        // Show the terminal window
        this.terminalRef.nativeElement.showModal();
      }
    });
  }

  public inputEntered() {
    this.webSocketService.sendInputMessage(this.terminalInput);
    this.terminalInput = "";
    this.cd.detectChanges();
  }

  public returnToUserPage() {
    this.router.navigate(["/user/", this.userId()]);
  }
}


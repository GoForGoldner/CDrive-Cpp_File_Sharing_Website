import {ChangeDetectorRef, Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {CppFileBody, UserService} from '../services/user.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {RandomGradientDirective} from '../directives/random-gradient.directive';
import {FormsModule} from '@angular/forms';
import {UserIconComponent} from '../user-icon/user-icon.component';
import {AuthService} from '../services/auth.service';
import {CppFileResponse} from '../json-responses/cpp-file-response';
import {EditorComponent} from 'ngx-monaco-editor-v2';


@Component({
  selector: 'app-user-page',
  imports: [CommonModule, RandomGradientDirective, FormsModule, UserIconComponent, EditorComponent],
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.scss',
  standalone: true
})
export class UserPageComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  public searchStr: string = "";

  public editorOptions = {
    theme: 'vs-dark',
    language: 'cpp',
    fontSize: 8,
    readOnly: true,
    lineNumbers: 'on',
    minimap: {enabled: false},
    scrollBeyondLastLine: false,
    wordWrap: 'on',
    contextmenu: false,
    links: false,
    renderLineHighlight: 'none',
    scrollbar: {
      vertical: 'hidden',
      horizontal: 'hidden',
      handleMouseWheel: false,
      alwaysConsumeMouseWheel: false
    },
    smoothScrolling: false,
    overviewRulerLanes: 0,
    hideCursorInOverviewRuler: true
  };

  private userService = inject(UserService);
  public userId = this.userService.id;
  public username = this.userService.username;
  public password = this.userService.password;
  public cppFiles = this.userService.cppFiles;

  constructor(public authService: AuthService, private activatedRoute: ActivatedRoute,
              private router: Router, private changeDetector: ChangeDetectorRef
  ) {
  }

  get filteredCppFiles(): CppFileResponse[] {
    let filteredFiles: CppFileResponse[] = this.cppFiles()
      ?.slice()  // Create copy
      .reverse() // Reverse the copy
      // Filters the files by the search string
      .filter(file => file.filename.startsWith(this.searchStr)) || [];

    // Sort each code version by the date
    filteredFiles.forEach(cppFile => {
      cppFile.source_code.sort((a, b) => {
        return new Date(b.date).getTime() - new Date(a.date).getTime();
      });
    });

    return filteredFiles;
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const tempUserId = Number(params["userId"]);
      this.userService.getUser(tempUserId);
    });
  }

  public deleteCppFile(cppFile: CppFileResponse) {
    this.userService.removeCppFileFromUser(cppFile.id);
  }

  public fileButtonClick() {
    console.log("Files button clicked!");
    this.fileInput.nativeElement.click();
  }

  public cppFileClick(cppFile: CppFileResponse) {
    this.router.navigate(['/user/' + this.userId() + '/files/' + cppFile.id]);
  }

  public onDragEnter(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    (event.currentTarget as HTMLElement).classList.add('drag-over');
    console.log("added drag-over");
  }

  public async onDragDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    (event.currentTarget as HTMLElement).classList.remove('drag-over');
    console.log("removed drag-over");

    // Create a fake event to send to the input form
    const fakeEvent = {
      target: {
        files: event.dataTransfer?.files
      }
    } as unknown as Event;

    // Call your existing method
    await this.fileChange(fakeEvent);
  }

  public onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    (event.currentTarget as HTMLElement).classList.remove('drag-over');
    console.log("removed drag-over");
  }

  async fileChange(event: Event) {
    console.log("Looking at files...");

    const input = event.target as HTMLInputElement;
    const files = input.files;

    // No files imported
    if (!files || files.length == 0) return;

    // Turn files into an array and filter out all non '.cpp' files
    const cppFiles = Array.from(files).filter(file => file.name.endsWith('.cpp'));

    console.log("Files button converting file imports to CppFile objects...");
    let cppArray: CppFileBody[] = await this.processImportedCppFiles(cppFiles);

    console.log("Adding CppFiles to user...");
    this.userService.addCppFilesToUser(cppArray);
  }

  private async processImportedCppFiles(files: File[]): Promise<CppFileBody[]> {
    let cppFiles: CppFileBody[] = [];

    for (const file of files) {
      const code = await this.readFileContent(file);

      cppFiles.push({
        filename: file.name,
        code: code
      } as CppFileBody);
    }

    return cppFiles;
  }

  private readFileContent(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (event) => {
        resolve(event.target?.result as string);
      };

      reader.onerror = (error) => {
        reject(error);
      };

      reader.readAsText(file);
    });
  }
}

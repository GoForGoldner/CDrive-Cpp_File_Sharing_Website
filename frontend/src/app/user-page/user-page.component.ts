import { Component, ElementRef, ViewChild, OnInit, ChangeDetectorRef } from '@angular/core';
import { CppFile } from '../services/cpp-file.service';
import { User, UserService } from '../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { RandomGradientDirective } from '../directives/random-gradient.directive';
import { FormsModule } from '@angular/forms';
import { UserIconComponent } from '../user-icon/user-icon.component';
import { AuthService } from '../services/auth.service';


@Component({
  selector: 'app-user-page',
  imports: [CommonModule, RandomGradientDirective, FormsModule, UserIconComponent],
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.scss'
})
export class UserPageComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  public searchStr: string = "";

  constructor(public authService: AuthService, private userService: UserService, private activatedRoute: ActivatedRoute,
    private router: Router, private changeDetector: ChangeDetectorRef
  ) {
  }

  private userId: number = 0;
  public currentUser: User = new User(-1, "", "", []);

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.userId = Number(params["userId"]);
      this.loadUserData();
    });
  }

  public get filteredCppFiles() {
    return this.currentUser?.cppFiles
      ?.slice()  // Create copy
      .reverse() // Reverse the copy
      .filter(file => file.filename.startsWith(this.searchStr)) || [];
  }

  public loadUserData() {
    this.userService.getUser(this.userId).subscribe({
      next: (response) => {
        this.currentUser = response;
        this.changeDetector.markForCheck();
      },
      error: (err) => {
        throw new Error(err);
      }
    });
  }

  public deleteCppFile(cppFile: CppFile) {
    this.userService.removeCppFileFromUser(this.currentUser.userId, cppFile.id ?? -1).subscribe({
      next: (response) => {
        console.log("Updating Cpp files after deleting one!");
        this.currentUser.cppFiles = response.cppFiles;
      },
      error: (err) => {
        throw new Error(err);
      }
    });
  }

  public fileButtonClick() {
    console.log("Files button clicked!");
    this.fileInput.nativeElement.click();
  }

  public cppFileClick(cppFile: CppFile) {
    this.router.navigate(['/user/' + this.userId + '/files/' + cppFile.id]);
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
    let cppArray: CppFile[] = await this.processImportedCppFiles(cppFiles);

    console.log("Adding CppFiles to user...");

    // A list of the async addFiles that need to be called
    const requests: Promise<User>[] = cppArray.map(cppFile =>
      firstValueFrom(this.userService.addCppFileToUser(this.userId, cppFile))
    );

    try {
      // Wait for all the requests to complete
      const results = await Promise.all(requests);

      if (results.length > 0) {
        // Get the last user given from the database
        this.currentUser = results[results.length - 1];
        this.changeDetector.detectChanges();
      }
    } catch (error) {
      console.error('Error adding files:', error);
    } finally {
      input.value = '';
    }
  }

  private async processImportedCppFiles(files: File[]): Promise<CppFile[]> {
    let cppFiles: CppFile[] = [];

    for (const file of files) {
      const code = await this.readFileContent(file);

      console.log("FileCode: " + code);

      cppFiles.push(new CppFile(file.name, code));
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
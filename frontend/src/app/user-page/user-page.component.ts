import { Component, ElementRef, ViewChild, OnInit, inject, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CppFile } from '../services/cpp-file.service';
import { User, UserService } from '../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-user-page',
  imports: [CommonModule],
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPageComponent implements OnInit {
  constructor(private userService: UserService, private activatedRoute: ActivatedRoute,
    private router: Router, private changeDetector: ChangeDetectorRef
  ) {
    this.activatedRoute = inject(ActivatedRoute);
  }

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  private userId: number = 0;
  public currentUser!: User;

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.userId = Number(params["userId"]);
      this.loadUserData();
    });
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

  public fileButtonClick() {
    console.log("Files button clicked!");
    this.fileInput.nativeElement.click();
  }

  public cppFileClick(cppFile: CppFile) {
    this.router.navigate(['/user/' + this.userId + '/files/' + cppFile.id]);
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

        // Run change detection after it's updated
        this.changeDetector.markForCheck();
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
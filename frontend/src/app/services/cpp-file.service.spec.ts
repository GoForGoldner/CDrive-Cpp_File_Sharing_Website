import { TestBed } from '@angular/core/testing';

import { CppFileService } from './cpp-file.service';

describe('CppFileService', () => {
  let service: CppFileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CppFileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

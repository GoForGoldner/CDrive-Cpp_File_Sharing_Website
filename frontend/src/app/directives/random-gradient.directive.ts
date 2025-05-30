import { AfterViewInit, Directive, ElementRef, input } from '@angular/core';

@Directive({
  selector: '[randomGradient]',
  standalone: true
})
export class RandomGradientDirective implements AfterViewInit {
  gradientKey = input.required<string>();

  constructor(private elementRef: ElementRef) { }

  private palette = [
    '255,153,0',
    '246,255,0',
    '0,255,234',
    '0,123,255',
    '93,0,255',
    '187,0,255',
    '255,0,144'
  ];

  ngAfterViewInit() {
    // If the colors are already set return
    if (this.elementRef.nativeElement.dataset.gradientSet) return;

    // Attempt to get the colors from the storage
    const gradientKeyValue = this.gradientKey();
    const storageKey = `gradient-${gradientKeyValue}`;
    let colors = JSON.parse(localStorage.getItem(storageKey) || 'null');

    // If colors isn't already in the storage
    if (!colors) {
      // Get 4 random colors and add them to the local storage
      colors = this.palette.sort(() => 0.5 - Math.random()).slice(0, 4);
      localStorage.setItem(storageKey, JSON.stringify(colors));
    }

    // Set the CSS attribute for the color
    colors.forEach((color: string, i: number) =>
      this.elementRef.nativeElement.style.setProperty(`--gradient-color-${i + 1}`, color)
    );

    // Mark that the colors have been set
    this.elementRef.nativeElement.dataset.gradientSet = 'true';
  }
}
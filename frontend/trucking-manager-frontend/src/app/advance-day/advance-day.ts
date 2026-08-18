import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Api } from '../api';

@Component({
  selector: 'app-advance-day',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './advance-day.html',
  styleUrl: './advance-day.css'
})
export class AdvanceDay {
  private readonly api = inject(Api);

  advance(): void {
    this.api.advanceDay().subscribe();
  }
}

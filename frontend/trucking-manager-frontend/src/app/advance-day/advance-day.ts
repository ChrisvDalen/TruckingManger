import { Component } from '@angular/core';
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
  constructor(private api: Api) {}
  advance() {
    this.api.advanceDay().subscribe();
  }
}

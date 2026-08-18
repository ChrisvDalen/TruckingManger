import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Api, CompanyStatus } from '../api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  readonly status = signal<CompanyStatus | null>(null);
  private readonly api = inject(Api);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.getStatus().subscribe(data => this.status.set(data));
  }
}

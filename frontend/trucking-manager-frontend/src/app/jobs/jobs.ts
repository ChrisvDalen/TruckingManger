import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Api, CompanyStatus, Job } from '../api';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './jobs.html',
  styleUrl: './jobs.css'
})
export class Jobs implements OnInit {
  readonly jobs = signal<Job[]>([]);
  readonly status = signal<CompanyStatus | null>(null);
  private readonly api = inject(Api);

  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.api.availableJobs().subscribe(jobs => this.jobs.set(jobs));
    this.api.getStatus().subscribe(status => this.status.set(status));
  }
  accept(jobId: number, truckId: number): void {
    this.api.acceptJob(jobId, truckId).subscribe(() => this.load());
  }
}

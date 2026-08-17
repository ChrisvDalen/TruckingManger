import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Api } from '../api';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './jobs.html',
  styleUrl: './jobs.css'
})
export class Jobs implements OnInit {
  jobs: any[] = [];
  status: any;
  constructor(private api: Api) {}
  ngOnInit() {
    this.load();
  }
  load() {
    this.api.availableJobs().subscribe(j => this.jobs = j as any[]);
    this.api.getStatus().subscribe(s => this.status = s);
  }
  accept(jobId: number, truckId: number) {
    this.api.acceptJob(jobId, truckId).subscribe(() => this.load());
  }
}

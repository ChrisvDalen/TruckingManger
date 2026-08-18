import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api, CompanyStatus } from '../api';

@Component({
  selector: 'app-drivers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './drivers.html',
  styleUrl: './drivers.css'
})
export class Drivers implements OnInit {
  readonly status = signal<CompanyStatus | null>(null);
  readonly newDriver = {name: 'Driver', salary: 200};
  private readonly api = inject(Api);

  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.api.getStatus().subscribe(status => this.status.set(status));
  }
  hire(): void {
    this.api.hireDriver(this.newDriver.name, this.newDriver.salary).subscribe(() => this.load());
  }
}

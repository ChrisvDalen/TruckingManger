import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api, CompanyStatus } from '../api';

@Component({
  selector: 'app-trucks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trucks.html',
  styleUrl: './trucks.css'
})
export class Trucks implements OnInit {
  readonly status = signal<CompanyStatus | null>(null);
  readonly newTruck = {name: 'New Truck', consumption: 30, price: 10000};
  private readonly api = inject(Api);

  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.api.getStatus().subscribe(status => this.status.set(status));
  }
  buy(): void {
    this.api.buyTruck(this.newTruck.name, this.newTruck.consumption, this.newTruck.price)
      .subscribe(() => this.load());
  }
}

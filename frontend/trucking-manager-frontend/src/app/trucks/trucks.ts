import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../api';

@Component({
  selector: 'app-trucks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trucks.html',
  styleUrl: './trucks.css'
})
export class Trucks implements OnInit {
  status: any;
  newTruck = {name: 'New Truck', consumption: 30, price: 10000};
  constructor(private api: Api) {}
  ngOnInit() {
    this.load();
  }
  load() {
    this.api.getStatus().subscribe(s => this.status = s);
  }
  buy() {
    this.api.buyTruck(this.newTruck.name, this.newTruck.consumption, this.newTruck.price)
      .subscribe(() => this.load());
  }
}

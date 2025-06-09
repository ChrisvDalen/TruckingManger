import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../api';

@Component({
  selector: 'app-drivers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './drivers.html',
  styleUrl: './drivers.css'
})
export class Drivers implements OnInit {
  status: any;
  newDriver = {name: 'Driver', salary: 200};
  constructor(private api: Api) {}
  ngOnInit() {
    this.load();
  }
  load() {
    this.api.getStatus().subscribe(s => this.status = s);
  }
  hire() {
    this.api.hireDriver(this.newDriver.name, this.newDriver.salary).subscribe(() => this.load());
  }
}

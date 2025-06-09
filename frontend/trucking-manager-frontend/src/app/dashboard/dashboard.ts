import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Api } from '../api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  status: any;
  constructor(private api: Api) {}
  ngOnInit() {
    this.load();
  }
  load() {
    this.api.getStatus().subscribe(data => this.status = data);
  }
}

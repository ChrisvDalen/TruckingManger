import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class Api {
  private base = 'http://localhost:8080/api';
  constructor(private http: HttpClient) {}

  getStatus(): Observable<any> {
    return this.http.get(this.base + '/game/status');
  }

  advanceDay(): Observable<any> {
    return this.http.post(this.base + '/game/advance', {});
  }

  availableJobs(): Observable<any> {
    return this.http.get(this.base + '/jobs/available');
  }

  acceptJob(jobId: number, truckId: number): Observable<any> {
    return this.http.post(this.base + '/jobs/accept', { jobId, truckId });
  }

  buyTruck(name: string, consumption: number, price: number): Observable<any> {
    return this.http.post(this.base + '/trucks/buy', { name, consumption, price });
  }

  hireDriver(name: string, salary: number): Observable<any> {
    return this.http.post(this.base + '/drivers/hire', { name, salary });
  }
}

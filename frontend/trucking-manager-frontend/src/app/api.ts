import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class Api {
  private readonly base = 'http://localhost:8080/api';
  private readonly http = inject(HttpClient);

  getStatus(): Observable<CompanyStatus> {
    return this.http.get<CompanyStatus>(this.base + '/game/status');
  }

  advanceDay(): Observable<CompanyStatus> {
    return this.http.post<CompanyStatus>(this.base + '/game/advance', {});
  }

  availableJobs(): Observable<Job[]> {
    return this.http.get<Job[]>(this.base + '/jobs/available');
  }

  acceptJob(jobId: number, truckId: number): Observable<{ accepted: boolean }> {
    return this.http.post<{ accepted: boolean }>(this.base + '/jobs/accept', { jobId, truckId });
  }

  buyTruck(name: string, consumption: number, price: number): Observable<CompanyStatus> {
    return this.http.post<CompanyStatus>(this.base + '/trucks/buy', { name, consumption, price });
  }

  hireDriver(name: string, salary: number): Observable<CompanyStatus> {
    return this.http.post<CompanyStatus>(this.base + '/drivers/hire', { name, salary });
  }
}

export interface Truck {
  id: number;
  name: string;
  fuelConsumption: number;
  condition: number;
}

export interface Driver {
  id: number;
  name: string;
  dailySalary: number;
}

export interface Job {
  id: number;
  distance: number;
  weight: number;
  durationDays: number;
  reward: number;
  remainingDays: number;
}

export interface CompanyStatus {
  cash: number;
  trucks: Truck[];
  drivers: Driver[];
  activeJobs: Job[];
}

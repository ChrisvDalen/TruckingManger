import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';
import { Api } from './api';

describe('Api', () => {
  let api: Api;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Api, provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(Api);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('fetches game status from /api/game/status', async () => {
    const status = { cash: 1000, trucks: [], drivers: [], activeJobs: [] };
    const promise = firstValueFrom(api.getStatus());
    const req = http.expectOne('/api/game/status');
    expect(req.request.method).toBe('GET');
    req.flush(status);
    await expect(promise).resolves.toEqual(status);
  });

  it('posts to /api/game/advance', async () => {
    const status = { cash: 1000, trucks: [], drivers: [], activeJobs: [] };
    const promise = firstValueFrom(api.advanceDay());
    const req = http.expectOne('/api/game/advance');
    expect(req.request.method).toBe('POST');
    req.flush(status);
    await expect(promise).resolves.toEqual(status);
  });

  it('fetches available jobs from /api/jobs/available', async () => {
    const jobs = [{ id: 1, distance: 100, weight: 1, durationDays: 1, reward: 310, remainingDays: 0 }];
    const promise = firstValueFrom(api.availableJobs());
    const req = http.expectOne('/api/jobs/available');
    expect(req.request.method).toBe('GET');
    req.flush(jobs);
    await expect(promise).resolves.toEqual(jobs);
  });

  it('accepts a job with the correct payload', async () => {
    const promise = firstValueFrom(api.acceptJob(7, 3));
    const req = http.expectOne('/api/jobs/accept');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ jobId: 7, truckId: 3 });
    const body = { accepted: true };
    req.flush(body);
    await expect(promise).resolves.toEqual(body);
  });

  it('buys a truck with the correct payload', async () => {
    const status = { cash: 1000, trucks: [], drivers: [], activeJobs: [] };
    const promise = firstValueFrom(api.buyTruck('Rig', 40, 5000));
    const req = http.expectOne('/api/trucks/buy');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ name: 'Rig', consumption: 40, price: 5000 });
    req.flush(status);
    await expect(promise).resolves.toEqual(status);
  });

  it('hires a driver with the correct payload', async () => {
    const status = { cash: 1000, trucks: [], drivers: [], activeJobs: [] };
    const promise = firstValueFrom(api.hireDriver('Ann', 200));
    const req = http.expectOne('/api/drivers/hire');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ name: 'Ann', salary: 200 });
    req.flush(status);
    await expect(promise).resolves.toEqual(status);
  });
});

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Jobs } from './jobs';
import { Api, Job, CompanyStatus } from '../api';

describe('Jobs', () => {
  let http: HttpTestingController;
  const jobs: Job[] = [
    { id: 1, distance: 100, weight: 1, durationDays: 1, reward: 310, remainingDays: 0 },
  ];
  const status: CompanyStatus = { cash: 50000, trucks: [], drivers: [], activeJobs: [] };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [Jobs],
      providers: [Api, provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads available jobs and status on init', () => {
    const fixture = TestBed.createComponent(Jobs);
    const component = fixture.componentInstance;

    fixture.detectChanges();

    // init() issues two requests: /jobs/available then /game/status
    http.expectOne('/api/jobs/available').flush(jobs);
    http.expectOne('/api/game/status').flush(status);

    expect(component.jobs()).toEqual(jobs);
    expect(component.status()).toEqual(status);
  });

  it('accepts a job then reloads', () => {
    const fixture = TestBed.createComponent(Jobs);
    const component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit -> two GETs

    http.expectOne('/api/jobs/available').flush(jobs);
    http.expectOne('/api/game/status').flush(status);

    component.accept(1, 2);
    const acceptReq = http.expectOne('/api/jobs/accept');
    expect(acceptReq.request.body).toEqual({ jobId: 1, truckId: 2 });
    acceptReq.flush({ accepted: true });

    // reload() re-fetches both endpoints
    http.expectOne('/api/jobs/available').flush(jobs);
    http.expectOne('/api/game/status').flush(status);

    expect(component.jobs()).toEqual(jobs);
  });
});

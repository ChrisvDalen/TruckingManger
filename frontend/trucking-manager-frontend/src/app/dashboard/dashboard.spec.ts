import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Dashboard } from './dashboard';
import { Api, CompanyStatus } from '../api';

describe('Dashboard', () => {
  let http: HttpTestingController;
  const status: CompanyStatus = { cash: 50000, trucks: [], drivers: [], activeJobs: [] };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [Api, provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads company status on init', () => {
    const fixture = TestBed.createComponent(Dashboard);
    const dashboard = fixture.componentInstance;

    expect(dashboard.status()).toBeNull();
    fixture.detectChanges();

    const req = http.expectOne('/api/game/status');
    req.flush(status);

    expect(dashboard.status()).toEqual(status);
  });

  it('refreshes status when load() is called', () => {
    const fixture = TestBed.createComponent(Dashboard);
    const dashboard = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit -> one GET

    http.expectOne('/api/game/status').flush(status);

    dashboard.status.set(null);
    dashboard.load();
    http.expectOne('/api/game/status').flush(status);

    expect(dashboard.status()).toEqual(status);
  });
});

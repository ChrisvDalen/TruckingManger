import { Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { Jobs } from './jobs/jobs';
import { Trucks } from './trucks/trucks';
import { Drivers } from './drivers/drivers';
import { AdvanceDay } from './advance-day/advance-day';

export const routes: Routes = [
  { path: '', component: Dashboard },
  { path: 'jobs', component: Jobs },
  { path: 'trucks', component: Trucks },
  { path: 'drivers', component: Drivers },
  { path: 'advance', component: AdvanceDay }
];

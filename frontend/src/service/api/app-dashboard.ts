import { request } from '../request';

// Get numbers about the dashboard
export function fetchApiAppDashboardGetNumbers() {
  return request<Api.AppDashboard.Numbers>({
    url: '/api/app/dashboard/numbers',
    method: 'GET'
  });
}

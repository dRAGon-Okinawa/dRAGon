import { request } from '../request';

/** Granaries Search */
export function fetchGranariesSearch(params?: Api.GranaryManage.GranarySearchParams) {
  return request<Api.Common.PaginatingQueryRecord<Api.GranaryManage.Granary>>({
    url: '/api/backend/repository/granary/',
    method: 'GET',
    params
  });
}

/** Upsert Granary */
export function fetchUpsertGranary(data: Api.GranaryManage.Granary) {
  return request<Api.GranaryManage.Granary>({
    url: `/api/backend/repository/granary/${data.uuid}`,
    method: 'PUT',
    data
  });
}

/** Delete Granary */
export function fetchDeleteGranary(uuid: string) {
  return request<Api.Common.CommonRecord>({
    url: `/api/backend/repository/granary/${uuid}`,
    method: 'DELETE'
  });
}

/** Delete Granaries */
export function fetchDeleteMultipleGranaries(uuids: string[]) {
  return request<Api.Common.CommonRecord>({
    url: `/api/backend/repository/granary/deleteMultiple`,
    method: 'DELETE',
    data: {
      uuids
    }
  });
}

import { request } from '../request';

/** Farms Search */
export function fetchFarmsSearch(params?: Api.FarmManage.FarmSearchParams) {
  return request<Api.Common.PaginatingQueryRecord<Api.FarmManage.Farm>>({
    url: '/api/backend/repository/farm/',
    method: 'GET',
    params
  });
}

/** Upsert Farm */
export function fetchUpsertFarm(data: Api.FarmManage.Farm) {
  return request<Api.FarmManage.Farm>({
    url: `/api/backend/repository/farm/${data.uuid}`,
    method: 'PUT',
    data
  });
}

/** Delete Farm */
export function fetchDeleteFarm(uuid: string) {
  return request<Api.Common.CommonRecord>({
    url: `/api/backend/repository/farm/${uuid}`,
    method: 'DELETE'
  });
}

/** Delete Farms */
export function fetchDeleteMultipleFarms(uuids: string[]) {
  return request<Api.Common.CommonRecord>({
    url: `/api/backend/repository/farm/deleteMultiple`,
    method: 'DELETE',
    data: {
      uuids
    }
  });
}

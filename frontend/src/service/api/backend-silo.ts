import { request } from '../request';

/** Get Silos List */
export function fetchGetSilosList(params?: Api.SiloManage.SiloSearchParams) {
  return request<Api.Common.PaginatingQueryRecord<Api.SiloManage.Silo>>({
    url: '/api/backend/repository/silo/',
    method: 'GET',
    params
  });
}

/** Upsert Silo */
export function fetchUpsertSilo(data: Api.SiloManage.Silo) {
  return request<Api.SiloManage.Silo>({
    url: `/api/backend/repository/silo/${data.uuid}`,
    method: 'PUT',
    data
  });
}

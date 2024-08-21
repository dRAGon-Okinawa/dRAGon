import { request } from '../request';

/** Get Silos List */
export function fetchGetSilosList(params?: Api.SiloManage.SiloSearchParams) {
  return request<Api.Common.PaginatingQueryRecord<Api.SiloManage.Silo>>({
    url: '/api/backend/repository/silo/',
    method: 'get',
    params
  });
}

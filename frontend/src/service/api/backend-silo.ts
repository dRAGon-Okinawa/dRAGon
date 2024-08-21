import { request } from '../request';

/**
 * get all roles
 *
 * these roles are all enabled
 */
export function fetchGetAllRoles() {
  return request<Api.SystemManage.AllRole[]>({
    url: '/systemManage/getAllRoles',
    method: 'get'
  });
}

/** Get Silos List */
export function fetchGetSilosList(params?: Api.SiloManage.SiloSearchParams) {
  return request<Api.SiloManage.Silo>({
    url: '/api/backend/repository/silo/',
    method: 'get',
    params
  });
}

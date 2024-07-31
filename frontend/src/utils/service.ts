/**
 * Create service config by current env
 *
 * @param env The current env
 */
export function createServiceConfig(env: Env.ImportMeta) {
  const { VITE_SERVICE_BASE_URL } = env;
  const baseBackendURL = VITE_SERVICE_BASE_URL.replace('__HOSTNAME__', location.hostname).replace(
    '__SCHEME__',
    location.protocol.replace(':', '')
  );

  const httpConfig: App.Service.SimpleServiceConfig = {
    baseURL: baseBackendURL
  };

  const config: App.Service.ServiceConfig = {
    baseURL: httpConfig.baseURL,
    proxyPattern: createProxyPattern()
  };

  return config;
}

/**
 * get backend service base url
 *
 * @param env - the current env
 * @param isProxy - if use proxy
 */
export function getServiceBaseURL(env: Env.ImportMeta, isProxy: boolean) {
  const { baseURL } = createServiceConfig(env);

  return {
    baseURL: isProxy ? createProxyPattern() : baseURL
  };
}

/**
 * Get proxy pattern of backend service base url
 *
 * @param key If not set, will use the default key
 */
function createProxyPattern(key?: string) {
  if (!key) {
    return '/proxy-default';
  }

  return `/proxy-${key}`;
}

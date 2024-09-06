/**
 * Namespace Api
 *
 * All backend api type
 */
declare namespace Api {
  namespace Common {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    /** common params of paginating */
    interface PaginatingCommonParams {
      /** current page number */
      current: number;
      /** page size */
      size: number;
      /** total count */
      total: number;
    }

    /** common params of paginating query list data */
    interface PaginatingQueryRecord<T = any> extends PaginatingCommonParams {
      records: T[];
    }

    /**
     * enable status
     *
     * - "1": enabled
     * - "2": disabled
     */
    type EnableStatus = '1' | '2';

    /** common record */
    type CommonRecord<T = any> = {
      /** record uuid */
      uuid: string;
    } & T;
  }

  type SelectOptionItem = {
    /** value */
    value?: string;
    /** label */
    label: string;
    /** hint */
    hint?: string;
  };

  /**
   * namespace Auth
   *
   * backend api module: "auth"
   */
  namespace Auth {
    interface LoginToken {
      token: string;
      refreshToken: string;
    }

    interface UserInfo {
      userId: string;
      userName: string;
      roles: string[];
      buttons: string[];
    }
  }

  /**
   * namespace Route
   *
   * backend api module: "route"
   */
  namespace Route {
    type ElegantConstRoute = import('@elegant-router/types').ElegantConstRoute;

    interface MenuRoute extends ElegantConstRoute {
      id: string;
    }

    interface UserRoute {
      routes: MenuRoute[];
      home: import('@elegant-router/types').LastLevelRouteKey;
    }
  }

  /**
   * namespace AppDashboard
   *
   * backend api module: "appDashboard"
   */
  namespace AppDashboard {
    type Numbers = {
      /** total Farm count */
      farms: number;
      /** total Silo count */
      silos: number;
      /** total Document count */
      documents: number;
      /** system load average */
      systemLoadAverage: number;
      /** system processor count */
      availableProcessors: number;
      /** system architecture */
      arch: string;
      /** used memory */
      usedMemory: number;
      /** total memory */
      totalMemory: number;
      /** free memory */
      freeMemory: number;
      /** used memory percentage */
      usedMemoryPercentage: number;
      /** free memory percentage */
      freeMemoryPercentage: number;
      /** system uptime */
      uptime: number;
      /** heap memory usage */
      heapMemoryUsage: number;
      /** heap memory usage percentage */
      heapMemoryUsagePercentage: number;
      /** non-heap memory usage */
      nonHeapMemoryUsage: number;
    };
  }

  /**
   * namespace SiloManage
   *
   * backend api module: "siloManage"
   */
  namespace SiloManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    /** Vector Store Type */
    type VectorStoreType = 'PersistInMemoryEmbeddingStore' | 'InMemoryEmbeddingStore' | 'PGVectorEmbeddingStore';

    /** Embedding Model Type */
    type EmbeddingModelType =
      | 'BgeSmallEnV15QuantizedEmbeddingModel'
      | 'OpenAiEmbeddingAda002Model'
      | 'OpenAiEmbedding3SmallModel'
      | 'OpenAiEmbedding3LargeModel';

    /** Ingestor Loader */
    type IngestorLoaderType = 'None' | 'FileSystem' | 'URL';

    /** Silo Search Params */
    type SiloSearchParams = CommonType.RecordNullable<
      Pick<Api.SiloManage.Silo, 'name' | 'uuid' | 'vectorStore'> & Common.CommonSearchParams
    >;

    /** Silo */
    type Silo = Common.CommonRecord<{
      /** Silo Name */
      name: string;
      /** Vector Store Type */
      vectorStore: VectorStoreType | null;
      /** Embedding Model */
      embeddingModel: EmbeddingModelType | null;
      /** Ingestor Loader */
      ingestorLoader: IngestorLoaderType | null;
      /** Vector Store Settings */
      vectorStoreSettings: string[] | null;
      /* Embedding Settings */
      embeddingSettings: string[] | null;
      /* Ingestor Settings */
      ingestorSettings: string[] | null;
    }>;
  }

  /**
   * namespace FarmManage
   *
   * backend api module: "farmManage"
   */
  namespace FarmManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    /** Language Model Type */
    type LanguageModelType = 'OpenAiModel';

    /** Chat Memory Strategy Type */
    type ChatMemoryStrategyType = 'MaxMessages' | 'MaxTokens';

    /** Query Router Type */
    type QueryRouterType = 'Default' | 'LanguageModel';

    /** Farm Search Params */
    type FarmSearchParams = CommonType.RecordNullable<
      Pick<Api.FarmManage.Farm, 'name' | 'uuid' | 'raagIdentifier'> & Common.CommonSearchParams
    >;

    /** Farm */
    type Farm = Common.CommonRecord<{
      /** Farm Name */
      name: string;
      /** RaaG Identifier */
      raagIdentifier: string;
      /** Linked Silos */
      silos: string[];
      /** Language Model */
      languageModel: LanguageModelType | null;
      /** Language Model Settings */
      languageModelSettings: string[] | null;
      /** Chat Memory Strategy */
      chatMemoryStrategy: ChatMemoryStrategyType | null;
      /** Query Router Type */
      queryRouter: QueryRouterType | null;
      /** Retrieval Augmentor Settings */
      retrievalAugmentorSettings: string[] | null;
    }>;
  }

  /**
   * namespace SystemManage
   *
   * backend api module: "systemManage"
   */
  namespace SystemManage {
    /** role */
    type Role = Common.CommonRecord<{
      /** role name */
      roleName: string;
      /** role code */
      roleCode: string;
      /** role description */
      roleDesc: string;
    }>;

    /** role search params */
    type RoleSearchParams = CommonType.RecordNullable<
      Pick<Api.SystemManage.Role, 'uuid' | 'roleName' | 'roleCode'> & Common.CommonSearchParams
    >;

    /** role list */
    type RoleList = Common.PaginatingQueryRecord<Role>;

    /** all role */
    type AllRole = Pick<Role, 'uuid' | 'roleName' | 'roleCode'>;

    /**
     * user gender
     *
     * - "1": "male"
     * - "2": "female"
     */
    type UserGender = '1' | '2';

    /** user */
    type User = Common.CommonRecord<{
      /** user name */
      userName: string;
      /** user gender */
      userGender: UserGender | null;
      /** user nick name */
      nickName: string;
      /** user phone */
      userPhone: string;
      /** user email */
      userEmail: string;
      /** user role code collection */
      userRoles: string[];
    }>;

    /** user search params */
    type UserSearchParams = CommonType.RecordNullable<
      Pick<Api.SystemManage.User, 'uuid' | 'userName' | 'userGender' | 'nickName' | 'userPhone' | 'userEmail'> &
        Common.CommonSearchParams
    >;

    /** Silo List */
    type SiloList = Common.PaginatingQueryRecord<SiloManage.Silo>;

    /**
     * menu type
     *
     * - "1": directory
     * - "2": menu
     */
    type MenuType = '1' | '2';

    type MenuButton = {
      /**
       * button code
       *
       * it can be used to control the button permission
       */
      code: string;
      /** button description */
      desc: string;
    };

    /**
     * icon type
     *
     * - "2": local icon
     */
    type IconType = '2';

    type MenuPropsOfRoute = Pick<
      import('vue-router').RouteMeta,
      | 'i18nKey'
      | 'keepAlive'
      | 'constant'
      | 'order'
      | 'href'
      | 'hideInMenu'
      | 'activeMenu'
      | 'multiTab'
      | 'fixedIndexInTab'
      | 'query'
    >;

    type Menu = Common.CommonRecord<{
      /** parent menu id */
      parentId: number;
      /** menu type */
      menuType: MenuType;
      /** menu name */
      menuName: string;
      /** route name */
      routeName: string;
      /** route path */
      routePath: string;
      /** component */
      component?: string;
      /** icon type */
      iconType: IconType;
      /** icon */
      localIcon?: string;
      /** buttons */
      buttons?: MenuButton[] | null;
      /** children menu */
      children?: Menu[] | null;
    }> &
      MenuPropsOfRoute;

    /** menu list */
    type MenuList = Common.PaginatingQueryRecord<Menu>;

    type MenuTree = {
      id: number;
      label: string;
      pId: number;
      children?: MenuTree[];
    };
  }
}

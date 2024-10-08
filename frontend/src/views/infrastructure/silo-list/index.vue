<script setup lang="tsx">
import { fetchDeleteMultipleSilos, fetchDeleteSilo, fetchSilosSearch } from '@/service/api';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { embeddingModelRecord, ingestorLoaderRecord, vectoreStoreRecord } from '@/constants/business';
import { useTable, useTableOperate } from '@/hooks/common/table';
import SplitDropdown from '@/components/custom/split-dropdown.vue';
import TagRenderer from '@/components/custom/tag-renderer.vue';
import SiloEdit from './modules/silo-edit.vue';
import SiloSearch from './modules/silo-search.vue';

const appStore = useAppStore();

const {
  columns,
  columnChecks,
  data,
  getData,
  getDataByPage,
  loading,
  mobilePagination,
  searchParams,
  resetSearchParams
} = useTable({
  apiFn: fetchSilosSearch,
  showTotal: true,
  apiParams: {
    current: 1,
    size: 10,
    uuid: '',
    name: '',
    vectorStore: null
  },
  columns: () => [
    {
      type: 'selection',
      align: 'center',
      width: 48
    },
    {
      key: 'uuid',
      title: 'UUID',
      align: 'center',
      width: 90,
      render: row => row.uuid.split('-')[0]
    },
    {
      key: 'name',
      title: $t('common.name'),
      align: 'center',
      minWidth: 100
    },
    {
      key: 'vectorStore',
      title: $t('dRAGon.vectorStore'),
      align: 'center',
      width: 100,
      render: (row: Api.SiloManage.Silo) => (
        <TagRenderer
          value={row.vectorStore}
          tagMap={{
            PersistInMemoryEmbeddingStore: 'default',
            InMemoryEmbeddingStore: 'default',
            PGVectorEmbeddingStore: 'default'
          }}
          labelMap={vectoreStoreRecord}
        />
      )
    },
    {
      key: 'embeddingModel',
      title: $t('dRAGon.embeddingModel'),
      align: 'center',
      minWidth: 100,
      render: (row: Api.SiloManage.Silo) => (
        <TagRenderer
          value={row.embeddingModel}
          tagMap={{
            BgeSmallEnV15QuantizedEmbeddingModel: 'default',
            OpenAiEmbeddingAda002Model: 'default',
            OpenAiEmbedding3SmallModel: 'default',
            OpenAiEmbedding3LargeModel: 'default'
          }}
          labelMap={embeddingModelRecord}
        />
      )
    },
    {
      key: 'ingestorLoader',
      title: $t('dRAGon.ingestorLoader'),
      align: 'center',
      width: 120,
      render: (row: Api.SiloManage.Silo) => (
        <TagRenderer
          value={row.ingestorLoader}
          tagMap={{
            None: 'default',
            FileSystem: 'default',
            URL: 'default'
          }}
          labelMap={ingestorLoaderRecord}
        />
      )
    },
    {
      key: 'operate',
      title: $t('common.action'),
      align: 'center',
      width: 130,
      render: (row: Api.SiloManage.Silo) => (
        <div class="flex-center gap-8px">
          <SplitDropdown
            main-button-icon="mdi--lead-pencil"
            onMainAction={() => edit(row.uuid)}
            options={[
              {
                label: $t('common.edit'),
                icon: 'mdi--lead-pencil',
                callback: () => {
                  edit(row.uuid);
                }
              },
              { isDivider: true },
              {
                label: $t('common.delete'),
                icon: 'mdi--trash-can-outline',
                confirmTitle: () => `${$t('common.confirm')} - ${row.name}`,
                confirmMessage: $t('common.confirmDelete'),
                confirmPositiveText: $t('common.delete'),
                callback: () => {
                  handleDelete(row.uuid);
                }
              }
            ]}
          />
        </div>
      )
    }
  ]
});

const {
  drawerVisible,
  operateType,
  editingData,
  handleAdd,
  handleEdit,
  checkedRowKeys,
  onBatchDeleted,
  onDeleted
  // closeDrawer
} = useTableOperate(data, getData);

async function handleBatchDelete() {
  fetchDeleteMultipleSilos(checkedRowKeys.value).then(response => {
    if (response.error === null) {
      onBatchDeleted();
    }
  });
}

function handleDelete(_id: string) {
  fetchDeleteSilo(_id).then(response => {
    if (response.error === null) {
      onDeleted();
    }
  });
}

function handleReset() {
  resetSearchParams();
  getDataByPage();
}

function edit(id: string) {
  handleEdit(id);
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <SiloSearch v-model:model="searchParams" @reset="handleReset" @search="getDataByPage" />
    <NCard :title="$t('dRAGon.silos')" :bordered="false" size="small" class="sm:flex-1-hidden card-wrapper">
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :disabled-delete="checkedRowKeys.length === 0"
          :loading="loading"
          @add="handleAdd"
          @delete="handleBatchDelete"
          @refresh="getData"
        />
      </template>
      <NDataTable
        v-model:checked-row-keys="checkedRowKeys"
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :scroll-x="962"
        :loading="loading"
        remote
        :row-key="row => row.uuid"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <SiloEdit
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
    </NCard>
  </div>
</template>

<style scoped></style>

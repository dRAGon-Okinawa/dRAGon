<script setup lang="tsx">
import { fetchDeleteGranary, fetchDeleteMultipleGranaries, fetchGranariesSearch } from '@/service/api';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { granaryEngineTypeRecord } from '@/constants/business';
import { useTable, useTableOperate } from '@/hooks/common/table';
import SplitDropdown from '@/components/custom/split-dropdown.vue';
import TagRenderer from '@/components/custom/tag-renderer.vue';
import GranaryEdit from './modules/granary-edit.vue';
import GranarySearch from './modules/granary-search.vue';

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
  apiFn: fetchGranariesSearch,
  showTotal: true,
  apiParams: {
    current: 1,
    size: 10,
    uuid: '',
    name: '',
    engineType: null
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
      key: 'engineType',
      title: $t('dRAGon.engineType'),
      align: 'center',
      width: 200,
      render: (row: Api.GranaryManage.Granary) => (
        <TagRenderer
          value={row.engineType}
          tagMap={{
            WebSearchEngine: 'default'
          }}
          labelMap={granaryEngineTypeRecord}
        />
      )
    },
    {
      key: 'operate',
      title: $t('common.action'),
      align: 'center',
      width: 130,
      render: (row: Api.GranaryManage.Granary) => (
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
  fetchDeleteMultipleGranaries(checkedRowKeys.value).then(response => {
    if (response.error === null) {
      onBatchDeleted();
    }
  });
}

function handleDelete(_id: string) {
  fetchDeleteGranary(_id).then(response => {
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
    <GranarySearch v-model:model="searchParams" @reset="handleReset" @search="getDataByPage" />
    <NCard :title="$t('dRAGon.granaries')" :bordered="false" size="small" class="sm:flex-1-hidden card-wrapper">
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
      <GranaryEdit
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
    </NCard>
  </div>
</template>

<style scoped></style>

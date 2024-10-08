<script setup lang="tsx">
import { h } from 'vue';
import { fetchDeleteFarm, fetchDeleteMultipleFarms, fetchFarmsSearch } from '@/service/api';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { chatMemoryStrategyRecord, languageModelRecord } from '@/constants/business';
import { useTable, useTableOperate } from '@/hooks/common/table';
import SvgIcon from '@/components/custom/svg-icon.vue';
import SplitDropdown from '@/components/custom/split-dropdown.vue';
import TagRenderer from '@/components/custom/tag-renderer.vue';
import FarmEdit from './modules/farm-edit.vue';
import FarmSearch from './modules/farm-search.vue';

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
  apiFn: fetchFarmsSearch,
  showTotal: true,
  apiParams: {
    current: 1,
    size: 10,
    uuid: '',
    name: '',
    raagIdentifier: ''
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
      key: 'raagIdentifier',
      title: $t('dRAGon.raagIdentifier'),
      align: 'center'
    },
    {
      key: 'chatMemoryStrategy',
      title: $t('dRAGon.chatMemoryStrategy'),
      align: 'center',
      render: (row: Api.FarmManage.Farm) => (
        <TagRenderer
          value={row.chatMemoryStrategy}
          tagMap={{
            MaxMessages: 'default',
            MaxTokens: 'default'
          }}
          labelMap={chatMemoryStrategyRecord}
        />
      )
    },
    {
      key: 'languageModel',
      title: $t('dRAGon.languageModel'),
      align: 'center',
      render: (row: Api.FarmManage.Farm) => (
        <TagRenderer
          value={row.languageModel}
          tagMap={{
            OpenAiModel: 'default'
          }}
          labelMap={languageModelRecord}
        />
      )
    },
    {
      key: 'silos',
      title() {
        return renderHeaderIcon('mdi--silo-outline', $t('dRAGon.silos'));
      },
      align: 'center',
      render: (row: Api.FarmManage.Farm) => row.silos.length,
      width: 30
    },
    {
      key: 'granaries',
      title() {
        return renderHeaderIcon('mdi--warehouse', $t('dRAGon.granaries'));
      },
      align: 'center',
      render: (row: Api.FarmManage.Farm) => row.granaries.length,
      width: 30
    },
    {
      key: 'operate',
      title: $t('common.action'),
      align: 'center',
      width: 130,
      render: (row: Api.FarmManage.Farm) => (
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
  fetchDeleteMultipleFarms(checkedRowKeys.value).then(response => {
    if (response.error === null) {
      onBatchDeleted();
    }
  });
}

function handleDelete(_id: string) {
  fetchDeleteFarm(_id).then(response => {
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

function renderHeaderIcon(icon: string, tooltip?: string) {
  return h(SvgIcon, {
    localIcon: icon,
    tooltip
  });
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <FarmSearch v-model:model="searchParams" @reset="handleReset" @search="getDataByPage" />
    <NCard :title="$t('dRAGon.farms')" :bordered="false" size="small" class="sm:flex-1-hidden card-wrapper">
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
      <FarmEdit
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />
    </NCard>
  </div>
</template>

<style scoped></style>

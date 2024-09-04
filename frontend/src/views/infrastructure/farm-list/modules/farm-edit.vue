<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { NIL as NIL_UUID } from 'uuid';
import type { SelectOption } from 'naive-ui';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { chatMemoryStrategyOptions, languageModelOptions } from '@/constants/business';
import { fetchSilosSearch, fetchUpsertFarm } from '@/service/api';
import KVSettings from '../../../../components/custom/kv-settings.vue';

defineOptions({
  name: 'FarmEdit'
});

interface Props {
  /** the type of operation */
  operateType: NaiveUI.TableOperateType;
  /** the edit row data */
  rowData?: Api.FarmManage.Farm | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const silosLoadingRef = ref(false);
const silosOptionsRef = ref<SelectOption[]>([]);

const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule, formRules } = useFormRules();

const title = computed(() => {
  const titles: Record<NaiveUI.TableOperateType, string> = {
    add: $t('common.add'),
    edit: $t('common.edit')
  };
  return titles[props.operateType];
});

type Model = Pick<Api.FarmManage.Farm, 'uuid'>;

const model: Model = reactive(createDefaultModel());

function createDefaultModel(): Api.FarmManage.Farm {
  return {
    uuid: NIL_UUID,
    name: '',
    raagIdentifier: null,
    silos: [],
    languageModel: null,
    languageModelSettings: [],
    chatMemoryStrategy: null,
    retrievalAugmentorSettings: []
  };
}

type RuleKey = Extract<keyof Model, 'name' | 'raagIdentifier' | 'languageModel' | 'chatMemoryStrategy'>;

const rules: Record<RuleKey, App.Global.FormRule> = {
  name: defaultRequiredRule,
  raagIdentifier: formRules.raagIdentifier,
  languageModel: defaultRequiredRule,
  chatMemoryStrategy: defaultRequiredRule
};

const kvLanguageModelSettingsKey = ref(0);
const kvRetrievalAugmentorSettingsKey = ref(0);

function refreshKeyValueSettings() {
  kvLanguageModelSettingsKey.value += 1;
  kvRetrievalAugmentorSettingsKey.value += 1;
}

function handleInitModel() {
  Object.assign(model, createDefaultModel());
  if (props.operateType === 'edit' && props.rowData) {
    Object.assign(model, props.rowData);
  }
  refreshKeyValueSettings();
  initSilosOptions();
}

function closeDrawer() {
  visible.value = false;
}

function initSilosOptions() {
  fetchSilosSearch({ current: 1, size: 100 }).then(response => {
    if (response.error === null) {
      silosOptionsRef.value = response.data.records.map(item => ({
        label: item.name,
        value: item.uuid
      }));
    }
  });
}

function handleSearch(query: string) {
  if (!query.length) {
    silosOptionsRef.value = [];
    return;
  }
  silosLoadingRef.value = true;
  fetchSilosSearch({ name: query, current: 1, size: 10 }).then(response => {
    if (response.error === null) {
      silosOptionsRef.value = response.data.records.map(item => ({
        label: item.name,
        value: item.uuid
      }));
    }
    silosLoadingRef.value = false;
  });
}

async function handleSubmit() {
  await validate();
  fetchUpsertFarm(model as Api.FarmManage.Farm).then(response => {
    if (response.error === null) {
      window.$message?.success($t('common.updateSuccess'));
      closeDrawer();
      emit('submitted');
    }
  });
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});
</script>

<template>
  <NDrawer v-model:show="visible" display-directive="show" width="80%">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <NForm ref="formRef" :model="model" :rules="rules">
        <NDivider title-placement="left">
          {{ $t('dRAGon.farm') }}
        </NDivider>
        <NFormItem :label="$t('common.name')" path="name">
          <NInput v-model:value="model.name" :placeholder="$t('common.name')" />
        </NFormItem>
        <NFormItem :label="$t('dRAGon.raagIdentifier')" path="raagIdentifier">
          <NInput v-model:value="model.raagIdentifier" :placeholder="$t('dRAGon.raagIdentifier')" />
        </NFormItem>
        <NFormItem :label="$t('dRAGon.chatMemoryStrategy')" path="chatMemoryStrategy">
          <NSelect
            v-model:value="model.chatMemoryStrategy"
            :placeholder="$t('dRAGon.chatMemoryStrategy')"
            :options="chatMemoryStrategyOptions"
            clearable
          />
        </NFormItem>
        <NFormItem :label="$t('dRAGon.silos')" path="silos">
          <NSelect
            v-model:value="model.silos"
            multiple
            filterable
            :placeholder="$t('dRAGon.silos')"
            :options="silosOptionsRef"
            :loading="silosLoadingRef"
            clearable
            remote
            :clear-filter-after-select="true"
            @search="handleSearch"
          />
        </NFormItem>
        <NDivider title-placement="left">
          {{ $t('dRAGon.languageModel') }}
        </NDivider>
        <NFormItem :label="$t('common.type')" path="languageModel">
          <NSelect
            v-model:value="model.languageModel"
            :placeholder="$t('dRAGon.languageModel')"
            :options="languageModelOptions"
            clearable
          />
        </NFormItem>
        <NCollapse>
          <NCollapseItem :title="$t('common.settings')">
            <KVSettings :key="kvLanguageModelSettingsKey" v-model:settings="model.languageModelSettings" />
          </NCollapseItem>
        </NCollapse>
        <NDivider title-placement="left">
          {{ $t('dRAGon.retrievalAugmentor') }}
        </NDivider>
        <NCollapse>
          <NCollapseItem :title="$t('common.settings')">
            <KVSettings :key="kvRetrievalAugmentorSettingsKey" v-model:settings="model.retrievalAugmentorSettings" />
          </NCollapseItem>
        </NCollapse>
      </NForm>
      <template #footer>
        <NSpace :size="16">
          <NButton @click="closeDrawer">{{ $t('common.cancel') }}</NButton>
          <NButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
        </NSpace>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>

<style scoped></style>

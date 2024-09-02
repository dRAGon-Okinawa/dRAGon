<script setup lang="ts">
import { computed, reactive, watch } from 'vue';
import { NIL as NIL_UUID } from 'uuid';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { embeddingModelOptions, ingestorLoaderOptions, vectorStoreOptions } from '@/constants/business';
import { fetchUpsertSilo } from '@/service/api';
import KVSettings from '../../../../components/custom/kv-settings.vue';

defineOptions({
  name: 'SiloEdit'
});

interface Props {
  /** the type of operation */
  operateType: NaiveUI.TableOperateType;
  /** the edit row data */
  rowData?: Api.SiloManage.Silo | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() => {
  const titles: Record<NaiveUI.TableOperateType, string> = {
    add: $t('common.add'),
    edit: $t('common.edit')
  };
  return titles[props.operateType];
});

type Model = Pick<Api.SiloManage.Silo, 'uuid'>;

const model: Model = reactive(createDefaultModel());

function createDefaultModel(): Api.SiloManage.Silo {
  return {
    uuid: NIL_UUID,
    name: '',
    vectorStore: null,
    embeddingModel: null,
    ingestorLoader: null,
    vectorStoreSettings: [],
    embeddingSettings: [],
    ingestorSettings: []
  };
}

type RuleKey = Extract<keyof Model, 'name' | 'vectorStore' | 'embeddingModel' | 'ingestorLoader'>;

const rules: Record<RuleKey, App.Global.FormRule> = {
  name: defaultRequiredRule,
  vectorStore: defaultRequiredRule,
  embeddingModel: defaultRequiredRule,
  ingestorLoader: defaultRequiredRule
};

function handleInitModel() {
  Object.assign(model, createDefaultModel());
  if (props.operateType === 'edit' && props.rowData) {
    Object.assign(model, props.rowData);
  }
}

function closeDrawer() {
  visible.value = false;
}

async function handleSubmit() {
  await validate();
  fetchUpsertSilo(model as Api.SiloManage.Silo).then(response => {
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
  <NDrawer v-model:show="visible" display-directive="show" :width="360">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <NForm ref="formRef" :model="model" :rules="rules">
        <NDivider title-placement="left">
          {{ $t('dRAGon.silo') }}
        </NDivider>
        <NFormItem :label="$t('common.name')" path="name">
          <NInput v-model:value="model.name" :placeholder="$t('common.name')" />
        </NFormItem>
        <NDivider title-placement="left">
          {{ $t('dRAGon.vectorStore') }}
        </NDivider>
        <NFormItem :label="$t('common.type')" path="vectorStore">
          <NSelect
            v-model:value="model.vectorStore"
            :placeholder="$t('dRAGon.vectorStore')"
            :options="vectorStoreOptions"
            clearable
          />
        </NFormItem>
        <NCollapse>
          <NCollapseItem :title="$t('common.settings')">
            <KVSettings v-model:settings="model.vectorStoreSettings" />
          </NCollapseItem>
        </NCollapse>
        <NDivider title-placement="left">
          {{ $t('dRAGon.embeddingModel') }}
        </NDivider>
        <NFormItem :label="$t('common.type')" path="embeddingModel">
          <NSelect
            v-model:value="model.embeddingModel"
            :placeholder="$t('dRAGon.embeddingModel')"
            :options="embeddingModelOptions"
            clearable
          />
        </NFormItem>
        <NCollapse>
          <NCollapseItem :title="$t('common.settings')">
            <KVSettings v-model:settings="model.embeddingSettings" />
          </NCollapseItem>
        </NCollapse>
        <NDivider title-placement="left">
          {{ $t('dRAGon.ingestorLoader') }}
        </NDivider>
        <NFormItem :label="$t('common.type')" path="ingestorLoader">
          <NSelect
            v-model:value="model.ingestorLoader"
            :placeholder="$t('dRAGon.ingestorLoader')"
            :options="ingestorLoaderOptions"
            clearable
          />
        </NFormItem>
        <NCollapse>
          <NCollapseItem :title="$t('common.settings')">
            <KVSettings v-model:settings="model.ingestorSettings" />
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

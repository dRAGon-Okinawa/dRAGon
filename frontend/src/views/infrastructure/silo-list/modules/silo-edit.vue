<script setup lang="ts">
import { computed, reactive, watch } from 'vue';
import { NIL as NIL_UUID } from 'uuid';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { translateOptions } from '@/utils/common';
import { embeddingModelOptions, ingestorLoaderOptions, vectorStoreOptions } from '@/constants/business';
import { fetchUpsertSilo } from '@/service/api';

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

function createDefaultModel(): Model {
  return {
    uuid: NIL_UUID
  };
}

type RuleKey = Extract<keyof Model, 'userName' | 'status'>;

const rules: Record<RuleKey, App.Global.FormRule> = {
  userName: defaultRequiredRule,
  status: defaultRequiredRule
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
        <NInput v-model:value="model.name" :placeholder="$t('common.name')" />
        <NDivider title-placement="left">
          {{ $t('dRAGon.vectorStore') }}
        </NDivider>
        <NSelect
          v-model:value="model.vectorStore"
          :placeholder="$t('dRAGon.vectorStore')"
          :options="translateOptions(vectorStoreOptions)"
          clearable
          class="mb-4"
        />
        <NCollapse>
          <NCollapseItem :title="$t('common.settings')">
            <NDynamicInput>
              <template #create-button-default>
                {{ $t('common.add') }}
              </template>
              <div class="flex">
                <NFormItem ignore-path-change :show-label="false" class="flex-auto">
                  <NInput :placeholder="$t('common.key')" @keydown.enter.prevent />
                </NFormItem>
                <div class="mt-1 w-14 flex-auto text-center align-middle">=</div>
                <NFormItem ignore-path-change :show-label="false" class="flex-auto">
                  <NInput :placeholder="$t('common.value')" @keydown.enter.prevent />
                </NFormItem>
              </div>
            </NDynamicInput>
          </NCollapseItem>
        </NCollapse>
        <NDivider title-placement="left">
          {{ $t('dRAGon.embeddingModel') }}
        </NDivider>
        <NSelect
          v-model:value="model.embeddingModel"
          :placeholder="$t('dRAGon.embeddingModel')"
          :options="translateOptions(embeddingModelOptions)"
          clearable
        />
        <NDivider title-placement="left">
          {{ $t('dRAGon.ingestorLoader') }}
        </NDivider>
        <NSelect
          v-model:value="model.ingestorLoader"
          :placeholder="$t('dRAGon.ingestorLoader')"
          :options="translateOptions(ingestorLoaderOptions)"
          clearable
        />
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

<script setup lang="ts">
import { computed, reactive, watch } from 'vue';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { translateOptions } from '@/utils/common';
import { embeddingModelOptions, ingestorLoaderOptions, vectorStoreOptions } from '@/constants/business';

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

type Model = Pick<Api.SiloManage.Silo, 'name'>;

const model: Model = reactive(createDefaultModel());

function createDefaultModel(): Model {
  return {
    name: ''
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
  // request
  window.$message?.success($t('common.updateSuccess'));
  closeDrawer();
  emit('submitted');
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
        <NFormItem :label="$t('common.name')" path="name">
          <NInput v-model:value="model.name" :placeholder="$t('common.name')" />
        </NFormItem>
        <NFormItem :label="$t('dRAGon.vectorStore')" path="vectorStore">
          <NSelect
            v-model:value="model.vectorStore"
            :placeholder="$t('dRAGon.vectorStore')"
            :options="translateOptions(vectorStoreOptions)"
            clearable
          />
        </NFormItem>
        <NFormItem :label="$t('dRAGon.embeddingModel')" path="embeddingModel">
          <NSelect
            v-model:value="model.embeddingModel"
            :placeholder="$t('dRAGon.embeddingModel')"
            :options="translateOptions(embeddingModelOptions)"
            clearable
          />
        </NFormItem>
        <NFormItem :label="$t('dRAGon.ingestorLoader')" path="ingestorLoader">
          <NSelect
            v-model:value="model.ingestorLoader"
            :placeholder="$t('dRAGon.ingestorLoader')"
            :options="translateOptions(ingestorLoaderOptions)"
            clearable
          />
        </NFormItem>
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

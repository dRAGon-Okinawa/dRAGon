<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { NIL as NIL_UUID } from 'uuid';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { granaryEngineTypeOptions } from '@/constants/business';
import { fetchUpsertGranary } from '@/service/api';
import KVSettings from '../../../../components/custom/kv-settings.vue';

defineOptions({
  name: 'GranaryEdit'
});

interface Props {
  /** the type of operation */
  operateType: NaiveUI.TableOperateType;
  /** the edit row data */
  rowData?: Api.GranaryManage.Granary | null;
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

type Model = Pick<Api.GranaryManage.Granary, 'uuid'>;

const model: Model = reactive(createDefaultModel());

function createDefaultModel(): Api.GranaryManage.Granary {
  return {
    uuid: NIL_UUID,
    name: '',
    description: '',
    engineType: null,
    engineSettings: []
  };
}

type RuleKey = Extract<keyof Model, 'name' | 'engineType'>;

const rules: Record<RuleKey, App.Global.FormRule> = {
  name: defaultRequiredRule,
  engineType: defaultRequiredRule
};

const kvEngineSettingsKey = ref(0);

function refreshKeyValueSettings() {
  kvEngineSettingsKey.value += 1;
}

function handleInitModel() {
  Object.assign(model, createDefaultModel());
  if (props.operateType === 'edit' && props.rowData) {
    Object.assign(model, props.rowData);
  }
  refreshKeyValueSettings();
}

function closeDrawer() {
  visible.value = false;
}

async function handleSubmit() {
  await validate();
  fetchUpsertGranary(model as Api.GranaryManage.Granary).then(response => {
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
          {{ $t('dRAGon.granary') }}
        </NDivider>
        <NFormItem :label="$t('common.name')" path="name">
          <NInput v-model:value="model.name" :placeholder="$t('common.name')" />
        </NFormItem>
        <NFormItem :label="$t('common.description')" path="description">
          <NInput
            v-model:value="model.description"
            :placeholder="$t('common.description')"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 5 }"
          />
        </NFormItem>
        <NDivider title-placement="left">
          {{ $t('dRAGon.engineType') }}
        </NDivider>
        <NFormItem :label="$t('common.type')" path="engineType">
          <NSelect
            v-model:value="model.engineType"
            :placeholder="$t('dRAGon.engineType')"
            :options="granaryEngineTypeOptions"
            clearable
          />
        </NFormItem>
        <NCollapse>
          <NCollapseItem :title="$t('common.settings')">
            <KVSettings :key="kvEngineSettingsKey" v-model:settings="model.engineSettings" />
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

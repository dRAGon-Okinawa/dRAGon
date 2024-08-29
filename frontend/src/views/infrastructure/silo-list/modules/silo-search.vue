<script setup lang="ts">
import { computed } from 'vue';
import { $t } from '@/locales';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { vectorStoreOptions } from '@/constants/business';
import { translateOptions } from '@/utils/common';

defineOptions({
  name: 'SiloSearch'
});

interface Emits {
  (e: 'reset'): void;
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const { formRef, validate, restoreValidation } = useNaiveForm();

const model = defineModel<Api.SiloManage.SiloSearchParams>('model', { required: true });

type RuleKey = Extract<keyof Api.SystemManage.UserSearchParams, 'userEmail' | 'userPhone'>;

const rules = computed<Record<RuleKey, App.Global.FormRule>>(() => {
  const { patternRules } = useFormRules(); // inside computed to make locale reactive

  return {
    userEmail: patternRules.email,
    userPhone: patternRules.phone
  };
});

async function reset() {
  await restoreValidation();
  emit('reset');
}

async function search() {
  await validate();
  emit('search');
}
</script>

<template>
  <NCard :title="$t('common.search')" :bordered="false" size="small" class="card-wrapper">
    <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="80">
      <NGrid responsive="screen" item-responsive>
        <NFormItemGi span="24 s:12 m:6" label="UUID" path="uuid" class="pr-24px">
          <NInput v-model:value="model.uuid" placeholder="UUID" @keyup.enter="search" />
        </NFormItemGi>
        <NFormItemGi span="24 s:12 m:6" :label="$t('common.name')" path="name" class="pr-24px">
          <NInput v-model:value="model.name" :placeholder="$t('common.name')" @keyup.enter="search" />
        </NFormItemGi>
        <NFormItemGi span="24 s:12 m:6" :label="$t('dRAGon.vectorStore')" path="vectorStore" class="pr-24px">
          <NSelect
            v-model:value="model.vectorStore"
            :placeholder="$t('dRAGon.vectorStore')"
            :options="translateOptions(vectorStoreOptions)"
            clearable
            @update-value="search"
          />
        </NFormItemGi>
        <NFormItemGi span="24 s:12 m:6" class="pr-24px">
          <NSpace class="w-full" justify="end">
            <NButton @click="reset">
              <template #icon>
                <icon-ic-round-refresh class="text-icon" />
              </template>
              {{ $t('common.reset') }}
            </NButton>
            <NButton type="primary" ghost @click="search">
              <template #icon>
                <icon-ic-round-search class="text-icon" />
              </template>
              {{ $t('common.search') }}
            </NButton>
          </NSpace>
        </NFormItemGi>
      </NGrid>
    </NForm>
  </NCard>
</template>

<style scoped></style>

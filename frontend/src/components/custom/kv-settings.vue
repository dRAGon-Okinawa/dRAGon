<script setup lang="ts">
import { ref, watch } from 'vue';
import debounce from 'lodash/debounce';
import { $t } from '@/locales';

defineOptions({
  name: 'KVSettings'
});

const settingsModel = defineModel<string[]>('settings', { required: true });

const parseSettingsModel = () => {
  if (!settingsModel.value) {
    return [];
  }
  return settingsModel.value.map(item => {
    const [key, value] = item.split('=');
    return { key, value: value || '' };
  });
};

const settings = ref(parseSettingsModel());

function onCreate() {
  return {
    key: '',
    value: ''
  };
}

const syncSettingsModel = debounce(() => {
  settingsModel.value = settings.value
    .map(item => {
      if (item.key.trim() === '' || item.value.trim() === '') {
        return '';
      }
      return `${item.key}=${item.value}`;
    })
    .filter(item => item !== '' && item !== '=');
}, 300);

watch(
  settings,
  () => {
    syncSettingsModel();
  },
  { deep: true }
);
</script>

<template>
  <NDynamicInput v-model:value="settings" :on-create="onCreate" show-sort-button>
    <template #create-button-default>
      {{ $t('common.add') }}
    </template>
    <template #default="{ index }">
      <div class="w-full flex">
        <NFormItem zz:path="`settings[${index}].key`" ignore-path-change :show-label="false" class="max-w-64 flex-auto">
          <NInput v-model:value="settings[index].key" :placeholder="$t('common.key')" @keydown.enter.prevent />
        </NFormItem>
        <div class="flex-0 mt-1 w-14 text-center align-middle">=</div>
        <NFormItem zz:path="`settings[${index}].value`" ignore-path-change :show-label="false" class="flex-auto">
          <NInput v-model:value="settings[index].value" :placeholder="$t('common.value')" @keydown.enter.prevent />
        </NFormItem>
      </div>
    </template>
  </NDynamicInput>
</template>

<style scoped></style>

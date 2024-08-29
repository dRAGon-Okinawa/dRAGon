<script setup lang="ts">
import { computed } from 'vue';
import { $t } from '@/locales';

defineOptions({
  name: 'KVSettings'
});

const settingsModel = defineModel<string[]>('settings', { required: true });

const settings = computed({
  get() {
    // console.log(settingsModel.value);
    if (!settingsModel.value) {
      return [];
    }

    // Split settingsModel into key-value pairs
    const test = settingsModel.value.map(item => {
      const [key, value] = item.split('=');
      return { key, value: value || '' };
    });

    // console.log(test);

    return test;
  },
  set(newValue) {
    // console.log(newValue);
    settingsModel.value = newValue.map(item => (item ? `${item.key}=${item.value}` : ''));
  }
});
</script>

<template>
  <NDynamicInput v-model:value="settings">
    <template #create-button-default>
      {{ $t('common.add') }}
    </template>
    <template #default="{ index }">
      <div class="flex">
        <NFormItem :path="`settings[${index}].key`" ignore-path-change :show-label="false" class="flex-auto">
          <NInput v-model:value="settings[index].key" :placeholder="$t('common.key')" @keydown.enter.prevent />
        </NFormItem>
        <div class="mt-1 w-14 flex-auto text-center align-middle">=</div>
        <NFormItem :path="`settings[${index}].value`" ignore-path-change :show-label="false" class="flex-auto">
          <NInput v-model:value="settings[index].value" :placeholder="$t('common.value')" @keydown.enter.prevent />
        </NFormItem>
      </div>
    </template>
  </NDynamicInput>
</template>

<style scoped></style>

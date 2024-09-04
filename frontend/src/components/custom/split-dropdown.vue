<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { useDialog } from 'naive-ui';
import { $t } from '@/locales';

defineOptions({
  name: 'SplitDropdown'
});

interface Emits {
  (e: 'mainAction'): void;
}

interface DropdownOption {
  label?: string;
  value?: any;
  icon?: string;
  isDivider?: boolean;
  callback?: () => void;
  confirmTitle?: string | (() => import('vue').VNodeChild);
  confirmMessage?: string | (() => import('vue').VNodeChild);
  confirmPositiveText?: string;
  confirmNegativeText?: string;
}

const emit = defineEmits<Emits>();

const mainButtonLabel = defineModel<string>('mainButtonLabel', { default: '' });
const mainButtonIcon = defineModel<string>('mainButtonIcon', { default: '' });
const options = defineModel<DropdownOption[]>('options', { default: () => [] });

const isDropdownOpen = ref(false);

const onMainButtonClick = () => {
  emit('mainAction');
};

const handleClickOutside = event => {
  if (!event.target.closest('.inline-flex')) {
    isDropdownOpen.value = false;
    document.removeEventListener('click', handleClickOutside);
  }
};

onMounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
  if (isDropdownOpen.value) {
    document.addEventListener('click', handleClickOutside);
  } else {
    document.removeEventListener('click', handleClickOutside);
  }
};

const dialog = useDialog();

const onOptionClick = (option: DropdownOption) => {
  if (option.isDivider) {
    return;
  }
  isDropdownOpen.value = false;
  if (option.confirmTitle || option.confirmMessage || option.confirmPositiveText || option.confirmNegativeText) {
    dialog.warning({
      title: option.confirmTitle || $t('common.confirm'),
      content: option.confirmMessage || $t('common.sure'),
      positiveText: option.confirmPositiveText || $t('common.yesOrNo.yes'),
      negativeText: option.confirmNegativeText || $t('common.yesOrNo.no'),
      onPositiveClick: () => {
        if (option.callback) {
          option.callback();
        }
      },
      onNegativeClick: () => {}
    });
    return;
  }
  if (option.callback) {
    option.callback();
  }
};
</script>

<template>
  <div class="relative inline-flex">
    <button
      class="dropdown-split-main-button border border-primary-500 rounded-l bg-inherit px-4 py-2 text-primary font-semibold hover:bg-primary-500 hover:text-white"
      @click="onMainButtonClick"
    >
      <SvgIcon v-if="mainButtonIcon" :local-icon="mainButtonIcon" />
      {{ mainButtonLabel }}
    </button>
    <button
      class="border border-l-0 border-primary-500 rounded-r bg-primary-500 px-2 py-2 text-gray-100 font-semibold hover:bg-primary-600"
      @click="toggleDropdown"
    >
      <SvgIcon local-icon="mdi--arrow-down-drop-circle-outline" />
    </button>
    <div v-if="isDropdownOpen" class="absolute right-0 z-10 mt-2 w-48 rounded bg-white shadow-md dark:bg-[#48484E]">
      <ul>
        <template v-for="option in options" :key="option.value || option.label">
          <li
            v-if="!option.isDivider"
            class="m-1 flex cursor-pointer items-center rounded-md px-4 py-2 hover:bg-gray-200 dark:hover:bg-[#59595E]"
            @click="onOptionClick(option)"
          >
            <NDialogProvider>
              <SvgIcon v-if="option.icon" :local-icon="option.icon" class="mr-2" />
              <span>{{ option.label }}</span>
            </NDialogProvider>
          </li>
          <li v-else class="my-1 border-t dark:border-t-[#59595E]"></li>
        </template>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.main-button {
  display: flex;
  align-items: center;
}

.dropdown-split-main-button:hover {
  border-right: solid 1px #e2e8f0;
}
</style>

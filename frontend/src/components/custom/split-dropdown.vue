<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';

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
  document.removeEventListener('click', handleClickOutside); // Clean up to prevent duplicate events
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

const onOptionClick = (option: DropdownOption) => {
  if (option.isDivider) {
    return;
  }
  isDropdownOpen.value = false; // Close dropdown after option click
  if (option.callback) {
    option.callback();
  }
};
</script>

<template>
  <div class="relative inline-flex">
    <button
      class="border border-blue-500 rounded-l bg-blue-500 px-4 py-2 text-white font-semibold hover:bg-blue-600"
      @click="onMainButtonClick"
    >
      <SvgIcon v-if="mainButtonIcon" :local-icon="mainButtonIcon" />
      {{ mainButtonLabel }}
    </button>
    <button
      class="border border-l-0 border-blue-500 rounded-r bg-blue-500 px-2 py-2 text-white font-semibold hover:bg-blue-600"
      @click="toggleDropdown"
    >
      <SvgIcon local-icon="mdi--arrow-down-drop-circle-outline" />
    </button>
    <div
      v-if="isDropdownOpen"
      class="absolute right-0 z-10 mt-2 w-48 border border-gray-300 rounded bg-white shadow-md"
    >
      <ul>
        <template v-for="option in options" :key="option.value || option.label">
          <li
            v-if="!option.isDivider"
            class="flex cursor-pointer items-center px-4 py-2 hover:bg-gray-200"
            @click="onOptionClick(option)"
          >
            <SvgIcon v-if="option.icon" :local-icon="option.icon" class="mr-2" />
            {{ option.label }}
          </li>
          <li v-else class="my-1 border-t"></li>
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
</style>

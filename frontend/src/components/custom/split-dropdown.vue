<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';

defineOptions({
  name: 'SplitDropdown'
});

interface Emits {
  (e: 'mainAction'): void;
}

const emit = defineEmits<Emits>();

const mainButtonLabel = defineModel<string>('mainButtonLabel', { default: '' });
const mainButtonIcon = defineModel<string>('mainButtonIcon', { default: '' });

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
      <slot></slot>
    </div>
  </div>
</template>

<style scoped></style>

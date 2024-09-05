<script setup lang="ts">
import { NTag } from 'naive-ui';

interface Props {
  value: string | null;
  tagMap: Record<string, NaiveUI.ThemeColor>;
  labelMap: Record<string, string>;
}

const props = defineProps<Props>();
</script>

<template>
  <NTag v-if="props.value !== null" :type="props.tagMap[props.value]">
    {{ props.labelMap[props.value] }}
  </NTag>
</template>

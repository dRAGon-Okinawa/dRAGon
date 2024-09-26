<script setup lang="ts">
interface Props {
  value: string | null;
  tagMap: Record<string, NaiveUI.ThemeColor>;
  labelMap: Record<string, string | Api.SelectOptionItem>;
}

const props = defineProps<Props>();

function getLabel(value: string | null) {
  if (value === null) {
    return '';
  }
  const item = props.labelMap[value];
  return typeof item === 'string' ? item : item.label;
}
</script>

<template>
  <NTag v-if="props.value !== null" :type="props.tagMap[props.value]">
    {{ getLabel(props.value) }}
  </NTag>
</template>

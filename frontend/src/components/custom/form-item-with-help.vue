<script setup lang="ts">
interface Props {
  label: string;
  path: string | undefined;
  helpText: string;
}

const props = defineProps<Props>();
</script>

<template>
  <NFormItem :path="props.path">
    <template #label>
      {{ props.label }}
      <NTooltip>
        <template #trigger>
          <span>
            <SvgIcon local-icon="mdi--question-mark-circle-outline" class="inline-block" />
          </span>
        </template>
        <span>{{ props.helpText }}</span>
      </NTooltip>
    </template>
    <slot></slot>
  </NFormItem>
</template>

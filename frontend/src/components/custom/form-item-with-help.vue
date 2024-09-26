<script setup lang="ts">
interface Props {
  label: string;
  path: string | undefined;
  helpText: string;
  helpLink?: string;
}

const props = defineProps<Props>();

const handleClick = () => {
  if (props.helpLink) {
    window.open(props.helpLink, '_blank');
  }
};
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
      <SvgIcon
        v-if="props.helpLink"
        local-icon="mdi--link-variant"
        class="inline-block cursor-pointer"
        @click="handleClick"
      />
    </template>
    <slot></slot>
  </NFormItem>
</template>

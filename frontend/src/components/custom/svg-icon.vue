<script setup lang="ts">
import { computed, useAttrs } from 'vue';

defineOptions({ name: 'SvgIcon', inheritAttrs: false });

/** Props */
interface Props {
  /** Local svg icon name */
  localIcon?: string;
  /** Click event handler */
  onClick?: (event: MouseEvent) => void;
}

const props = defineProps<Props>();

const attrs = useAttrs();

const bindAttrs = computed<{ class: string; style: string }>(() => ({
  class: (attrs.class as string) || '',
  style: (attrs.style as string) || ''
}));

const symbolId = computed(() => {
  const { VITE_ICON_LOCAL_PREFIX: prefix } = import.meta.env;

  const defaultLocalIcon = 'material-symbols--hide-image-outline-rounded';

  const icon = props.localIcon || defaultLocalIcon;

  return `#${prefix}-${icon}`;
});

const handleClick = (event: MouseEvent) => {
  if (props.onClick) {
    props.onClick(event);
  }
};
</script>

<template>
  <svg aria-hidden="true" width="1em" height="1em" v-bind="bindAttrs" @click="handleClick">
    <use :xlink:href="symbolId" fill="currentColor" />
  </svg>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent, ref, watch } from 'vue';
import { NSelect } from 'naive-ui';

interface Option {
  label: string;
  value: string;
  hint: string;
}

export default defineComponent({
  name: 'SelectWithHint',
  components: {
    NSelect
  },
  inheritAttrs: false,
  props: {
    options: {
      type: Array as PropType<Option[]>,
      required: true
    },
    modelValue: {
      type: [String, Number, null] as PropType<string | number | null>,
      required: true
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const selectedValue = ref<string | number | null>(props.modelValue);
    const hint = ref<string>('');

    const updateHint = (value: string | number | null) => {
      const selectedOption = props.options.find(option => option.value === value);
      hint.value = selectedOption ? selectedOption.hint : '';
      emit('update:modelValue', value);
    };

    updateHint(props.modelValue);

    watch(
      () => props.modelValue,
      newValue => {
        updateHint(newValue);
      }
    );

    return {
      selectedValue,
      hint,
      updateHint
    };
  }
});
</script>

<template>
  <div class="w-full flex flex-col items-center">
    <NSelect v-model:value="selectedValue" :options="options" v-bind="$attrs" @update:value="updateHint"></NSelect>
    <div v-if="hint" class="text-center text-gray-500 italic">
      <SvgIcon local-icon="mdi--information-outline" class="inline-block h-4 w-4" />
      {{ hint }}
    </div>
  </div>
</template>

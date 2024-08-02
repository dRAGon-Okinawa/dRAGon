<script setup lang="ts">
import { computed } from 'vue';
import { createReusableTemplate } from '@vueuse/core';
import { $t } from '@/locales';

defineOptions({
  name: 'JvmCards'
});

interface Props {
  numbers: Api.AppDashboard.Numbers;
}
const props = defineProps<Props>();

interface CardData {
  key: string;
  title: string;
  value: number;
  prefix?: string;
  suffix?: string;
  decimals?: number;
  color: {
    start: string;
    end: string;
  };
  icon: string;
}

const cardData = computed<CardData[]>(() => [
  {
    key: 'visitCount',
    title: $t('system.usedMemory'),
    value: props.numbers.usedMemoryPercentage,
    suffix: '%',
    color: {
      start: '#ec4786',
      end: '#b955a4'
    },
    icon: 'mdi--memory'
  },
  {
    key: 'turnover',
    title: $t('system.loadAverage'),
    value: props.numbers.systemLoadAverage,
    decimals: 2,
    color: {
      start: '#865ec0',
      end: '#5144b4'
    },
    icon: 'mdi--graph-bell-curve'
  },
  {
    key: 'downloadCount',
    title: $t('system.heapMemory'),
    value: props.numbers.heapMemoryUsagePercentage,
    suffix: '%',
    color: {
      start: '#56cdf3',
      end: '#719de3'
    },
    icon: 'mdi--layers-triple-outline'
  },
  {
    key: 'dealCount',
    title: $t('system.processors'),
    value: props.numbers.availableProcessors,
    color: {
      start: '#fcbc25',
      end: '#f68057'
    },
    icon: 'mdi--cpu-64-bit'
  }
]);

interface GradientBgProps {
  gradientColor: string;
}

const [DefineGradientBg, GradientBg] = createReusableTemplate<GradientBgProps>();

function getGradientColor(color: CardData['color']) {
  return `linear-gradient(to bottom right, ${color.start}, ${color.end})`;
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <!-- define component start: GradientBg -->
    <DefineGradientBg v-slot="{ $slots, gradientColor }">
      <div class="rd-8px px-16px pb-4px pt-8px text-white" :style="{ backgroundImage: gradientColor }">
        <component :is="$slots.default" />
      </div>
    </DefineGradientBg>
    <!-- define component end: GradientBg -->

    <NGrid cols="s:1 m:2 l:4" responsive="screen" :x-gap="16" :y-gap="16">
      <NGi v-for="item in cardData" :key="item.key">
        <GradientBg :gradient-color="getGradientColor(item.color)" class="flex-1">
          <h3 class="text-16px">{{ item.title }}</h3>
          <div class="flex justify-between pt-12px">
            <SvgIcon :local-icon="item.icon" class="text-32px" />
            <CountTo
              :prefix="item.prefix"
              :suffix="item.suffix"
              :start-value="1"
              :end-value="item.value"
              :decimals="item.decimals"
              class="text-30px text-white dark:text-dark"
            />
          </div>
        </GradientBg>
      </NGi>
    </NGrid>
  </NCard>
</template>

<style scoped></style>

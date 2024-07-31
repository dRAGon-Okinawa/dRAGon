<script setup lang="ts">
import { computed } from 'vue';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';

defineOptions({
  name: 'HeaderBanner'
});

interface Props {
  numbers: Api.AppDashboard.Numbers;
}
defineProps<Props>();

const appStore = useAppStore();

const gap = computed(() => (appStore.isMobile ? 0 : 16));
</script>

<template>
  <NCard :bordered="false" class="card-wrapper">
    <NGrid :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
      <NGi span="24 s:24 m:18">
        <div class="flex-y-center">
          <div class="size-72px shrink-0 overflow-hidden">
            <SvgIcon local-icon="mdi--monitor-dashboard" class="text-64px" />
          </div>
          <div class="pl-12px">
            <h3 class="text-18px font-semibold">
              {{ $t('dRAGon.dashboard') }}
            </h3>
            <p class="text-#999 leading-30px">{{ $t('page.home.weatherDesc') }}</p>
          </div>
        </div>
      </NGi>
      <NGi span="24 s:24 m:6">
        <NSpace :size="24" justify="end">
          <NStatistic :label="$t('dRAGon.farms')" :value="numbers.farms" />
          <NStatistic :label="$t('dRAGon.silos')" :value="numbers.silos" />
          <NStatistic :label="$t('dRAGon.documents')" :value="numbers.documents" />
        </NSpace>
      </NGi>
    </NGrid>
  </NCard>
</template>

<style scoped></style>

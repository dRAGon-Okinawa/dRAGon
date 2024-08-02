<script setup lang="ts">
import { onActivated, ref } from 'vue';
import { fetchApiAppDashboardGetNumbers } from '@/service/api';
import InfrastructureOverall from './modules/infrastructure-overall.vue';
import JvmCards from './modules/jvm-cards.vue';

const numbers = ref({} as Api.AppDashboard.Numbers);

const GetNumbers = async () => {
  const { error, data } = await fetchApiAppDashboardGetNumbers();
  if (!error) {
    numbers.value = data;
  }
};

onActivated(() => {
  GetNumbers();
});
</script>

<template>
  <NSpace vertical :size="16">
    <InfrastructureOverall :numbers="numbers" />
    <JvmCards :numbers="numbers" />
  </NSpace>
</template>

<style scoped></style>

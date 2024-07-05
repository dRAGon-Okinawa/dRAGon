<script setup lang="ts">
import { onActivated, onMounted } from 'vue';

onMounted(() => {
  // console.log('Help mounted');
});

onActivated(() => {
  // console.log('Help activated');
});
</script>

<template>
  <div class="h-full">
    <iframe id="iframePage" class="size-full" src="https://dragon.okinawa"></iframe>
  </div>
</template>

<style scoped>
iframe {
  border-radius: 10px;
}
</style>

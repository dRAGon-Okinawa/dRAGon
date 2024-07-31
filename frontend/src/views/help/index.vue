<script setup lang="ts">
import { onActivated, onBeforeUnmount, onMounted, ref } from 'vue';

const helpFrameRef = ref();

onMounted(() => {
  // console.log('Help mounted', helpFrameRef.value);
});

onActivated(() => {
  // console.log('Help activated', helpFrameRef.value);
});

onBeforeUnmount(() => {
  // console.log('Help unmounted', helpFrameRef.value);
});
</script>

<template>
  <div class="h-full">
    <iframe id="helpFrame" ref="helpFrameRef" class="size-full" src="https://dragon.okinawa"></iframe>
  </div>
</template>

<style scoped>
iframe {
  border-radius: 10px;
}
</style>

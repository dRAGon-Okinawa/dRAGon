<script setup>
import { useLayout } from '@/layout/composables/layout';
import { computed } from 'vue';

const { layoutConfig } = useLayout();

const logoUrl = computed(() => {
    return `/images/${layoutConfig.darkTheme.value ? 'dragon_okinawa_icon' : 'dragon_okinawa_icon'}.png`;
});
</script>

<template>
    <div class="layout-footer">
        <img :src="logoUrl" alt="Logo" height="20" class="mr-2" />
        by
        <span class="font-medium ml-2">PrimeVue</span>
    </div>
</template>
<style lang="scss" scoped></style>

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
        <img :src="logoUrl" alt="dRAGon" height="20" class="mr-1" />
        <span class="font-medium mr-1">dRAGon</span>
        <span>&copy; 2024</span>
    </div>
</template>
<style lang="scss" scoped></style>

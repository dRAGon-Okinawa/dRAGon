<script setup>
import { ref } from 'vue';
import { computed } from 'vue';
import { usePrimeVue } from 'primevue/config';

const currentTheme = ref('aura-light-purple');

const isDark = computed(() => {
    return currentTheme.value === 'aura-dark-purple';
});

const PrimeVue = usePrimeVue();

const toggleTheme = () => {
    let newTheme = currentTheme.value === 'aura-light-purple' ? 'aura-dark-purple' : 'aura-light-purple';
    PrimeVue.changeTheme(currentTheme.value, newTheme, 'theme-css', () => {});
    currentTheme.value = newTheme;
};

const switchTitle = computed(() => {
    return isDark.value ? 'message.light_theme' : 'message.dark_theme';
});
</script>

<template>
    <InputSwitch v-model="isDark" @click="toggleTheme" :title="$t(switchTitle)" />
</template>

<style scoped>

</style>

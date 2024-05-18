<script setup>
import { ref, onMounted } from 'vue';
import { computed } from 'vue';
import { usePrimeVue } from 'primevue/config';

const PrimeVue = usePrimeVue();
const currentTheme = ref('aura-light-purple');

const isDark = computed(() => {
    return currentTheme.value === 'aura-dark-purple';
});

onMounted(() => {
    if (localStorage.getItem('darkMode') == 'true') {
        applyTheme('aura-dark-purple');
    } else {
        applyTheme('aura-light-purple');
    }
});

const applyTheme = (theme) => {
    PrimeVue.changeTheme(currentTheme.value, theme, 'theme-css', () => {});
    currentTheme.value = theme;
    localStorage.setItem('darkMode', isDark.value);
};

const toggleTheme = () => {
    applyTheme(isDark.value ? 'aura-light-purple' : 'aura-dark-purple');
};

const switchTitle = computed(() => {
    return isDark.value ? 'message.light_theme' : 'message.dark_theme';
});
</script>

<template>
    <InputSwitch v-model="isDark" @click="toggleTheme" :title="$t(switchTitle)" class="inputswitch-theme" />
</template>

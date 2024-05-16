import { createApp } from 'vue';
import { createI18n } from 'vue-i18n';

import App from './App.vue';
import router from './router';

import PrimeVue from 'primevue/config';
import BadgeDirective from 'primevue/badgedirective';
import ConfirmationService from 'primevue/confirmationservice';
import DialogService from 'primevue/dialogservice';
import Ripple from 'primevue/ripple';
import StyleClass from 'primevue/styleclass';
import ToastService from 'primevue/toastservice';
import Tooltip from 'primevue/tooltip';
import InputSwitch from 'primevue/inputswitch';

import ThemeSwitch from '@/components/ThemeSwitch.vue';

import '@/assets/styles.scss';

const app = createApp(App);

// Import translations :
import en from './locales/en.json';
import fr from './locales/fr.json';
const i18n = createI18n({
    fallbackLocale: 'en',
    locale: navigator.language.split('-')[0] || 'en',
    messages: { en, fr }
});
app.use(i18n);

app.use(router);
app.use(PrimeVue, { ripple: true });
app.use(ToastService);
app.use(DialogService);
app.use(ConfirmationService);

// PrimeVue components :
app.component('InputSwitch', InputSwitch);

// Custom components :
app.component('ThemeSwitch', ThemeSwitch);

app.directive('tooltip', Tooltip);
app.directive('badge', BadgeDirective);
app.directive('ripple', Ripple);
app.directive('styleclass', StyleClass);

app.mount('#app');

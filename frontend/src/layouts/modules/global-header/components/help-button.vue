<script setup lang="ts">
import { $t } from '@/locales';
import { useRouterPush } from '@/hooks/common/router';

defineOptions({
  name: 'HelpButton'
});

const { routerPushByKey } = useRouterPush();

function showHelp() {
  routerPushByKey('help');
}
</script>

<template>
  <ButtonIcon local-icon="majesticons--question-circle-line" :tooltip-content="$t('common.help')" @click="showHelp" />
</template>

<style scoped></style>

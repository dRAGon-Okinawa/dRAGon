import { dateEnUS, enUS } from 'naive-ui';
import type { NDateLocale, NLocale } from 'naive-ui';

export const naiveLocales: Record<App.I18n.LangType, NLocale> = {
  'en-US': enUS
};

export const naiveDateLocales: Record<App.I18n.LangType, NDateLocale> = {
  'en-US': dateEnUS
};

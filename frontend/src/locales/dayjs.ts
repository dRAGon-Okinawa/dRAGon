import { locale } from 'dayjs';
import 'dayjs/locale/en';
import { localStg } from '@/utils/storage';

/**
 * Set dayjs locale
 *
 * @param lang
 */
export function setDayjsLocale(lang: App.I18n.LangType = 'en-US') {
  const localMap = {
    'en-US': 'en'
  } satisfies Record<App.I18n.LangType, string>;

  const l = lang || localStg.get('lang') || 'en-US';

  locale(localMap[l]);
}

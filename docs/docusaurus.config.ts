import { themes as prismThemes } from 'prism-react-renderer';
import type { Config } from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const config: Config = {
  title: 'dRAGon',
  tagline: 'Dynamic Retrieval Augmented Generation for Optimized Nimble',
  favicon: '/favicon.ico',
  url: 'https://dragon.okinawa',
  baseUrl: '/',
  organizationName: 'isontheline',
  projectName: 'dRAGon',
  deploymentBranch: 'gh-pages',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'throw',
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },
  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
          showLastUpdateTime: true,
        },
        theme: {
          customCss: './src/css/custom.css',
        },
        pages: {
          showLastUpdateTime: true,
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    image: 'img/dragon_okinawa.jpg',
    navbar: {
      title: 'dRAGon',
      logo: {
        alt: 'dRAGon',
        src: 'img/dragon_okinawa_icon.png',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'left',
          label: 'Getting Started',
        },
        {
          href: 'https://github.com/isontheline/dRAGon',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'Getting Started',
              to: '/docs/getting-started',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Discussions',
              href: 'https://github.com/isontheLine/dRAGon/discussions',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'GitHub',
              href: 'https://github.com/isontheline/dRAGon',
            },
          ],
        },
      ],
      copyright: `&copy; ${new Date().getFullYear()} All dRAGon Contributors`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
  plugins: [
    'docusaurus-lunr-search'
  ]
};

export default config;

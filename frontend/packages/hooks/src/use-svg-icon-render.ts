import { h } from 'vue';
import type { Component } from 'vue';

/**
 * Svg icon render hook
 *
 * @param SvgIcon Svg icon component
 */
export default function useSvgIconRender(SvgIcon: Component) {
  interface IconConfig {
    /** Local icon name */
    localIcon?: string;
    /** Icon color */
    color?: string;
    /** Icon size */
    fontSize?: number;
  }

  type IconStyle = Partial<Pick<CSSStyleDeclaration, 'color' | 'fontSize'>>;

  /**
   * Svg icon VNode
   *
   * @param config
   */
  const SvgIconVNode = (config: IconConfig) => {
    const { color, fontSize, localIcon } = config;

    const style: IconStyle = {};

    if (color) {
      style.color = color;
    }
    if (fontSize) {
      style.fontSize = `${fontSize}px`;
    }

    if (!localIcon) {
      return undefined;
    }

    return () => h(SvgIcon, { localIcon, style });
  };

  return {
    SvgIconVNode
  };
}

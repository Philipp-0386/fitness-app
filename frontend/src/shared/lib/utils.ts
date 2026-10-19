import { createCn } from 'cn/config';

export const cn = createCn({
  extend: {
    theme: {
      text: [
        'display',
        'headline',
        'title',
        'body',
        'meta',
        'caption',
        'label',
        'button',
        'input',
      ],
      spacing: ['tap', 'tap-primary'],
      radius: ['field', 'card', 'sheet'],
      container: ['app'],
    },
  },
});

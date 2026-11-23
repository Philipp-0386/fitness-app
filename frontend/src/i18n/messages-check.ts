import de from '../../messages/de.json';
import en from '../../messages/en.json';

// Compile time check only. fails when both jsons keys arent identical
export const deHasAllKeys: typeof en = de;
export const deHasNoExtraKeys: typeof de = en;

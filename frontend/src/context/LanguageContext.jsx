import React, { createContext, useContext, useMemo, useState } from 'react';
import { LANGUAGES, translate } from '../lib/i18n';

const LanguageContext = createContext(null);

const STORAGE_KEY = 'hostelspace-language';

const getInitialLanguage = () => {
  if (typeof window === 'undefined') return 'en';
  const stored = window.localStorage.getItem(STORAGE_KEY);
  if (stored && LANGUAGES.some((l) => l.code === stored)) return stored;
  return 'en';
};

export const LanguageProvider = ({ children }) => {
  const [language, setLanguageState] = useState(getInitialLanguage);

  const setLanguage = (code) => {
    setLanguageState(code);
    window.localStorage.setItem(STORAGE_KEY, code);
  };

  const t = useMemo(() => (key, vars) => translate(language, key, vars), [language]);

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t, languages: LANGUAGES }}>
      {children}
    </LanguageContext.Provider>
  );
};

export const useLanguage = () => {
  const ctx = useContext(LanguageContext);
  if (!ctx) throw new Error('useLanguage must be used within a LanguageProvider');
  return ctx;
};

import React, { createContext, useContext, useMemo, useState } from 'react';

type Locale = 'en' | 'el';
type Dict = Record<string, string>;

const messages: Record<Locale, Dict> = {
  en: {
    'nav.toggle': 'Toggle navigation',
    'nav.home': 'Home',
    'nav.users': 'Users',
    'nav.language': 'Language',
    'nav.userMenu': 'User menu',
    'nav.manageUsers': 'Manage users',
    'auth.signIn': 'Sign in',
    'auth.signOut': 'Sign out',

    'home.title': 'Home',
    'home.welcome': 'Welcome back.',
    'home.pleaseLogin': 'Please sign in to access protected pages.',
    'home.profile': 'Your profile',

    'users.title': 'User management',
    'users.table.caption': 'List of users',
    'users.col.id': 'ID',
    'users.col.username': 'Username',
    'users.col.email': 'Email',
    'users.col.status': 'Status',
    'users.col.actions': 'Actions',
    'users.enabled': 'Enabled',
    'users.disabled': 'Disabled',
    'users.view': 'View',
    'users.edit': 'Edit',
    'users.delete': 'Delete',
    'users.create': 'Create user',
    'users.confirm.delete.title': 'Delete user?',
    'users.confirm.delete.body': 'This action cannot be undone.',
    'users.deleted.toast': 'User deleted.',

    'user.view.title': 'User details',
    'user.edit.title': 'Edit user',
    'user.save': 'Save',
    'user.cancel': 'Cancel',
    'user.confirm.save.title': 'Save changes?',
    'user.confirm.save.body': 'Confirm saving these changes.',
    'user.saved.toast': 'User saved.',
    'user.notFound': 'User not found.',

    'form.username': 'Username',
    'form.email': 'Email',
    'form.status': 'Status',
    'form.enabled': 'Enabled',
  },
  el: {
    'nav.toggle': 'Εναλλαγή πλοήγησης',
    'nav.home': 'Αρχική',
    'nav.users': 'Χρήστες',
    'nav.language': 'Γλώσσα',
    'nav.userMenu': 'Μενού χρήστη',
    'nav.manageUsers': 'Διαχείριση χρηστών',
    'auth.signIn': 'Σύνδεση',
    'auth.signOut': 'Αποσύνδεση',

    'home.title': 'Αρχική',
    'home.welcome': 'Καλωσήρθες ξανά.',
    'home.pleaseLogin': 'Συνδέσου για πρόσβαση στις προστατευμένες σελίδες.',
    'home.profile': 'Το προφίλ σου',

    'users.title': 'Διαχείριση χρηστών',
    'users.table.caption': 'Λίστα χρηστών',
    'users.col.id': 'ID',
    'users.col.username': 'Όνομα χρήστη',
    'users.col.email': 'Email',
    'users.col.status': 'Κατάσταση',
    'users.col.actions': 'Ενέργειες',
    'users.enabled': 'Ενεργός',
    'users.disabled': 'Ανενεργός',
    'users.view': 'Προβολή',
    'users.edit': 'Επεξεργασία',
    'users.delete': 'Διαγραφή',
    'users.create': 'Δημιουργία χρήστη',
    'users.confirm.delete.title': 'Διαγραφή χρήστη;',
    'users.confirm.delete.body': 'Η ενέργεια δεν μπορεί να αναιρεθεί.',
    'users.deleted.toast': 'Ο χρήστης διαγράφηκε.',

    'user.view.title': 'Λεπτομέρειες χρήστη',
    'user.edit.title': 'Επεξεργασία χρήστη',
    'user.save': 'Αποθήκευση',
    'user.cancel': 'Άκυρο',
    'user.confirm.save.title': 'Αποθήκευση αλλαγών;',
    'user.confirm.save.body': 'Επιβεβαίωσε την αποθήκευση των αλλαγών.',
    'user.saved.toast': 'Ο χρήστης αποθηκεύτηκε.',
    'user.notFound': 'Ο χρήστης δεν βρέθηκε.',

    'form.username': 'Όνομα χρήστη',
    'form.email': 'Email',
    'form.status': 'Κατάσταση',
    'form.enabled': 'Ενεργός',
  },
};

type I18nContextType = {
  locale: Locale;
  setLocale: (loc: Locale) => void;
  t: (key: string) => string;
};

const I18nContext = createContext<I18nContextType | undefined>(undefined);

export function I18nProvider({ children }: { children: React.ReactNode }) {
  const [locale, setLocale] = useState<Locale>(
    (localStorage.getItem('locale') as Locale) || 'en'
  );

  const t = (key: string) => messages[locale][key] ?? key;

  const value = useMemo(
    () => ({
      locale,
      setLocale: (loc: Locale) => {
        localStorage.setItem('locale', loc);
        setLocale(loc);
      },
      t,
    }),
    [locale]
  );

  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}

export function useI18n() {
  const ctx = useContext(I18nContext);
  if (!ctx) throw new Error('useI18n must be used within I18nProvider');
  return ctx;
}

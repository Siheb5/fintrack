// App.tsx — the ROOT component.
//
// It wraps the whole app in two providers:
//   - SafeAreaProvider: keeps content clear of notches / status bars
//   - AuthProvider: makes login state available to every screen
// and then renders the RootNavigator, which chooses Login vs. the main app.

import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { AuthProvider } from './src/auth/AuthContext';
import { RootNavigator } from './src/navigation/RootNavigator';

export default function App() {
  return (
    <SafeAreaProvider>
      <AuthProvider>
        <RootNavigator />
        <StatusBar style="auto" />
      </AuthProvider>
    </SafeAreaProvider>
  );
}

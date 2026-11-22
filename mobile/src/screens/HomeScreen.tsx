// TEMPORARY Home screen (placeholder for step 4b).
// Confirms login worked and provides logout. Replaced by the real dashboard
// + transactions list in steps 4c/4d.

import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { AppButton } from '../components/AppButton';
import { useAuth } from '../auth/AuthContext';
import { colors, spacing } from '../theme';

export function HomeScreen() {
  const { email, logout } = useAuth();
  return (
    <View style={styles.container}>
      <Text style={styles.title}>You're logged in 🎉</Text>
      {email ? <Text style={styles.email}>{email}</Text> : null}
      <Text style={styles.hint}>Dashboard & transactions coming in the next step.</Text>
      <AppButton title="Log out" onPress={logout} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background, padding: spacing.lg, justifyContent: 'center' },
  title: { fontSize: 24, fontWeight: 'bold', color: colors.text, textAlign: 'center' },
  email: { fontSize: 16, color: colors.primary, textAlign: 'center', marginTop: spacing.sm },
  hint: { fontSize: 13, color: colors.textMuted, textAlign: 'center', marginTop: spacing.lg },
});

// The Login screen. Collects email + password, calls the auth context's
// login(), and on success the app automatically switches to the main screens
// (because the navigator reacts to the token appearing).

import React, { useState } from 'react';
import { Alert, StyleSheet, Text, View } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { AppButton } from '../components/AppButton';
import { AppTextInput } from '../components/AppTextInput';
import { useAuth } from '../auth/AuthContext';
import { colors, spacing } from '../theme';
import type { RootStackParamList } from '../navigation/types';

type Props = NativeStackScreenProps<RootStackParamList, 'Login'>;

export function LoginScreen({ navigation }: Props) {
  // Each field is a controlled input backed by state.
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { login } = useAuth();

  async function onSubmit() {
    if (!email || !password) {
      Alert.alert('Missing info', 'Please enter your email and password.');
      return;
    }
    try {
      setSubmitting(true);
      await login(email.trim(), password);
      // No manual navigation needed — the navigator shows Home once logged in.
    } catch (e: any) {
      const msg = e?.response?.data?.message ?? 'Login failed. Check your credentials.';
      Alert.alert('Login failed', msg);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.logo}>FinTrack 💸</Text>
      <Text style={styles.subtitle}>Welcome back</Text>

      <AppTextInput
        label="Email"
        value={email}
        onChangeText={setEmail}
        autoCapitalize="none"
        keyboardType="email-address"
        placeholder="you@example.com"
      />
      <AppTextInput
        label="Password"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
        placeholder="••••••••"
      />

      <AppButton title="Log in" onPress={onSubmit} loading={submitting} />

      <Text style={styles.link} onPress={() => navigation.navigate('Register')}>
        No account? Create one
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background, padding: spacing.lg, justifyContent: 'center' },
  logo: { fontSize: 34, fontWeight: 'bold', color: colors.primary, textAlign: 'center' },
  subtitle: { fontSize: 16, color: colors.textMuted, textAlign: 'center', marginTop: spacing.xs },
  link: { color: colors.primary, textAlign: 'center', marginTop: spacing.lg, fontSize: 15 },
});

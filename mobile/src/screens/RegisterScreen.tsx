// The Register screen. Very similar to Login, but calls register(). On success
// the user is logged in immediately (the backend returns a token on register).

import React, { useState } from 'react';
import { Alert, StyleSheet, Text, View } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { AppButton } from '../components/AppButton';
import { AppTextInput } from '../components/AppTextInput';
import { useAuth } from '../auth/AuthContext';
import { colors, spacing } from '../theme';
import type { RootStackParamList } from '../navigation/types';

type Props = NativeStackScreenProps<RootStackParamList, 'Register'>;

export function RegisterScreen({ navigation }: Props) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { register } = useAuth();

  async function onSubmit() {
    if (password.length < 8) {
      Alert.alert('Weak password', 'Password must be at least 8 characters.');
      return;
    }
    try {
      setSubmitting(true);
      await register(email.trim(), password);
    } catch (e: any) {
      const msg = e?.response?.data?.message ?? 'Registration failed.';
      Alert.alert('Registration failed', msg);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.logo}>Create account</Text>

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
        placeholder="at least 8 characters"
      />

      <AppButton title="Sign up" onPress={onSubmit} loading={submitting} />

      <Text style={styles.link} onPress={() => navigation.navigate('Login')}>
        Already have an account? Log in
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background, padding: spacing.lg, justifyContent: 'center' },
  logo: { fontSize: 30, fontWeight: 'bold', color: colors.primary, textAlign: 'center', marginBottom: spacing.sm },
  link: { color: colors.primary, textAlign: 'center', marginTop: spacing.lg, fontSize: 15 },
});

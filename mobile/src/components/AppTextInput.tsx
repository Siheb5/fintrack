// A reusable labeled text field. It's a "controlled input": its value comes
// from state in the parent, and onChangeText reports every keystroke back.

import React from 'react';
import { StyleSheet, Text, TextInput, View, TextInputProps } from 'react-native';
import { colors, spacing } from '../theme';

interface Props extends TextInputProps {
  label: string;
}

export function AppTextInput({ label, style, ...rest }: Props) {
  return (
    <View style={styles.wrap}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        style={[styles.input, style]}
        placeholderTextColor={colors.textMuted}
        {...rest}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { marginTop: spacing.md },
  label: { color: colors.text, marginBottom: spacing.xs, fontSize: 14, fontWeight: '500' },
  input: {
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 10,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm + 2,
    fontSize: 16,
    color: colors.text,
  },
});

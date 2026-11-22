// Add OR edit a transaction. If a route param `id` is present, it loads that
// transaction and pre-fills the form (edit mode) and shows a Delete button.
// Otherwise it starts blank (add mode).

import React, { useEffect, useState } from 'react';
import { Alert, Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { AppButton } from '../components/AppButton';
import { AppTextInput } from '../components/AppTextInput';
import {
  createTransaction,
  deleteTransaction,
  getTransaction,
  updateTransaction,
} from '../api/transactions';
import { TransactionType } from '../types';
import { todayIso } from '../format';
import { colors, spacing } from '../theme';
import type { RootStackParamList } from '../navigation/types';

type Props = NativeStackScreenProps<RootStackParamList, 'AddTransaction'>;

export function AddTransactionScreen({ navigation, route }: Props) {
  const editingId = route.params?.id;
  const isEditing = editingId != null;

  const [amount, setAmount] = useState('');
  const [type, setType] = useState<TransactionType>('EXPENSE');
  const [category, setCategory] = useState('');
  const [note, setNote] = useState('');
  const [occurredOn, setOccurredOn] = useState(todayIso());
  const [saving, setSaving] = useState(false);

  // In edit mode, load the existing transaction and fill the fields.
  useEffect(() => {
    if (!isEditing) return;
    (async () => {
      try {
        const t = await getTransaction(editingId!);
        setAmount(String(t.amount));
        setType(t.type);
        setCategory(t.category);
        setNote(t.note ?? '');
        setOccurredOn(t.occurredOn);
      } catch {
        Alert.alert('Error', 'Could not load this transaction.');
        navigation.goBack();
      }
    })();
  }, [editingId, isEditing, navigation]);

  // Keep the header title and (in edit mode) a Delete action in sync.
  useEffect(() => {
    navigation.setOptions({
      title: isEditing ? 'Edit transaction' : 'Add transaction',
      headerRight: isEditing
        ? () => (
            <Pressable onPress={onDelete} hitSlop={10}>
              <Text style={styles.deleteHeader}>Delete</Text>
            </Pressable>
          )
        : undefined,
    });
  });

  async function onSave() {
    const parsed = Number(amount.replace(',', '.'));
    if (!parsed || parsed <= 0) {
      Alert.alert('Invalid amount', 'Enter an amount greater than 0.');
      return;
    }
    if (!category.trim()) {
      Alert.alert('Missing category', 'Please enter a category.');
      return;
    }
    const input = {
      amount: parsed,
      type,
      category: category.trim(),
      note: note.trim() || undefined,
      occurredOn,
    };
    try {
      setSaving(true);
      if (isEditing) {
        await updateTransaction(editingId!, input);
      } else {
        await createTransaction(input);
      }
      navigation.goBack(); // list reloads on focus
    } catch (e: any) {
      const msg = e?.response?.data?.message ?? 'Could not save. Try again.';
      Alert.alert('Save failed', msg);
    } finally {
      setSaving(false);
    }
  }

  function onDelete() {
    Alert.alert('Delete transaction', 'This cannot be undone.', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: async () => {
          try {
            await deleteTransaction(editingId!);
            navigation.goBack();
          } catch {
            Alert.alert('Error', 'Could not delete.');
          }
        },
      },
    ]);
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ padding: spacing.lg }}>
      {/* Income / Expense toggle */}
      <View style={styles.toggle}>
        {(['EXPENSE', 'INCOME'] as TransactionType[]).map((t) => {
          const active = type === t;
          return (
            <Pressable
              key={t}
              onPress={() => setType(t)}
              style={[
                styles.toggleBtn,
                active && { backgroundColor: t === 'INCOME' ? colors.income : colors.expense },
              ]}
            >
              <Text style={[styles.toggleText, active && styles.toggleTextActive]}>
                {t === 'INCOME' ? 'Income' : 'Expense'}
              </Text>
            </Pressable>
          );
        })}
      </View>

      <AppTextInput
        label="Amount (€)"
        value={amount}
        onChangeText={setAmount}
        keyboardType="decimal-pad"
        placeholder="0.00"
      />
      <AppTextInput
        label="Category"
        value={category}
        onChangeText={setCategory}
        placeholder="Food, Rent, Salary…"
      />
      <AppTextInput
        label="Note (optional)"
        value={note}
        onChangeText={setNote}
        placeholder="e.g. Lunch with team"
      />
      <AppTextInput
        label="Date (YYYY-MM-DD)"
        value={occurredOn}
        onChangeText={setOccurredOn}
        placeholder="2026-11-20"
        autoCapitalize="none"
      />

      <AppButton title={isEditing ? 'Save changes' : 'Add transaction'} onPress={onSave} loading={saving} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  toggle: { flexDirection: 'row', gap: spacing.sm },
  toggleBtn: {
    flex: 1,
    paddingVertical: spacing.md,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: 'center',
    backgroundColor: colors.card,
  },
  toggleText: { fontSize: 15, fontWeight: '600', color: colors.textMuted },
  toggleTextActive: { color: '#fff' },
  deleteHeader: { color: colors.expense, fontSize: 15, fontWeight: '600' },
});

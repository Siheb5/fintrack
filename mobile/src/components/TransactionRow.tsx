// One row in the transactions list. Shows category + note on the left, the
// amount on the right (green for income, red for expense), and the date below.
// Tapping it calls onPress (used to open the edit form).

import React from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Transaction } from '../types';
import { colors, spacing } from '../theme';
import { formatDate, formatMoney } from '../format';

interface Props {
  transaction: Transaction;
  onPress: () => void;
}

export function TransactionRow({ transaction, onPress }: Props) {
  const isIncome = transaction.type === 'INCOME';
  const sign = isIncome ? '+' : '−';
  const amountColor = isIncome ? colors.income : colors.expense;

  return (
    <Pressable style={styles.row} onPress={onPress}>
      <View style={styles.left}>
        <Text style={styles.category}>{transaction.category}</Text>
        {transaction.note ? <Text style={styles.note}>{transaction.note}</Text> : null}
        <Text style={styles.date}>{formatDate(transaction.occurredOn)}</Text>
      </View>
      <Text style={[styles.amount, { color: amountColor }]}>
        {sign}
        {formatMoney(transaction.amount)}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    backgroundColor: colors.card,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: spacing.md,
    borderRadius: 12,
    marginBottom: spacing.sm,
    borderWidth: 1,
    borderColor: colors.border,
  },
  left: { flex: 1, paddingRight: spacing.sm },
  category: { fontSize: 16, fontWeight: '600', color: colors.text },
  note: { fontSize: 13, color: colors.textMuted, marginTop: 2 },
  date: { fontSize: 12, color: colors.textMuted, marginTop: 4 },
  amount: { fontSize: 16, fontWeight: '700' },
});

// The dashboard shown at the top of the Home screen: balance, income/expense
// totals, and a donut chart of spending by category. It receives the summary
// as a prop (the parent screen fetches it) so this component stays purely about
// presentation.

import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { PieChart } from 'react-native-gifted-charts';
import { Summary } from '../types';
import { colors, spacing } from '../theme';
import { formatMoney } from '../format';

// A small palette; each category slice gets the next color, cycling if needed.
const PALETTE = ['#2563eb', '#16a34a', '#f59e0b', '#db2777', '#7c3aed', '#0891b2', '#dc2626', '#65a30d'];

interface Props {
  summary: Summary | null;
}

export function SummaryHeader({ summary }: Props) {
  if (!summary) return null;

  // Map the server's byCategory list into the chart's expected slice shape.
  const slices = summary.byCategory.map((c, i) => ({
    value: c.total,
    color: PALETTE[i % PALETTE.length],
    text: c.category,
  }));

  const hasExpenses = slices.length > 0;

  return (
    <View style={styles.wrap}>
      {/* Balance card */}
      <View style={styles.balanceCard}>
        <Text style={styles.balanceLabel}>Balance</Text>
        <Text style={styles.balanceValue}>{formatMoney(summary.balance)}</Text>

        <View style={styles.totalsRow}>
          <View style={styles.totalBox}>
            <Text style={styles.totalLabel}>Income</Text>
            <Text style={[styles.totalValue, { color: colors.income }]}>
              {formatMoney(summary.totalIncome)}
            </Text>
          </View>
          <View style={styles.totalBox}>
            <Text style={styles.totalLabel}>Expense</Text>
            <Text style={[styles.totalValue, { color: colors.expense }]}>
              {formatMoney(summary.totalExpense)}
            </Text>
          </View>
        </View>
      </View>

      {/* Spending-by-category chart */}
      <View style={styles.chartCard}>
        <Text style={styles.sectionTitle}>Spending by category</Text>

        {hasExpenses ? (
          <>
            <View style={styles.chartRow}>
              <PieChart
                data={slices}
                donut
                radius={80}
                innerRadius={48}
                centerLabelComponent={() => (
                  <Text style={styles.chartCenter}>{formatMoney(summary.totalExpense)}</Text>
                )}
              />
              {/* Legend */}
              <View style={styles.legend}>
                {slices.map((s) => (
                  <View key={s.text} style={styles.legendItem}>
                    <View style={[styles.legendDot, { backgroundColor: s.color }]} />
                    <Text style={styles.legendText} numberOfLines={1}>
                      {s.text} · {formatMoney(s.value)}
                    </Text>
                  </View>
                ))}
              </View>
            </View>
          </>
        ) : (
          <Text style={styles.emptyChart}>Add some expenses to see your breakdown.</Text>
        )}
      </View>

      <Text style={styles.listTitle}>Recent transactions</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { marginBottom: spacing.sm },
  balanceCard: {
    backgroundColor: colors.primary,
    borderRadius: 16,
    padding: spacing.lg,
  },
  balanceLabel: { color: '#dbeafe', fontSize: 14 },
  balanceValue: { color: '#fff', fontSize: 34, fontWeight: 'bold', marginTop: spacing.xs },
  totalsRow: { flexDirection: 'row', gap: spacing.md, marginTop: spacing.md },
  totalBox: {
    flex: 1,
    backgroundColor: 'rgba(255,255,255,0.15)',
    borderRadius: 12,
    padding: spacing.md,
  },
  totalLabel: { color: '#e0e7ff', fontSize: 13 },
  totalValue: { fontSize: 18, fontWeight: '700', marginTop: 2, color: '#fff' },

  chartCard: {
    backgroundColor: colors.card,
    borderRadius: 16,
    padding: spacing.lg,
    marginTop: spacing.md,
    borderWidth: 1,
    borderColor: colors.border,
  },
  sectionTitle: { fontSize: 16, fontWeight: '700', color: colors.text, marginBottom: spacing.md },
  chartRow: { flexDirection: 'row', alignItems: 'center', gap: spacing.lg },
  chartCenter: { fontSize: 13, fontWeight: '700', color: colors.text },
  legend: { flex: 1 },
  legendItem: { flexDirection: 'row', alignItems: 'center', marginBottom: spacing.sm },
  legendDot: { width: 12, height: 12, borderRadius: 6, marginRight: spacing.sm },
  legendText: { fontSize: 13, color: colors.text, flexShrink: 1 },
  emptyChart: { color: colors.textMuted, fontSize: 14, textAlign: 'center', paddingVertical: spacing.md },

  listTitle: { fontSize: 16, fontWeight: '700', color: colors.text, marginTop: spacing.lg, marginBottom: spacing.sm },
});

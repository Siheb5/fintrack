// The transactions list. Fetches from the backend when focused, shows
// loading / empty / error states, supports pull-to-refresh, and lets you tap a
// row to edit or the "+" button to add.

import React, { useCallback, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  RefreshControl,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { TransactionRow } from '../components/TransactionRow';
import { listTransactions } from '../api/transactions';
import { Transaction } from '../types';
import { colors, spacing } from '../theme';
import type { RootStackParamList } from '../navigation/types';

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;

export function TransactionsScreen({ navigation }: Props) {
  const [items, setItems] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      setError(null);
      const data = await listTransactions();
      setItems(data);
    } catch (e: any) {
      setError('Could not load transactions. Is the backend running?');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  // Reload every time the screen comes into focus (e.g. after adding one).
  useFocusEffect(
    useCallback(() => {
      load();
    }, [load])
  );

  function onRefresh() {
    setRefreshing(true);
    load();
  }

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {error ? <Text style={styles.error}>{error}</Text> : null}

      <FlatList
        data={items}
        keyExtractor={(t) => String(t.id)}
        renderItem={({ item }) => (
          <TransactionRow
            transaction={item}
            onPress={() => navigation.navigate('AddTransaction', { id: item.id })}
          />
        )}
        contentContainerStyle={styles.listContent}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        ListEmptyComponent={
          <View style={styles.center}>
            <Text style={styles.emptyTitle}>No transactions yet</Text>
            <Text style={styles.emptyHint}>Tap “+” to add your first one.</Text>
          </View>
        }
      />

      {/* Floating add button */}
      <Pressable style={styles.fab} onPress={() => navigation.navigate('AddTransaction')}>
        <Text style={styles.fabText}>+</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  listContent: { padding: spacing.md, flexGrow: 1 },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: spacing.xl },
  error: { color: colors.expense, textAlign: 'center', padding: spacing.sm },
  emptyTitle: { fontSize: 18, fontWeight: '600', color: colors.text },
  emptyHint: { fontSize: 14, color: colors.textMuted, marginTop: spacing.xs },
  fab: {
    position: 'absolute',
    right: spacing.lg,
    bottom: spacing.lg,
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
    shadowColor: '#000',
    shadowOpacity: 0.2,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },
  fabText: { color: '#fff', fontSize: 32, lineHeight: 36 },
});

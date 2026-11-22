// Transaction-related API calls. Each maps to a backend endpoint from Phase 3c.
// The JWT is attached automatically by the axios interceptor, so these calls
// stay focused on just the data.

import { api } from './client';
import { Summary, Transaction, TransactionInput } from '../types';

export async function listTransactions(): Promise<Transaction[]> {
  const res = await api.get<Transaction[]>('/api/transactions');
  return res.data;
}

export async function getTransaction(id: number): Promise<Transaction> {
  const res = await api.get<Transaction>(`/api/transactions/${id}`);
  return res.data;
}

export async function createTransaction(input: TransactionInput): Promise<Transaction> {
  const res = await api.post<Transaction>('/api/transactions', input);
  return res.data;
}

export async function updateTransaction(id: number, input: TransactionInput): Promise<Transaction> {
  const res = await api.put<Transaction>(`/api/transactions/${id}`, input);
  return res.data;
}

export async function deleteTransaction(id: number): Promise<void> {
  await api.delete(`/api/transactions/${id}`);
}

export async function getSummary(): Promise<Summary> {
  const res = await api.get<Summary>('/api/transactions/summary');
  return res.data;
}

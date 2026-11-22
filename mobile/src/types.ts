// Shared TypeScript types used across the app. Keeping them in one place means
// every screen agrees on the exact shape of the data (and the editor catches
// mistakes before the app runs).

/** A transaction is either money coming in or going out. */
export type TransactionType = 'INCOME' | 'EXPENSE';

/** One transaction, exactly as the backend returns it. */
export interface Transaction {
  id: number;
  amount: number;
  type: TransactionType;
  category: string;
  note: string | null;
  occurredOn: string;   // ISO date, e.g. "2026-11-20"
  createdAt: string;    // ISO timestamp
}

/** The body we send when creating or updating a transaction. */
export interface TransactionInput {
  amount: number;
  type: TransactionType;
  category: string;
  note?: string;
  occurredOn: string;
}

/** The dashboard summary returned by /api/transactions/summary. */
export interface Summary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  byCategory: { category: string; total: number }[];
}

/** What the auth endpoints return after register/login. */
export interface AuthResult {
  token: string;
  email: string;
}

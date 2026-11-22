// Small formatting helpers used across screens, so money and dates look
// consistent everywhere.

/** Format a number as euro currency, e.g. 1234.5 -> "€1,234.50". */
export function formatMoney(amount: number): string {
  return new Intl.NumberFormat('en-IE', {
    style: 'currency',
    currency: 'EUR',
  }).format(amount);
}

/** Format an ISO date (YYYY-MM-DD) as a short readable date, e.g. "20 Nov 2026". */
export function formatDate(iso: string): string {
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
}

/** Today's date as YYYY-MM-DD, used as the default for new transactions. */
export function todayIso(): string {
  return new Date().toISOString().slice(0, 10);
}

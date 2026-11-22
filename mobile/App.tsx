import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { colors, spacing } from './src/theme';

/**
 * App.tsx is the ROOT component — the first thing that renders.
 *
 * Right now it shows a simple welcome screen so we can confirm the project
 * runs on your phone. In the next steps we replace this with a navigator that
 * switches between the Login, Home, Add-Transaction, and Dashboard screens.
 *
 * Anatomy of a React Native component:
 *   - it's a function that returns JSX (the UI),
 *   - <View> is a container (like a <div>), <Text> shows text,
 *   - `style` applies the styles defined below with StyleSheet.create.
 */
export default function App() {
  return (
    <View style={styles.container}>
      <Text style={styles.logo}>FinTrack 💸</Text>
      <Text style={styles.subtitle}>Your money, in your pocket.</Text>
      <Text style={styles.hint}>Setup complete — screens coming next.</Text>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,                        // fill the whole screen
    backgroundColor: colors.background,
    alignItems: 'center',           // center horizontally
    justifyContent: 'center',       // center vertically
    padding: spacing.lg,
  },
  logo: {
    fontSize: 40,
    fontWeight: 'bold',
    color: colors.primary,
  },
  subtitle: {
    fontSize: 16,
    color: colors.text,
    marginTop: spacing.sm,
  },
  hint: {
    fontSize: 13,
    color: colors.textMuted,
    marginTop: spacing.xl,
  },
});

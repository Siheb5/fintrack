// Decides which screens to show based on login state.
//   - still restoring the saved token  -> a loading spinner
//   - no token (logged out)            -> Login / Register
//   - token present (logged in)        -> Home (and later Add/Edit)
//
// Because this reads the auth context, the moment login() sets a token the
// app automatically swaps to the logged-in screens. No manual redirect needed.

import React from 'react';
import { ActivityIndicator, View, StyleSheet } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Pressable, Text } from 'react-native';
import { useAuth } from '../auth/AuthContext';
import { LoginScreen } from '../screens/LoginScreen';
import { RegisterScreen } from '../screens/RegisterScreen';
import { TransactionsScreen } from '../screens/TransactionsScreen';
import { AddTransactionScreen } from '../screens/AddTransactionScreen';
import { colors } from '../theme';
import type { RootStackParamList } from './types';

const Stack = createNativeStackNavigator<RootStackParamList>();

export function RootNavigator() {
  const { token, loading, logout } = useAuth();

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  return (
    <NavigationContainer>
      <Stack.Navigator>
        {token == null ? (
          // Logged-out stack
          <>
            <Stack.Screen name="Login" component={LoginScreen} options={{ headerShown: false }} />
            <Stack.Screen name="Register" component={RegisterScreen} options={{ title: 'Sign up' }} />
          </>
        ) : (
          // Logged-in stack
          <>
            <Stack.Screen
              name="Home"
              component={TransactionsScreen}
              options={{
                title: 'FinTrack',
                headerRight: () => (
                  <Pressable onPress={logout} hitSlop={10}>
                    <Text style={{ color: colors.primary, fontWeight: '600' }}>Log out</Text>
                  </Pressable>
                ),
              }}
            />
            <Stack.Screen name="AddTransaction" component={AddTransactionScreen} />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: colors.background },
});

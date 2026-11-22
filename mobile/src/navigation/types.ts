// The list of screens and any parameters each one expects. React Navigation
// uses this so TypeScript can check navigation.navigate(...) calls.

export type RootStackParamList = {
  Login: undefined;
  Register: undefined;
  Home: undefined;
  AddTransaction: { id?: number } | undefined;  // id present => editing
};

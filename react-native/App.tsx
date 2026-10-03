import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {SafeAreaProvider} from 'react-native-safe-area-context';
import {AuthProvider, useAuth} from './src/features/auth/AuthContext';
import {AuthStack, AppStack} from './src/navigation/AppNavigator';
import {ActivityIndicator, StyleSheet, View} from 'react-native';

function RootNavigator(): React.JSX.Element {
  const {status} = useAuth();
  if (status === 'loading') {return <View style={styles.loading}><ActivityIndicator color="#ffd60a" /></View>;}
  return status === 'signedIn' ? <AppStack /> : <AuthStack />;
}

export default function App(): React.JSX.Element {
  return <SafeAreaProvider><AuthProvider><NavigationContainer><RootNavigator /></NavigationContainer></AuthProvider></SafeAreaProvider>;
}
const styles = StyleSheet.create({loading: {flex: 1, backgroundColor: '#101820', alignItems: 'center', justifyContent: 'center'}});

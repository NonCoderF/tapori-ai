import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { AuthScreen } from '../features/auth/AuthScreen';
import { ChatScreen } from '../features/chat/ChatScreen';
import { CreditScreen } from '../features/credits/CreditScreen';

export type AuthStackParamList = { SignIn: undefined };
export type AppStackParamList = { Chat: undefined; Credits: undefined };

const Auth = createNativeStackNavigator<AuthStackParamList>();
const App = createNativeStackNavigator<AppStackParamList>();
export function AuthStack(): React.JSX.Element {
  return (
    <Auth.Navigator screenOptions={{ headerShown: false }}>
      <Auth.Screen name="SignIn" component={AuthScreen} />
    </Auth.Navigator>
  );
}
export function AppStack(): React.JSX.Element {
  return (
    <App.Navigator>
      <App.Screen name="Chat" component={ChatScreen} options={{ headerShown: false }} />
      <App.Screen name="Credits" component={CreditScreen} options={{ title: 'Credit packs' }} />
    </App.Navigator>
  );
}

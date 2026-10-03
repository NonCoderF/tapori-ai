import React, { useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { useAuth } from './AuthContext';

export function AuthScreen(): React.JSX.Element {
  const { signIn } = useAuth();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const onSignIn = async () => {
    if (busy) {
      return;
    }
    setBusy(true);
    setError(null);
    try {
      await signIn();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Google sign-in failed');
    } finally {
      setBusy(false);
    }
  };
  return (
    <View style={styles.root}>
      <View style={styles.card}>
        <Text style={styles.title}>Arre Bhidu! 👊</Text>
        <Text style={styles.subtitle}>Apna Tapori AI chat shuru kar re.</Text>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Sign in with Google"
          disabled={busy}
          onPress={onSignIn}
          style={styles.button}
        >
          {busy ? (
            <ActivityIndicator color="#101820" />
          ) : (
            <Text style={styles.buttonText}>Google se sign in kar re</Text>
          )}
        </Pressable>
        {error ? (
          <Text accessibilityRole="alert" style={styles.error}>
            {error}
          </Text>
        ) : null}
      </View>
    </View>
  );
}
const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: '#101820',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  card: { width: '100%', alignItems: 'center', gap: 18 },
  title: { fontSize: 32, fontWeight: '800', color: '#fff' },
  subtitle: { color: '#d8e1e8', fontSize: 16 },
  button: {
    backgroundColor: '#ffd60a',
    minHeight: 56,
    borderRadius: 28,
    paddingHorizontal: 28,
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonText: { fontSize: 17, fontWeight: '700', color: '#101820' },
  error: { color: '#ffb4ab', textAlign: 'center' },
});

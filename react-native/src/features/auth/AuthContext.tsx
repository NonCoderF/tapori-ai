import React, {createContext, useContext, useEffect, useMemo, useState} from 'react';
import {sessionStorage} from '../../services/storage';
import {GOOGLE_WEB_CLIENT_ID} from '../../services/config';
import {nativeAuth} from '../../services/native';

type AuthState = {status: 'loading' | 'signedOut' | 'signedIn'; token: string | null; name: string | null; signIn: () => Promise<void>; signOut: () => Promise<void>};
const AuthContext = createContext<AuthState | undefined>(undefined);
export function AuthProvider({children}: React.PropsWithChildren): React.JSX.Element {
  const [state, setState] = useState<Omit<AuthState, 'signIn' | 'signOut'>>({status: 'loading', token: null, name: null});
  useEffect(() => { let mounted = true; void (async () => { const value = await sessionStorage.read(); if (!mounted) {return;} if (value.token && GOOGLE_WEB_CLIENT_ID) { try { const refreshed = await nativeAuth.refresh(GOOGLE_WEB_CLIENT_ID); await sessionStorage.save(refreshed.idToken, refreshed.displayName ?? value.name); if (mounted) {setState({status: 'signedIn', token: refreshed.idToken, name: refreshed.displayName ?? value.name});} return; } catch { /* Keep the stored session when silent refresh is unavailable. */ } } setState({status: value.token ? 'signedIn' : 'signedOut', token: value.token, name: value.name}); })(); return () => {mounted = false;}; }, []);
  const value = useMemo<AuthState>(() => ({...state,
    signIn: async () => { if (!GOOGLE_WEB_CLIENT_ID) {throw new Error('Google sign-in is not configured');} const result = await nativeAuth.signIn(GOOGLE_WEB_CLIENT_ID); await sessionStorage.save(result.idToken, result.displayName ?? null); setState({status: 'signedIn', token: result.idToken, name: result.displayName ?? null}); },
    signOut: async () => { await nativeAuth.signOut(); await sessionStorage.clear(); setState({status: 'signedOut', token: null, name: null}); },
  }), [state]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export function useAuth(): AuthState { const value = useContext(AuthContext); if (!value) {throw new Error('useAuth must be used inside AuthProvider');} return value; }

import {NativeModules, Platform} from 'react-native';
export type DeviceInfo = {manufacturer: string; model: string; sdk: number};
export const nativeDevice = {getInfo: async (): Promise<DeviceInfo> => { if (Platform.OS !== 'android') {throw new Error('TaporiDevice is Android-only');} return NativeModules.TaporiDevice.getDeviceInfo() as Promise<DeviceInfo>; }};
type AuthResult = {idToken: string; displayName?: string};
type AuthBridge = {signIn(clientId: string): Promise<AuthResult>; refresh(clientId: string): Promise<AuthResult>; signOut(): Promise<void>};
type PaymentsBridge = {startCheckout(keyId: string, amountInPaise: number, description: string): Promise<string>};
export const nativeAuth = {
  signIn: (clientId: string) => (NativeModules.TaporiAuth as AuthBridge).signIn(clientId),
  refresh: (clientId: string) => (NativeModules.TaporiAuth as AuthBridge).refresh(clientId),
  signOut: () => (NativeModules.TaporiAuth as AuthBridge | undefined)?.signOut() ?? Promise.resolve(),
};
export const nativePayments = {
  checkout: async (keyId: string, amountInPaise: number, description: string): Promise<string> => {
    if (Platform.OS !== 'android') {throw new Error('TaporiPayments is Android-only');}
    const module = NativeModules.TaporiPayments as PaymentsBridge | undefined;
    if (!module) {throw new Error('Payments are not configured');}
    return module.startCheckout(keyId, amountInPaise, description);
  },
};

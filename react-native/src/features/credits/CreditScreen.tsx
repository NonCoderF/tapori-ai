import React, {useState} from 'react';
import {ActivityIndicator, Pressable, StyleSheet, Text, View} from 'react-native';
import {useAuth} from '../auth/AuthContext';
import {addCreditsUseCase} from '../../domain/usecases/AddCreditsUseCase';
import {taporiRepository} from '../../data/repositories/TaporiRepositoryImpl';
import {nativePayments} from '../../services/native';
import {RAZORPAY_KEY_ID} from '../../services/config';

const packs = [{title: 'Cutting Chai Pack', price: 10, credits: 100}, {title: 'Full Bottle Pack', price: 50, credits: 600}, {title: 'Don Pack', price: 250, credits: 1500}];
export function CreditScreen(): React.JSX.Element {
  const {token} = useAuth(); const [busy, setBusy] = useState(false); const [message, setMessage] = useState('');
  const buy = async (price: number, credits: number) => { if (!token || busy) {return;} setBusy(true); setMessage(''); try { if (!RAZORPAY_KEY_ID) {throw new Error('Payment is not configured for this build.');} await nativePayments.checkout(RAZORPAY_KEY_ID, price * 100, `Tapori AI ${credits} credits`); const result = await addCreditsUseCase(taporiRepository, token, credits); setMessage(result.credits === undefined ? 'Payment successful. Credits update ho rahe hain.' : `Jhakaas! Ab ${result.credits} credits hain.`); } catch (error) { setMessage(error instanceof Error ? error.message : 'Payment failed. Dobara try kar.'); } finally { setBusy(false); } };
  return <View style={styles.root}><Text style={styles.title}>Kuch choose kar!</Text>{packs.map(pack => <Pressable key={pack.price} disabled={busy} onPress={() => buy(pack.price, pack.credits)} style={styles.card}><Text style={styles.pack}>{pack.title}</Text><Text style={styles.price}>₹{pack.price} → {pack.credits} chats</Text></Pressable>)}{busy ? <ActivityIndicator color="#1d4760" /> : null}{message ? <Text accessibilityRole="alert" style={styles.note}>{message}</Text> : null}</View>;
}
const styles = StyleSheet.create({root: {flex: 1, padding: 18, gap: 16, backgroundColor: '#f3f6f8'}, title: {fontSize: 26, fontWeight: '800', color: '#17232b', marginBottom: 8}, card: {padding: 18, borderRadius: 16, backgroundColor: '#fff', elevation: 2}, pack: {fontSize: 18, fontWeight: '700', color: '#17232b'}, price: {marginTop: 8, color: '#52616a'}, note: {color: '#8a5a00', textAlign: 'center'}});

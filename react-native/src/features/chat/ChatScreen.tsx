import React, {memo, useCallback, useRef} from 'react';
import {ActivityIndicator, FlatList, KeyboardAvoidingView, Platform, Pressable, StatusBar, StyleSheet, Text, TextInput, View} from 'react-native';
import {SafeAreaView} from 'react-native-safe-area-context';
import {useAuth} from '../auth/AuthContext';
import {useChat} from './useChat';
import {ChatMessage} from '../../types/api';
import {useNavigation} from '@react-navigation/native';
import type {NativeStackNavigationProp} from '@react-navigation/native-stack';
import type {AppStackParamList} from '../../navigation/AppNavigator';

const Bubble = memo(({item}: {item: ChatMessage}) => <View style={[styles.bubble, item.isUser ? styles.userBubble : styles.aiBubble]}><Text style={[styles.message, item.isUser ? styles.userText : styles.aiText]}>{item.text}</Text></View>);

export function ChatScreen(): React.JSX.Element {
  const {token, name, signOut} = useAuth();
  const {state, setInput, send, retry, reload, clearPayment} = useChat(token);
  const navigation = useNavigation<NativeStackNavigationProp<AppStackParamList>>();
  const listRef = useRef<FlatList<ChatMessage>>(null);
  const renderItem = useCallback(({item}: {item: ChatMessage}) => <Bubble item={item} />, []);
  const keyExtractor = useCallback((item: ChatMessage) => item.id, []);

  return <SafeAreaView style={styles.safeRoot} edges={['top', 'bottom']}>
    <StatusBar backgroundColor="#101820" barStyle="light-content" />
    <KeyboardAvoidingView style={styles.root} behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
      <View style={styles.header}><View><Text style={styles.brand}>Tapori AI</Text><Text style={styles.greeting}>{name ? `Kya bolta ${name.split(' ')[0]}?` : 'Kya bolta bhidu?'}</Text></View><View style={styles.headerActions}><Pressable accessibilityRole="button" onPress={() => navigation.navigate('Credits')}><Text style={styles.credits}>Credits</Text></Pressable><Pressable accessibilityRole="button" onPress={signOut}><Text style={styles.logout}>Logout</Text></Pressable></View></View>
      {state.loading ? <View style={styles.center}><ActivityIndicator color="#ffd60a" /><Text style={styles.muted}>Chat load ho raha hai...</Text></View> : state.error && state.messages.length === 0 ? <View style={styles.center}><Text style={styles.error}>{state.error}</Text><Pressable onPress={reload} style={styles.retry}><Text style={styles.retryText}>Dobara try</Text></Pressable></View> : <FlatList ref={listRef} data={state.messages} renderItem={renderItem} keyExtractor={keyExtractor} contentContainerStyle={state.messages.length ? styles.list : styles.emptyList} keyboardShouldPersistTaps="handled" onContentSizeChange={() => listRef.current?.scrollToEnd({animated: true})} ListEmptyComponent={<Text style={styles.muted}>Bolo bhidu, aaj kya scene hai?</Text>} />}
      {state.error && state.messages.length > 0 ? <Pressable onPress={retry} style={styles.errorBar}><Text style={styles.error}>{state.error} Tap karke retry kar.</Text></Pressable> : null}
      {state.paymentRequired ? <Pressable onPress={clearPayment} style={styles.creditBar}><Text style={styles.creditText}>{state.paymentRequired}</Text></Pressable> : null}
      <View style={styles.composer}><TextInput accessibilityLabel="Message" value={state.input} onChangeText={setInput} onSubmitEditing={() => void send()} placeholder="Kuch bhi puch bhidu..." placeholderTextColor="#82909b" multiline maxLength={4000} style={styles.input} editable={!state.sending} /><Pressable accessibilityRole="button" accessibilityLabel={state.sending ? 'Sending message' : 'Send message'} accessibilityState={{busy: state.sending, disabled: state.sending || !state.input.trim()}} onPress={() => void send()} disabled={state.sending || !state.input.trim()} style={[styles.send, (state.sending || !state.input.trim()) && styles.disabled]}>{state.sending ? <ActivityIndicator testID="send-loader" color="#101820" /> : <Text style={styles.sendText}>➤</Text>}</Pressable></View>
    </KeyboardAvoidingView>
  </SafeAreaView>;
}

const styles = StyleSheet.create({safeRoot: {flex: 1, backgroundColor: '#f3f6f8'}, root: {flex: 1, backgroundColor: '#f3f6f8'}, header: {backgroundColor: '#101820', paddingHorizontal: 18, paddingTop: 16, paddingBottom: 14, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center'}, headerActions: {flexDirection: 'row', alignItems: 'center', gap: 16}, brand: {fontSize: 22, fontWeight: '800', color: '#fff'}, greeting: {color: '#b8c5cc', marginTop: 2}, credits: {color: '#ffd60a', fontWeight: '700'}, logout: {color: '#ffd60a', fontWeight: '700'}, list: {padding: 16, gap: 10}, emptyList: {flexGrow: 1, alignItems: 'center', justifyContent: 'center'}, bubble: {maxWidth: '84%', borderRadius: 16, padding: 12}, userBubble: {alignSelf: 'flex-end', backgroundColor: '#1d4760', borderBottomRightRadius: 3}, aiBubble: {alignSelf: 'flex-start', backgroundColor: '#fff', borderBottomLeftRadius: 3}, message: {fontSize: 16, lineHeight: 22}, userText: {color: '#fff'}, aiText: {color: '#17232b'}, composer: {padding: 10, gap: 8, flexDirection: 'row', backgroundColor: '#fff', borderTopWidth: StyleSheet.hairlineWidth, borderTopColor: '#ccd5da'}, input: {flex: 1, maxHeight: 110, borderRadius: 22, backgroundColor: '#eef2f4', paddingHorizontal: 16, paddingVertical: 10, color: '#17232b'}, send: {width: 46, height: 46, borderRadius: 23, alignItems: 'center', justifyContent: 'center', backgroundColor: '#ffd60a'}, disabled: {opacity: 0.5}, sendText: {fontSize: 22, color: '#101820'}, center: {flex: 1, alignItems: 'center', justifyContent: 'center', gap: 12, padding: 24}, muted: {color: '#687780', fontSize: 16, textAlign: 'center'}, error: {color: '#b3261e', textAlign: 'center'}, retry: {padding: 12, borderRadius: 18, backgroundColor: '#1d4760'}, retryText: {color: '#fff', fontWeight: '700'}, errorBar: {padding: 10, backgroundColor: '#ffe5e2'}, creditBar: {padding: 12, backgroundColor: '#fff0b3'}, creditText: {color: '#5f4b00', textAlign: 'center', fontWeight: '600'}});

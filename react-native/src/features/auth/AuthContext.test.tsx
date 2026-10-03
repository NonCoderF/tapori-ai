import React from 'react'; import {render, waitFor} from '@testing-library/react-native'; import {Text} from 'react-native'; import {AuthProvider, useAuth} from './AuthContext';
jest.mock('@react-native-async-storage/async-storage', () => ({multiGet: jest.fn().mockResolvedValue([['@tapori/token', null], ['@tapori/name', null], ['@tapori/chat-id', null]]), multiSet: jest.fn(), multiRemove: jest.fn(), setItem: jest.fn()}));
function Probe() { const {status} = useAuth(); return <Text>{status}</Text>; }
test('restores signed-out state when no session is stored', async () => { const view = render(<AuthProvider><Probe /></AuthProvider>); await waitFor(() => expect(view.getByText('signedOut')).toBeTruthy()); });

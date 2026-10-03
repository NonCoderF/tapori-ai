export const API_BASE_URL = 'https://jmyqvrvrguhfujgombqe.functions.supabase.co/';
// This is the public OAuth web-client identifier required by Google Sign-In.
// It matches master-dev1; secrets and user tokens must never be placed here.
export const GOOGLE_WEB_CLIENT_ID =
  process.env.TAPORI_GOOGLE_WEB_CLIENT_ID ??
  '251119098046-ih3bq53nisp3fo39p6gmrfbth4mt2msb.apps.googleusercontent.com';
export const RAZORPAY_KEY_ID = process.env.TAPORI_RAZORPAY_KEY_ID ?? '';
export const SYSTEM_MESSAGE =
  'You are a Mumbai Tapori assistant. Reply in Mumbai slang hinglish language fully.';

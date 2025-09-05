package com.sparkstudios.taporiai

import android.content.Context
import androidx.core.content.edit

object Prefs {
    private const val PREFS_NAME = "tapori_prefs"
    private const val USER_ID = "user_id"
    private const val USER_NAME = "user_name"

    fun saveUser(context: Context, userId: String, userName: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(USER_ID, userId)
                    .putString(USER_NAME, userName)
            }
    }

    fun getUserId(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(USER_ID, null)
    }

    fun getUserName(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(USER_NAME, null)
    }

    fun clearUser(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                clear()
            }
    }

    fun saveChatId(context: Context, chatId: String) {
        val prefs = context.getSharedPreferences("tapori_ai_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("chat_id", chatId).apply()
    }

    fun getChatId(context: Context): String? {
        val prefs = context.getSharedPreferences("tapori_ai_prefs", Context.MODE_PRIVATE)
        return prefs.getString("chat_id", null)
    }

}

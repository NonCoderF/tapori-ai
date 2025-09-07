package com.sparkstudios.taporiai.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CreditPacksScreen(onBuyClick: (price: Int, chats: Int) -> Unit) {
    val packs = listOf(
        Triple("Cutting Chai Pack ☕", 10, 100),
        Triple("Full Bottle Pack 🥃", 50, 600),
        Triple("Don Pack 👑", 250, 1500)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kuch choose kar!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        packs.forEach { (title, price, chats) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBuyClick(price, chats) },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "₹$price → $chats chats",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = when (price) {
                            10 -> "Sasta aur tikau, bas shuruat ke liye."
                            50 -> "Thoda zyada, mast discount ke saath."
                            250 -> "Hardcore bhidus ke liye, ekdum unlimited jaisa feel."
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }

}

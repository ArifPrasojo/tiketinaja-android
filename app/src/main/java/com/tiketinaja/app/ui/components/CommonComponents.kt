package com.tiketinaja.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiketinaja.app.ui.theme.*

@Composable
fun StatusBadge(status: String, isCheckedIn: Boolean = false, isOngoing: Boolean = false) {
    val (label, bg, fg, border) = when {
        isOngoing -> Quad("Sedang Bermain", GreenTint, GreenText, FieldGreen)
        isCheckedIn && status == "confirmed" -> Quad("Check-in ✓", BlueTint, ElectricBlue, ElectricBlue)
        status == "pending" -> Quad("Menunggu Bayar", AmberTint, AmberText, Color(0xFFFDE68A))
        status == "confirmed" -> Quad("Terkonfirmasi", BlueTint, ElectricBlue, Color(0xFFBFDBFE))
        status == "completed" -> Quad("Selesai", GreenTint, GreenText, Color(0xFFBBF7D0))
        status == "cancelled" -> Quad("Dibatalkan", RedTint, RedText, Color(0xFFFECACA))
        status == "expired" -> Quad("Kedaluwarsa", Color(0xFFF1F5F9), Color(0xFF64748B), Color(0xFFCBD5E1))
        else -> Quad(status, Color(0xFFF1F5F9), Color(0xFF64748B), Color(0xFFCBD5E1))
    }

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(8.dp))
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

@Composable
fun AlertModal(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String = "Mengerti"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold, color = PitchBlack) },
        text = { Text(text = message, color = OnSurfaceVariant, fontSize = 14.sp) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

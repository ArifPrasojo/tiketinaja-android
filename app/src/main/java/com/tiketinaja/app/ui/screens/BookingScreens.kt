package com.tiketinaja.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiketinaja.app.data.api.ApiClient
import com.tiketinaja.app.data.api.QrGenerator
import com.tiketinaja.app.data.model.BookingDto
import com.tiketinaja.app.data.model.ReviewRequest
import com.tiketinaja.app.data.model.TicketResponse
import com.tiketinaja.app.ui.components.AlertModal
import com.tiketinaja.app.ui.components.StatusBadge
import com.tiketinaja.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf<String?>(null) }
    var bookings by remember { mutableStateOf<List<BookingDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun load() {
        loading = true
        scope.launch {
            try {
                val res = ApiClient.getService(context).bookings(status = selectedTab)
                bookings = res.data
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(selectedTab) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Saya", fontWeight = FontWeight.Bold, color = PitchBlack) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceLight)
                .padding(padding)
        ) {
            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = when (selectedTab) {
                    null -> 0
                    "pending" -> 1
                    "confirmed" -> 2
                    "completed" -> 3
                    "expired" -> 4
                    else -> 0
                },
                containerColor = Color.White,
                edgePadding = 16.dp
            ) {
                Tab(selected = selectedTab == null, onClick = { selectedTab = null }, text = { Text("Semua") })
                Tab(selected = selectedTab == "pending", onClick = { selectedTab = "pending" }, text = { Text("Menunggu Bayar") })
                Tab(selected = selectedTab == "confirmed", onClick = { selectedTab = "confirmed" }, text = { Text("Terkonfirmasi") })
                Tab(selected = selectedTab == "completed", onClick = { selectedTab = "completed" }, text = { Text("Selesai") })
                Tab(selected = selectedTab == "expired", onClick = { selectedTab = "expired" }, text = { Text("Hangus") })
            }

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricBlue)
                }
            } else if (bookings.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Belum ada tiket", style = MaterialTheme.typography.titleLarge, color = PitchBlack)
                        Spacer(Modifier.height(8.dp))
                        Text("Reservasi dengan status yang dipilih akan tampil di sini.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    }
                }
            } else {
                val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { b ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(b.id) }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(b.bookingCode, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    StatusBadge(b.status, b.checkedIn)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(b.venue?.name ?: "-", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PitchBlack)
                                val first = b.items?.firstOrNull()
                                Text(
                                    text = "${first?.bookingDate ?: ""} • ${first?.startTime ?: ""} - ${b.items?.lastOrNull()?.endTime ?: ""}",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Rp ${fmt.format(b.totalAmount - b.discountAmount)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PitchBlack
                                    )
                                    Text("Lihat Detail →", fontSize = 12.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToTicket: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var booking by remember { mutableStateOf<BookingDto?>(null) }
    var loading by remember { mutableStateOf(true) }
    var alertMsg by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        loading = true
        scope.launch {
            try {
                val res = ApiClient.getService(context).booking(bookingId)
                booking = res.booking
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(bookingId) { refresh() }

    if (alertMsg != null) {
        AlertModal(title = "Info", message = alertMsg ?: "", onDismiss = { alertMsg = null })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(booking?.bookingCode ?: "Detail Booking", fontWeight = FontWeight.Bold, color = PitchBlack) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { refresh() }) { Icon(Icons.Default.Refresh, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricBlue)
            }
        } else {
            val b = booking
            val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(b?.bookingCode ?: "", fontWeight = FontWeight.Bold, color = ElectricBlue)
                            StatusBadge(b?.status ?: "", b?.checkedIn ?: false)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(b?.venue?.name ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PitchBlack)
                        Text(b?.venue?.address ?: "", fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                }

                // Sesi Items
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Jadwal Sesi Lapangan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PitchBlack)
                        b?.items?.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(item.court?.name ?: "Lapangan", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = PitchBlack)
                                    Text("${item.bookingDate} • ${item.startTime}-${item.endTime}", fontSize = 11.sp, color = OnSurfaceVariant)
                                }
                                Text("Rp ${fmt.format(item.price)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PitchBlack)
                            }
                        }
                        Divider(Modifier.padding(vertical = 4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PitchBlack)
                            Text("Rp ${fmt.format((b?.totalAmount ?: 0) - (b?.discountAmount ?: 0))}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ElectricBlue)
                        }
                    }
                }

                // Actions based on status
                if (b?.status == "pending") {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val tokenRes = ApiClient.getService(context).paymentToken(bookingId)
                                    val url = tokenRes.redirectUrl
                                    if (!url.isNullOrBlank()) {
                                        // Open in secure Custom Tab
                                        val builder = CustomTabsIntent.Builder()
                                        val customTabsIntent = builder.build()
                                        customTabsIntent.launchUrl(context, Uri.parse(url))
                                    } else {
                                        alertMsg = "Gagal memuat URL pembayaran Midtrans."
                                    }
                                } catch (e: Exception) {
                                    alertMsg = "Waktu pembayaran sudah habis atau sesi kedaluwarsa."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("Bayar Sekarang (Midtrans)", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    ApiClient.getService(context).cancel(bookingId)
                                    alertMsg = "Booking berhasil dibatalkan."
                                    refresh()
                                } catch (_: Exception) {
                                    alertMsg = "Gagal membatalkan booking."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batalkan Booking", color = RedText)
                    }
                } else if (b?.status == "confirmed") {
                    Button(
                        onClick = { onNavigateToTicket(bookingId) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Icon(Icons.Default.QrCode, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Buka Tiket QR Check-in", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketQrScreen(
    bookingId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var ticket by remember { mutableStateOf<TicketResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    var alertMsg by remember { mutableStateOf<String?>(null) }
    var checkinLoading by remember { mutableStateOf(false) }

    fun refresh() {
        loading = true
        scope.launch {
            try {
                ticket = ApiClient.getService(context).ticket(bookingId)
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(bookingId) { refresh() }

    if (alertMsg != null) {
        AlertModal(title = "Info", message = alertMsg ?: "", onDismiss = { alertMsg = null })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiket QR", fontWeight = FontWeight.Bold, color = PitchBlack) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricBlue)
            }
        } else {
            val t = ticket
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
                    .padding(padding)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (t?.isForfeited == true) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RedTint),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TIKET HANGUS", fontWeight = FontWeight.Black, color = RedText, fontSize = 16.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Masa sewa telah berakhir dan Anda tidak melakukan check-in.",
                                color = RedText,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else if (!t?.qrToken.isNullOrBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(t?.booking?.venue?.name ?: "Tiket Pertandingan", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PitchBlack)
                            Text(t?.booking?.bookingCode ?: "", fontSize = 12.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))

                            val bitmap = remember(t?.qrToken) {
                                t?.qrToken?.let { QrGenerator.generate(it, 400) }
                            }

                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier.size(220.dp)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("Tunjukkan QR ini ke petugas lapangan", fontSize = 11.sp, color = OnSurfaceVariant)

                            Spacer(Modifier.height(16.dp))

                            // Self Check-in button if window is open
                            if (t?.isCheckedIn == true) {
                                Text("✓ Sudah Check-in", fontWeight = FontWeight.Bold, color = FieldGreen, fontSize = 14.sp)
                            } else if (t?.checkinWindow?.isOpen == true) {
                                Button(
                                    onClick = {
                                        checkinLoading = true
                                        scope.launch {
                                            try {
                                                val res = ApiClient.getService(context).checkin(bookingId)
                                                alertMsg = res.message ?: "Check-in berhasil!"
                                                refresh()
                                            } catch (e: Exception) {
                                                alertMsg = "Check-in gagal."
                                            } finally {
                                                checkinLoading = false
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !checkinLoading
                                ) {
                                    Text("Check-in Mandiri Sekarang", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    "Check-in mandiri dibuka 10 menit sebelum jam main.",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Text(t?.message ?: "Tiket belum tersedia.", color = OnSurfaceVariant)
                }
            }
        }
    }
}

package com.tiketinaja.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiketinaja.app.data.api.ApiClient
import com.tiketinaja.app.data.model.CustomerDashboardData
import com.tiketinaja.app.ui.components.StatusBadge
import com.tiketinaja.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToVenues: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToBookingDetail: (Long) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var data by remember { mutableStateOf<CustomerDashboardData?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        loading = true
        error = null
        scope.launch {
            try {
                val res = ApiClient.getService(context).dashboard()
                if (res.success) {
                    data = res.data
                }
            } catch (e: Exception) {
                error = "Gagal memuat dashboard."
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tiketin",
                            fontWeight = FontWeight.Black,
                            color = PitchBlack,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Aja",
                            fontWeight = FontWeight.Black,
                            color = ElectricBlue,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = PitchBlack)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profil", tint = ElectricBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = true,
                    onClick = { refresh() },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Beranda") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, selectedTextColor = ElectricBlue)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToVenues,
                    icon = { Icon(Icons.Default.Search, null) },
                    label = { Text("Venue") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToBookings,
                    icon = { Icon(Icons.Default.ConfirmationNumber, null) },
                    label = { Text("Booking") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Akun") }
                )
            }
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricBlue)
            }
        } else if (error != null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error ?: "", color = RedText)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { refresh() }) { Text("Coba Lagi") }
                }
            }
        } else {
            val d = data
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Halo, ${d?.user?.name?.substringBefore(' ') ?: "Pemain"}", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Text("Jadwalkan\npermainanmu.", style = MaterialTheme.typography.headlineLarge, color = PitchBlack)
                        Text("Pilih arena, tentukan jam, lalu siapkan timmu.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Spacer(Modifier.height(6.dp))
                        Button(
                            onClick = onNavigateToVenues,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Cari lapangan tersedia", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                // Active Match Widget (Sedang Bermain)
                d?.activeMatch?.let { match ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = FieldGreen),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToBookingDetail(match.bookingId) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "SEDANG BERMAIN",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${match.startTime} - ${match.endTime}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = match.venueName ?: "-",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = match.courtName ?: "-",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Quick Stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text("Mendatang", fontSize = 11.sp, color = OnSurfaceVariant)
                                Text("${d?.stats?.upcomingCount ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text("Total Main", fontSize = 11.sp, color = OnSurfaceVariant)
                                Text("${d?.stats?.totalBookings ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PitchBlack)
                            }
                        }
                    }
                }

                // Quick Action Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateToVenues,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Cari Lapangan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onNavigateToBookings,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ConfirmationNumber, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Tiket Saya", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Upcoming Bookings Section
                item {
                    Text(
                        text = "Jadwal Mendatang",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PitchBlack,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                val upcomings = d?.upcomingBookings ?: emptyList()
                if (upcomings.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("Belum ada jadwal pertandingan aktif.", fontSize = 13.sp, color = OnSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(upcomings) { booking ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToBookingDetail(booking.id) }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(booking.bookingCode, fontWeight = FontWeight.Bold, color = ElectricBlue, fontSize = 13.sp)
                                    StatusBadge(booking.status, booking.checkedIn)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(booking.venue?.name ?: "-", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PitchBlack)
                                val firstItem = booking.items?.firstOrNull()
                                Text(
                                    text = "${firstItem?.bookingDate ?: ""} • ${firstItem?.startTime ?: ""} - ${booking.items?.lastOrNull()?.endTime ?: ""}",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

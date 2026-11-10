package com.tiketinaja.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tiketinaja.app.data.api.ApiClient
import com.tiketinaja.app.data.model.*
import com.tiketinaja.app.ui.components.AlertModal
import com.tiketinaja.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenuesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVenueDetail: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var venues by remember { mutableStateOf<List<VenueDto>>(emptyList()) }
    var sports by remember { mutableStateOf<List<SportDto>>(emptyList()) }
    var selectedSportId by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    fun load() {
        loading = true
        scope.launch {
            try {
                sports = ApiClient.getService(context).sports()
                val res = ApiClient.getService(context).venues(
                    search = searchQuery.ifBlank { null },
                    sport = selectedSportId
                )
                venues = res.data
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(selectedSportId) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cari Venue", fontWeight = FontWeight.Bold, color = PitchBlack) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
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
            // Search Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama venue atau kota...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = OnSurfaceVariant) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { load() },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cari")
                }
            }

            // Sport Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedSportId == null,
                        onClick = { selectedSportId = null },
                        label = { Text("Semua") }
                    )
                }
                items(sports) { s ->
                    FilterChip(
                        selected = selectedSportId == s.id,
                        onClick = { selectedSportId = if (selectedSportId == s.id) null else s.id },
                        label = { Text(s.name) }
                    )
                }
            }

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricBlue)
                }
            } else if (venues.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada venue ditemukan.", color = OnSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(venues) { v ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToVenueDetail(v.id) }
                        ) {
                            Column {
                                if (!v.imageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = v.imageUrl,
                                        contentDescription = v.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(190.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(v.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PitchBlack, modifier = Modifier.weight(1f))
                                        if (v.reviewsAvgRating != null) {
                                            Text("★ ${String.format(Locale.US, "%.1f", v.reviewsAvgRating)}", fontSize = 12.sp, color = AthleticOrange, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(v.city, fontSize = 14.sp, color = OnSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                                    Text(
                                        "${v.courtsCount ?: 0} lapangan tersedia",
                                        fontSize = 11.sp,
                                        color = OnSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
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
fun VenueDetailScreen(
    venueId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToCourtBooking: (courtId: Long, courtName: String, pricePerHour: Long, venueName: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var venue by remember { mutableStateOf<VenueDto?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(venueId) {
        scope.launch {
            try {
                venue = ApiClient.getService(context).venue(venueId)
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(venue?.name ?: "Detail Venue", fontWeight = FontWeight.Bold, color = PitchBlack, maxLines = 1) },
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
            val v = venue
            val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                v?.imageUrl?.let { img ->
                    item {
                        AsyncImage(
                            model = img,
                            contentDescription = v.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item {
                    Text(v?.name ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PitchBlack)
                    Text("${v?.address ?: ""}, ${v?.city ?: ""}", fontSize = 13.sp, color = OnSurfaceVariant)
                    if (!v?.description.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(v?.description ?: "", fontSize = 13.sp, color = PitchBlack)
                    }
                }

                item {
                    Text("Pilih Lapangan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PitchBlack)
                }

                val courts = v?.courts ?: emptyList()
                items(courts) { court ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(court.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PitchBlack)
                                Text(court.sport?.name ?: "-", fontSize = 12.sp, color = OnSurfaceVariant)
                                Text("Rp ${fmt.format(court.pricePerHour)} / jam", fontSize = 13.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onNavigateToCourtBooking(court.id, court.name, court.pricePerHour, v?.name ?: "") },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Pilih", fontWeight = FontWeight.Bold)
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
fun CourtBookingScreen(
    courtId: Long,
    courtName: String,
    pricePerHour: Long,
    venueId: Long,
    venueName: String,
    onNavigateBack: () -> Unit,
    onBookingSuccess: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    var selectedDate by remember { mutableStateOf(sdf.format(Date())) }
    var slots by remember { mutableStateOf<List<TimeSlotDto>>(emptyList()) }
    var selectedSlots by remember { mutableStateOf<Set<String>>(emptySet()) }
    var promoCode by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf(0L) }
    var promoMessage by remember { mutableStateOf<String?>(null) }
    var loadingSlots by remember { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }
    var alertMsg by remember { mutableStateOf<String?>(null) }

    fun loadSlots() {
        loadingSlots = true
        selectedSlots = emptySet()
        scope.launch {
            try {
                val res = ApiClient.getService(context).availability(courtId, selectedDate)
                slots = res.slots
            } catch (_: Exception) {
            } finally {
                loadingSlots = false
            }
        }
    }

    LaunchedEffect(selectedDate) { loadSlots() }

    if (alertMsg != null) {
        AlertModal(title = "Info", message = alertMsg ?: "", onDismiss = { alertMsg = null })
    }

    val totalAmount = selectedSlots.size * pricePerHour
    val finalAmount = (totalAmount - discountAmount).coerceAtLeast(0L)
    val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(courtName, fontWeight = FontWeight.Bold, color = PitchBlack) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${selectedSlots.size} Jam Dipilih", fontSize = 12.sp, color = OnSurfaceVariant)
                            Text("Total: Rp ${fmt.format(finalAmount)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                        }
                        Button(
                            onClick = {
                                if (selectedSlots.isEmpty()) {
                                    alertMsg = "Pilih minimal 1 jam slot bermain."
                                    return@Button
                                }
                                submitting = true
                                scope.launch {
                                    try {
                                        val req = CreateBookingRequest(
                                            courtId = courtId,
                                            date = selectedDate,
                                            timeSlots = selectedSlots.toList().sorted(),
                                            promoCode = promoCode.ifBlank { null }
                                        )
                                        val res = ApiClient.getService(context).createBooking(req)
                                        onBookingSuccess(res.booking.id)
                                    } catch (e: Exception) {
                                        alertMsg = "Gagal membuat booking: slot sudah terisi atau sesi expired."
                                    } finally {
                                        submitting = false
                                    }
                                }
                            },
                            enabled = selectedSlots.isNotEmpty() && !submitting,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (submitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Booking Sekarang", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceLight)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(venueName, fontSize = 12.sp, color = OnSurfaceVariant)
                    Text(courtName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PitchBlack)
                    Text("Rp ${fmt.format(pricePerHour)} / jam (Slot Anker 07:00)", fontSize = 13.sp, color = ElectricBlue)
                }
            }

            // Date picker field (simple text yyyy-MM-dd)
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                label = { Text("Tanggal Booking (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Slot Picker
            Text("Pilih Slot Jam Main", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PitchBlack)
            if (loadingSlots) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricBlue)
                }
            } else if (slots.isEmpty()) {
                Text("Tidak ada slot tersedia pada tanggal ini.", color = OnSurfaceVariant, fontSize = 13.sp)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(slots) { slot ->
                        val isSelected = selectedSlots.contains(slot.startTime)
                        val bg = when {
                            !slot.available -> Color(0xFFF1F5F9)
                            isSelected -> ElectricBlue
                            else -> Color.White
                        }
                        val textCol = when {
                            !slot.available -> Color(0xFF94A3B8)
                            isSelected -> Color.White
                            else -> PitchBlack
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg)
                                .border(1.dp, if (isSelected) ElectricBlue else StadiumGray, RoundedCornerShape(10.dp))
                                .clickable(enabled = slot.available) {
                                    selectedSlots = if (isSelected) selectedSlots - slot.startTime else selectedSlots + slot.startTime
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${slot.startTime}-${slot.endTime}",
                                color = textCol,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Promo Code Input
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Punya Kode Promo?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PitchBlack)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = promoCode,
                            onValueChange = { promoCode = it.uppercase() },
                            placeholder = { Text("KODE PROMO") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (promoCode.isBlank()) return@Button
                                scope.launch {
                                    try {
                                        val res = ApiClient.getService(context).validatePromo(
                                            ValidatePromoRequest(promoCode.trim(), totalAmount.coerceAtLeast(1L), venueId)
                                        )
                                        if (res.valid) {
                                            discountAmount = res.discountAmount ?: 0L
                                            promoMessage = "Diskon Rp ${fmt.format(discountAmount)} berhasil diterapkan!"
                                        } else {
                                            discountAmount = 0L
                                            promoMessage = res.message ?: "Promo tidak valid"
                                        }
                                    } catch (_: Exception) {
                                        discountAmount = 0L
                                        promoMessage = "Promo tidak valid atau sudah kedaluwarsa."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PitchBlack),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Pakai")
                        }
                    }
                    promoMessage?.let {
                        Text(it, fontSize = 12.sp, color = if (discountAmount > 0) FieldGreen else RedText, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
    }
}

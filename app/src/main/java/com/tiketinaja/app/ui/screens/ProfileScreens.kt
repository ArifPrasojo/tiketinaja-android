package com.tiketinaja.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiketinaja.app.data.api.ApiClient
import com.tiketinaja.app.data.api.TokenManager
import com.tiketinaja.app.data.model.*
import com.tiketinaja.app.ui.components.AlertModal
import com.tiketinaja.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPromos: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<UserDto?>(null) }
    var loading by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var deletePassword by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var alertMsg by remember { mutableStateOf<String?>(null) }
    var savingProfile by remember { mutableStateOf(false) }
    var savingPassword by remember { mutableStateOf(false) }
    var deletingAccount by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val u = ApiClient.getService(context).me()
                user = u
                name = u.name
                email = u.email
                phone = u.phone ?: ""
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    if (alertMsg != null) {
        AlertModal(title = "Info", message = alertMsg ?: "", onDismiss = { alertMsg = null })
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Akun Permanen", fontWeight = FontWeight.Bold, color = RedText) },
            text = {
                Column {
                    Text("Tindakan ini tidak dapat dibatalkan. Masukkan password Anda untuk konfirmasi:", fontSize = 13.sp, color = OnSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = deletePassword,
                        onValueChange = { deletePassword = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deletePassword.isBlank()) return@Button
                        deletingAccount = true
                        scope.launch {
                            try {
                                ApiClient.getService(context).deleteAccount(DeleteAccountRequest(deletePassword))
                                TokenManager(context).clear()
                                showDeleteDialog = false
                                onLoggedOut()
                            } catch (e: Exception) {
                                alertMsg = "Password salah. Gagal menghapus akun."
                            } finally {
                                deletingAccount = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedText),
                    enabled = !deletingAccount
                ) {
                    Text("Ya, Hapus Akun")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Akun", fontWeight = FontWeight.Bold, color = PitchBlack) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Bio Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(BlueTint, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.name?.firstOrNull() ?: 'U').toString().uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = ElectricBlue
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(user?.name ?: "-", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PitchBlack)
                            Text(user?.email ?: "-", fontSize = 12.sp, color = OnSurfaceVariant)
                            Text(
                                text = (user?.role ?: "customer").replaceFirstChar { it.uppercase() },
                                fontSize = 11.sp,
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Shortcut Promo
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToPromos() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, null, tint = AthleticOrange)
                            Spacer(Modifier.width(12.dp))
                            Text("Lihat Kode Promo Aktif", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PitchBlack)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = OnSurfaceVariant)
                    }
                }

                // Edit Profile Form
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Informasi Akun", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PitchBlack)
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Nomor Telepon") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                savingProfile = true
                                scope.launch {
                                    try {
                                        ApiClient.getService(context).updateProfile(UpdateProfileRequest(name.trim(), email.trim(), phone.ifBlank { null }))
                                        alertMsg = "Profil berhasil disimpan."
                                    } catch (e: Exception) {
                                        alertMsg = "Gagal memperbarui profil: email mungkin sudah digunakan."
                                    } finally {
                                        savingProfile = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !savingProfile,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simpan Profil")
                        }
                    }
                }

                // Change Password Form
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Ubah Password", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PitchBlack)
                        OutlinedTextField(value = currentPassword, onValueChange = { currentPassword = it }, label = { Text("Password Saat Ini") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("Password Baru (min. 8 karakter)") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Ulangi Password Baru") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                if (newPassword != confirmPassword) {
                                    alertMsg = "Konfirmasi password baru tidak cocok."
                                    return@Button
                                }
                                if (newPassword.length < 8) {
                                    alertMsg = "Password baru minimal 8 karakter."
                                    return@Button
                                }
                                savingPassword = true
                                scope.launch {
                                    try {
                                        ApiClient.getService(context).password(UpdatePasswordRequest(currentPassword, newPassword, confirmPassword))
                                        alertMsg = "Password berhasil diubah."
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    } catch (e: Exception) {
                                        alertMsg = "Gagal mengubah password: password saat ini salah."
                                    } finally {
                                        savingPassword = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PitchBlack),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !savingPassword,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Perbarui Password")
                        }
                    }
                }

                // Logout & Danger Zone
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    try {
                                        ApiClient.getService(context).logout()
                                    } catch (_: Exception) {
                                    } finally {
                                        TokenManager(context).clear()
                                        onLoggedOut()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Logout, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Keluar dari Akun")
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = RedTint),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Hapus Akun Permanen", color = RedText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromosScreen(
    venueId: Long? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var promos by remember { mutableStateOf<List<PromoDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(venueId) {
        scope.launch {
            try {
                val res = ApiClient.getService(context).promos(venueId)
                if (res.success) {
                    promos = res.data
                }
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kode Promo", fontWeight = FontWeight.Bold, color = PitchBlack) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricBlue)
            }
        } else if (promos.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Belum ada promo aktif saat ini.", color = OnSurfaceVariant)
            }
        } else {
            val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(promos) { p ->
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
                                Text(p.code, fontWeight = FontWeight.Black, fontSize = 16.sp, color = ElectricBlue)
                                Text(
                                    if (p.type == "percentage") "${p.value}% OFF" else "Rp ${fmt.format(p.value)} OFF",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AthleticOrange
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(p.description, fontSize = 13.sp, color = PitchBlack)
                            p.minBookingAmount?.let { min ->
                                if (min > 0) {
                                    Text("Min. booking: Rp ${fmt.format(min)}", fontSize = 11.sp, color = OnSurfaceVariant)
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
fun NotificationsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<NotificationItemDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun load() {
        loading = true
        scope.launch {
            try {
                val res = ApiClient.getService(context).notifications()
                items = res.data
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", fontWeight = FontWeight.Bold, color = PitchBlack) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    TextButton(onClick = {
                        scope.launch {
                            try {
                                ApiClient.getService(context).readAll()
                                load()
                            } catch (_: Exception) {}
                        }
                    }) {
                        Text("Tandai Semua", fontSize = 12.sp, color = ElectricBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricBlue)
            }
        } else if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Tidak ada notifikasi baru.", color = OnSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
                    .padding(padding)
            ) {
                items(items) { notif ->
                    val isUnread = notif.readAt == null
                    val msg = (notif.data?.get("message") as? String) ?: "Notifikasi baru"
                    ListItem(
                        headlineContent = {
                            Text(msg, fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                        },
                        supportingContent = {
                            Text(notif.createdAt ?: "", fontSize = 11.sp, color = OnSurfaceVariant)
                        },
                        leadingContent = {
                            Icon(Icons.Default.Notifications, null, tint = if (isUnread) ElectricBlue else OnSurfaceVariant)
                        },
                        modifier = Modifier
                            .background(if (isUnread) BlueTint.copy(alpha = 0.5f) else Color.White)
                            .clickable {
                                if (isUnread) {
                                    scope.launch {
                                        try {
                                            ApiClient.getService(context).read(notif.id)
                                            load()
                                        } catch (_: Exception) {}
                                    }
                                }
                            }
                    )
                    Divider(color = StadiumGray)
                }
            }
        }
    }
}

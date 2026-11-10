package com.tiketinaja.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiketinaja.app.BuildConfig
import com.tiketinaja.app.data.api.ApiClient
import com.tiketinaja.app.data.api.TokenManager
import com.tiketinaja.app.data.model.LoginRequest
import com.tiketinaja.app.data.model.RegisterRequest
import com.tiketinaja.app.ui.components.AlertModal
import com.tiketinaja.app.ui.theme.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    if (alertMessage != null) {
        AlertModal(
            title = "Peringatan",
            message = alertMessage ?: "",
            onDismiss = { alertMessage = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .widthIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TiketinAja",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = ElectricBlue
        )
        Text(
            text = "Lapangan pilihan. Jadwal di tangan.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Selamat datang kembali",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = PitchBlack
                )
                Text(
                    text = "Masukkan kredensial Anda untuk melanjutkan.",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showPassword) "Sembunyikan password" else "Tampilkan password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            alertMessage = "Email dan password wajib diisi."
                            return@Button
                        }
                        if (password.length < 6) {
                            alertMessage = "Password minimal harus 6 karakter."
                            return@Button
                        }
                        loading = true
                        scope.launch {
                            try {
                                val res = ApiClient.getService(context).login(LoginRequest(email.trim(), password))
                                TokenManager(context).saveToken(res.token)
                                onLoginSuccess()
                            } catch (e: HttpException) {
                                alertMessage = if (e.code() == 401 || e.code() == 422) {
                                    "Login gagal: Email atau password salah."
                                } else {
                                    "Login gagal: Server error (${e.code()})."
                                }
                            } catch (e: IOException) {
                                alertMessage = "Gagal terhubung ke server backend (${BuildConfig.BASE_URL}). Pastikan IP server sesuai dan HP/Emulator terhubung ke jaringan yang sama."
                            } catch (e: Exception) {
                                alertMessage = "Login gagal: ${e.localizedMessage ?: "Terjadi kesalahan."}"
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Masuk Sekarang", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Belum punya akun? ", fontSize = 13.sp, color = OnSurfaceVariant)
            TextButton(onClick = onNavigateToRegister) {
                Text("Daftar Sekarang", fontWeight = FontWeight.Bold, color = ElectricBlue, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    if (alertMessage != null) {
        AlertModal(
            title = "Peringatan",
            message = alertMessage ?: "",
            onDismiss = { alertMessage = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .widthIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Daftar Akun Baru",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PitchBlack
        )
        Text(
            text = "Booking lapangan olahraga dalam hitungan menit.",
            fontSize = 13.sp,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor Telepon (opsional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordConfirmation,
                    onValueChange = { passwordConfirmation = it },
                    label = { Text("Ulangi Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank() || password.isBlank()) {
                            alertMessage = "Semua field wajib diisi."
                            return@Button
                        }
                        if (password != passwordConfirmation) {
                            alertMessage = "Konfirmasi password tidak cocok."
                            return@Button
                        }
                        if (password.length < 8) {
                            alertMessage = "Password minimal 8 karakter."
                            return@Button
                        }
                        loading = true
                        scope.launch {
                            try {
                                val res = ApiClient.getService(context).register(
                                    RegisterRequest(name.trim(), email.trim(), phone.ifBlank { null }, password, passwordConfirmation)
                                )
                                TokenManager(context).saveToken(res.token)
                                onRegisterSuccess()
                            } catch (e: Exception) {
                                alertMessage = "Pendaftaran gagal. Periksa email atau format data."
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Daftar Sekarang", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sudah punya akun? ", fontSize = 13.sp, color = OnSurfaceVariant)
            TextButton(onClick = onNavigateToLogin) {
                Text("Masuk", fontWeight = FontWeight.Bold, color = ElectricBlue, fontSize = 13.sp)
            }
        }
    }
}

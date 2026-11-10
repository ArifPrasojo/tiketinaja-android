package com.tiketinaja.app

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.tiketinaja.app.data.api.TokenManager
import com.tiketinaja.app.ui.screens.*
import com.tiketinaja.app.ui.theme.TiketinAjaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TiketinAjaTheme { AppNavigation() } }
    }
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val start = if (TokenManager(context).getToken().isNullOrBlank()) "login" else "home"
    val back: () -> Unit = { nav.popBackStack() }
    val authenticated: () -> Unit = { nav.navigate("home") { popUpTo(nav.graph.id) { inclusive = true } } }
    NavHost(navController = nav, startDestination = start) {
        composable("login") { LoginScreen(authenticated, { nav.navigate("register") }) }
        composable("register") { RegisterScreen(authenticated, back) }
        composable("home") {
            HomeScreen({ nav.navigate("venues") }, { nav.navigate("bookings") }, { nav.navigate("booking/$it") }, { nav.navigate("notifications") }, { nav.navigate("profile") })
        }
        composable("venues") { VenuesScreen(back, { nav.navigate("venue/$it") }) }
        composable("venue/{id}") { entry ->
            val id = entry.arguments?.getString("id")!!.toLong()
            VenueDetailScreen(id, back) { court, name, price, venue ->
                nav.navigate("court/$court/$id/$price?name=${Uri.encode(name)}&venue=${Uri.encode(venue)}")
            }
        }
        composable("court/{id}/{venueId}/{price}?name={name}&venue={venue}") { entry ->
            val a = entry.arguments!!
            CourtBookingScreen(a.getString("id")!!.toLong(), a.getString("name") ?: "Lapangan", a.getString("price")!!.toLong(), a.getString("venueId")!!.toLong(), a.getString("venue") ?: "Venue", back, { nav.navigate("booking/$it") })
        }
        composable("bookings") { MyBookingsScreen(back, { nav.navigate("booking/$it") }) }
        composable("booking/{id}") { entry -> BookingDetailScreen(entry.arguments!!.getString("id")!!.toLong(), back, { nav.navigate("ticket/$it") }) }
        composable("ticket/{id}") { entry -> TicketQrScreen(entry.arguments!!.getString("id")!!.toLong(), back) }
        composable("notifications") { NotificationsScreen(back) }
        composable("promos") { PromosScreen(onNavigateBack = back) }
        composable("profile") {
            ProfileScreen(back, { nav.navigate("promos") }, { nav.navigate("login") { popUpTo(nav.graph.id) { inclusive = true } } })
        }
    }
}

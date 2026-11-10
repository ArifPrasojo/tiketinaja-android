package com.tiketinaja.app

import com.google.gson.Gson
import com.tiketinaja.app.data.model.*
import org.junit.Assert.*
import org.junit.Test

class ApiContractModelsTest {
    private val gson = Gson()

    @Test
    fun testLoginResponseParsing() {
        val json = """{"token":"1|secret","user":{"id":1,"name":"Budi","email":"b@b.com","phone":"081","role":"customer"}}"""
        val res = gson.fromJson(json, AuthResponse::class.java)
        assertEquals("1|secret", res.token)
        assertEquals("Budi", res.user.name)
        assertEquals("customer", res.user.role)
    }

    @Test
    fun testDashboardResponseParsing() {
        val json = """{
            "success":true,
            "data":{
                "user":{"id":1,"name":"Budi","email":"b@b.com","phone":null,"role":"customer"},
                "stats":{"upcoming_count":2,"total_bookings":5,"total_spent":450000},
                "active_match":{
                    "booking_id":10,
                    "booking_code":"BK-123",
                    "venue_name":"GOR A",
                    "court_name":"Lap 1",
                    "date":"2026-10-19",
                    "start_time":"07:00",
                    "end_time":"08:00",
                    "end_datetime":"2026-10-19T08:00:00Z",
                    "remaining_seconds":1200
                },
                "upcoming_bookings":[],
                "recent_bookings":[]
            }
        }"""
        val res = gson.fromJson(json, CustomerDashboardResponse::class.java)
        assertTrue(res.success)
        assertEquals(2, res.data.stats.upcomingCount)
        assertEquals(10L, res.data.activeMatch?.bookingId)
        assertEquals(1200L, res.data.activeMatch?.remainingSeconds)
    }

    @Test
    fun testTicketResponseParsing() {
        val json = """{
            "success":true,
            "status":"confirmed",
            "is_forfeited":false,
            "message":"Siap",
            "qr_token":"uuid-1234",
            "qr_verify_url":"http://test",
            "is_checked_in":false,
            "checked_in_at":null,
            "checkin_window":{
                "opens_at":"2026-10-19T06:50:00Z",
                "play_time":"2026-10-19T07:00:00Z",
                "end_time":"2026-10-19T08:00:00Z",
                "is_open":true,
                "is_play_time":false,
                "seconds_until_open":0
            }
        }"""
        val res = gson.fromJson(json, TicketResponse::class.java)
        assertTrue(res.success)
        assertEquals("uuid-1234", res.qrToken)
        assertEquals(true, res.checkinWindow?.isOpen)
    }
}

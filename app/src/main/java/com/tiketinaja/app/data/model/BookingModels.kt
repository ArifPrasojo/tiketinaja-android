package com.tiketinaja.app.data.model

import com.google.gson.annotations.SerializedName

data class CreateBookingRequest(
    @SerializedName("court_id") val courtId: Long,
    val date: String,
    @SerializedName("time_slots") val timeSlots: List<String>,
    @SerializedName("promo_code") val promoCode: String?
)

data class CreateBookingResponse(
    val message: String?,
    val booking: BookingDto
)

data class BookingDto(
    val id: Long,
    @SerializedName("booking_code") val bookingCode: String,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("venue_id") val venueId: Long,
    val status: String,
    @SerializedName("total_amount") val totalAmount: Long,
    @SerializedName("discount_amount") val discountAmount: Long,
    @SerializedName("expires_at") val expiresAt: String?,
    @SerializedName("checked_in") val checkedIn: Boolean,
    @SerializedName("checked_in_at") val checkedInAt: String?,
    @SerializedName("qr_code") val qrCode: String?,
    @SerializedName("created_at") val createdAt: String?,
    val venue: VenueDto?,
    val items: List<BookingItemDto>?,
    val payment: PaymentDto?,
    val review: ReviewDto?,
    val promo: PromoDto?
)

data class BookingDetailResponse(
    val booking: BookingDto,
    @SerializedName("snap_token") val snapToken: String?,
    @SerializedName("snap_url") val snapUrl: String?
)

data class BookingItemDto(
    val id: Long,
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("court_id") val courtId: Long,
    @SerializedName("booking_date") val bookingDate: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    val price: Long,
    val court: CourtDto?
)

data class PaymentDto(
    val id: Long?,
    @SerializedName("payment_code") val paymentCode: String?,
    @SerializedName("snap_token") val snapToken: String?,
    val amount: Long,
    @SerializedName("payment_method") val paymentMethod: String?,
    val status: String,
    @SerializedName("paid_at") val paidAt: String?
)

data class PaymentTokenResponse(
    val success: Boolean,
    @SerializedName("snap_token") val snapToken: String,
    @SerializedName("client_key") val clientKey: String?,
    @SerializedName("redirect_url") val redirectUrl: String?,
    @SerializedName("expires_at") val expiresAt: String?
)

data class TicketResponse(
    val success: Boolean,
    val status: String,
    @SerializedName("is_forfeited") val isForfeited: Boolean?,
    val message: String?,
    @SerializedName("qr_token") val qrToken: String?,
    @SerializedName("qr_verify_url") val qrVerifyUrl: String?,
    @SerializedName("is_checked_in") val isCheckedIn: Boolean?,
    @SerializedName("checked_in_at") val checkedInAt: String?,
    @SerializedName("checkin_window") val checkinWindow: CheckinWindowDto?,
    val booking: BookingDto?
)

data class CheckinWindowDto(
    @SerializedName("opens_at") val opensAt: String?,
    @SerializedName("play_time") val playTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("is_open") val isOpen: Boolean,
    @SerializedName("is_play_time") val isPlayTime: Boolean,
    @SerializedName("seconds_until_open") val secondsUntilOpen: Long
)

data class CheckinResultResponse(
    val success: Boolean,
    val message: String?,
    @SerializedName("is_play_time") val isPlayTime: Boolean?,
    @SerializedName("minutes_to_play") val minutesToPlay: Int?,
    val booking: BookingDto?
)

data class PromoDto(
    val id: Long,
    val code: String,
    val description: String,
    val type: String,
    val value: Long,
    @SerializedName("max_uses") val maxUses: Int?,
    @SerializedName("used_count") val usedCount: Int?,
    @SerializedName("min_booking_amount") val minBookingAmount: Long?,
    @SerializedName("max_discount") val maxDiscount: Long?,
    @SerializedName("starts_at") val startsAt: String?,
    @SerializedName("expires_at") val expiresAt: String?,
    @SerializedName("is_active") val isActive: Boolean,
    val venue: VenueDto?
)

data class PromoListResponse(
    val success: Boolean,
    val data: List<PromoDto>
)

data class ValidatePromoRequest(
    val code: String,
    val amount: Long,
    @SerializedName("venue_id") val venueId: Long
)

data class ValidatePromoResponse(
    val valid: Boolean,
    val message: String?,
    @SerializedName("promo_id") val promoId: Long?,
    @SerializedName("discount_amount") val discountAmount: Long?,
    @SerializedName("final_amount") val finalAmount: Long?,
    val description: String?
)

data class ReviewDto(
    val id: Long,
    @SerializedName("booking_id") val bookingId: Long?,
    @SerializedName("venue_id") val venueId: Long?,
    val rating: Int,
    val comment: String?,
    @SerializedName("created_at") val createdAt: String?,
    val user: UserDto?
)

data class ReviewRequest(
    val rating: Int,
    val comment: String?
)

data class ReviewStoreResponse(
    val message: String?,
    val review: ReviewDto?
)

data class NotificationItemDto(
    val id: String,
    val type: String?,
    val data: Map<String, Any?>?,
    @SerializedName("read_at") val readAt: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class UnreadCountResponse(
    val count: Int
)

data class CustomerDashboardResponse(
    val success: Boolean,
    val data: CustomerDashboardData
)

data class CustomerDashboardData(
    val user: UserDto,
    val stats: CustomerStatsDto,
    @SerializedName("active_match") val activeMatch: ActiveMatchDto?,
    @SerializedName("upcoming_bookings") val upcomingBookings: List<BookingDto>,
    @SerializedName("recent_bookings") val recentBookings: List<BookingDto>
)

data class CustomerStatsDto(
    @SerializedName("upcoming_count") val upcomingCount: Int,
    @SerializedName("total_bookings") val totalBookings: Int,
    @SerializedName("total_spent") val totalSpent: Long
)

data class ActiveMatchDto(
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("booking_code") val bookingCode: String,
    @SerializedName("venue_name") val venueName: String?,
    @SerializedName("court_name") val courtName: String?,
    val date: String?,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("end_datetime") val endDatetime: String?,
    @SerializedName("remaining_seconds") val remainingSeconds: Long
)

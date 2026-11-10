package com.tiketinaja.app.data.api

import com.tiketinaja.app.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST("auth/login") suspend fun login(@Body body: LoginRequest): AuthResponse
    @POST("auth/register") suspend fun register(@Body body: RegisterRequest): AuthResponse
    @POST("auth/logout") suspend fun logout(): SimpleMessageResponse
    @GET("auth/me") suspend fun me(): UserDto
    @PUT("auth/me") suspend fun updateProfile(@Body body: UpdateProfileRequest): Map<String, Any?>
    @PUT("auth/password") suspend fun password(@Body body: UpdatePasswordRequest): SimpleMessageResponse
    @HTTP(method = "DELETE", path = "auth/me", hasBody = true) suspend fun deleteAccount(@Body body: DeleteAccountRequest): SimpleMessageResponse
    @GET("customer/dashboard") suspend fun dashboard(): CustomerDashboardResponse
    @GET("venues") suspend fun venues(@Query("search") search: String? = null, @Query("city") city: String? = null, @Query("sport") sport: Long? = null, @Query("page") page: Int = 1): PaginatedResponse<VenueDto>
    @GET("venues/{id}") suspend fun venue(@Path("id") id: Long): VenueDto
    @GET("venues/{id}/reviews") suspend fun venueReviews(@Path("id") id: Long, @Query("page") page: Int = 1): PaginatedResponse<ReviewDto>
    @GET("sports") suspend fun sports(): List<SportDto>
    @GET("cities") suspend fun cities(): List<String>
    @GET("courts/{id}/availability") suspend fun availability(@Path("id") id: Long, @Query("date") date: String): AvailabilityResponse
    @GET("bookings") suspend fun bookings(@Query("status") status: String? = null, @Query("page") page: Int = 1): PaginatedResponse<BookingDto>
    @POST("bookings") suspend fun createBooking(@Body body: CreateBookingRequest): CreateBookingResponse
    @GET("bookings/{id}") suspend fun booking(@Path("id") id: Long): BookingDetailResponse
    @POST("bookings/{id}/cancel") suspend fun cancel(@Path("id") id: Long): SimpleMessageResponse
    @POST("bookings/{id}/payment-token") suspend fun paymentToken(@Path("id") id: Long): PaymentTokenResponse
    @GET("bookings/{id}/ticket") suspend fun ticket(@Path("id") id: Long): TicketResponse
    @POST("bookings/{id}/checkin") suspend fun checkin(@Path("id") id: Long): CheckinResultResponse
    @POST("bookings/{id}/reviews") suspend fun review(@Path("id") id: Long, @Body body: ReviewRequest): ReviewStoreResponse
    @DELETE("bookings/{id}/reviews") suspend fun deleteReview(@Path("id") id: Long): SimpleMessageResponse
    @GET("promos") suspend fun promos(@Query("venue_id") id: Long? = null): PromoListResponse
    @POST("promos/validate") suspend fun validatePromo(@Body body: ValidatePromoRequest): ValidatePromoResponse
    @GET("notifications") suspend fun notifications(@Query("page") page: Int = 1): PaginatedResponse<NotificationItemDto>
    @GET("notifications/unread-count") suspend fun unreadCount(): UnreadCountResponse
    @POST("notifications/{id}/read") suspend fun read(@Path("id") id: String): SimpleMessageResponse
    @POST("notifications/read-all") suspend fun readAll(): SimpleMessageResponse
}

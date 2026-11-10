package com.tiketinaja.app.data.model

import com.google.gson.annotations.SerializedName

data class PaginatedResponse<T>(
    val data: List<T>,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int,
    val total: Int
)

data class VenueDto(
    val id: Long,
    val name: String,
    val slug: String?,
    val city: String,
    val address: String,
    val phone: String?,
    val description: String?,
    val image: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("courts_count") val courtsCount: Int?,
    @SerializedName("reviews_avg_rating") val reviewsAvgRating: Double?,
    val courts: List<CourtDto>?,
    @SerializedName("operating_hours") val operatingHours: List<OperatingHourDto>?
)

data class CourtDto(
    val id: Long,
    @SerializedName("venue_id") val venueId: Long,
    @SerializedName("sport_id") val sportId: Long,
    val name: String,
    val description: String?,
    val facilities: List<String>?,
    val image: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("price_per_hour") val pricePerHour: Long,
    @SerializedName("is_active") val isActive: Boolean,
    val sport: SportDto?
)

data class OperatingHourDto(
    val id: Long?,
    @SerializedName("day_of_week") val dayOfWeek: Int,
    @SerializedName("open_time") val openTime: String?,
    @SerializedName("close_time") val closeTime: String?,
    @SerializedName("is_closed") val isClosed: Boolean
)

data class SportDto(
    val id: Long,
    val name: String,
    val slug: String?,
    val icon: String?
)

data class AvailabilityResponse(
    val court: CourtHeaderDto,
    val date: String,
    val slots: List<TimeSlotDto>
)

data class CourtHeaderDto(
    val id: Long,
    val name: String,
    @SerializedName("price_per_hour") val pricePerHour: Long
)

data class TimeSlotDto(
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    val available: Boolean
)

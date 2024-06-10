package com.example.data.datasource.remote.model.response.coupon

import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val buyQuantity: Int = 0,
    val discount: Int = 0,
    val availableTime: AvailableTime = AvailableTime(),
    val getQuantity: Int = 0,
    val minimumAmount: Int = 0,
)

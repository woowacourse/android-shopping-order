package com.example.data.datasource.remote.model.response.coupon

import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    val id: Int,
    val availableTime: AvailableTime,
    val buyQuantity: Int,
    val code: String,
    val description: String,
    val discount: Int,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int,
    val minimumAmount: Int,
)

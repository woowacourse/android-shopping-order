package com.example.domain.model.coupon.shippingfeepolicy

class FixedShippingFeePolicy(
    private val shippingFee: Int,
) : ShippingFeePolicy {
    override fun getAmount(): Int = shippingFee
}

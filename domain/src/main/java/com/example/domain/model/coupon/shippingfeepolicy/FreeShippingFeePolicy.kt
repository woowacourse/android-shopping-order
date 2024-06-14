package com.example.domain.model.coupon.shippingfeepolicy

object FreeShippingFeePolicy : ShippingFeePolicy by FixedShippingFeePolicy(0)

package com.example.domain.model.coupon.shippingfeepolicy

object DefaultShippingFeePolicy : ShippingFeePolicy by FixedShippingFeePolicy(3000)

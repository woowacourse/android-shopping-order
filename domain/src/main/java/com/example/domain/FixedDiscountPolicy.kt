package com.example.domain

class FixedDiscountPolicy(val fixedDiscountPolicyUnits: List<FixedDiscountPolicyUnit>) {
    var discountPrice = 0

    fun getDiscountPrice(productsSum: Int): Int {
        var tempDiscountPrice = 0

        fixedDiscountPolicyUnits.forEach { discountPolicy ->
            if (discountPolicy.minimumPrice > productsSum) {
                discountPrice = tempDiscountPrice
                return tempDiscountPrice
            }
            tempDiscountPrice = discountPolicy.discountPrice
        }

        discountPrice = tempDiscountPrice
        return tempDiscountPrice
    }

    fun getFinalPrice(productsSum: Int): Int = (productsSum - discountPrice).coerceAtLeast(0)
}

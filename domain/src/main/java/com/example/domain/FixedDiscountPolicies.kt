package com.example.domain

class FixedDiscountPolicies(val fixedDiscountPolicies: List<FixedDiscountPolicy>) {
    var discountPrice = 0

    fun getDiscountPrice(productsSum: Int): Int {
        var tempDiscountPrice = 0

        fixedDiscountPolicies.forEach { discountPolicy ->
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

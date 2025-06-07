package woowacourse.shopping.domain.coupon

class CouponApplierFactory {
    val bogoCouponApplier =
        OrderBasedCouponApplier<BogoCoupon> { origin, order, coupon ->
            val mostExpensiveProduct =
                order
                    .filter { it.quantityValue == coupon.standardQuantity }
                    .maxBy { it.product.priceValue }
                    .product

            val discountPrice = mostExpensiveProduct.priceValue
            val totalPayment = origin.totalPayment - discountPrice
            origin.copy(
                couponDiscount = -discountPrice,
                totalPayment = totalPayment,
            )
        }

    val fixedCouponApplier =
        CouponBasedCouponApplier<FixedCoupon> { origin, coupon ->
            val totalPayment = origin.totalPayment - coupon.discount
            origin.copy(
                couponDiscount = -coupon.discount,
                totalPayment = totalPayment,
            )
        }

    val freeShippingCouponApplier =
        DefaultCouponApplier<FreeshippingCoupon> { origin ->
            val totalPayment = origin.totalPayment - origin.deliveryFee
            origin.copy(
                couponDiscount = -origin.deliveryFee,
                totalPayment = totalPayment,
            )
        }

    val miracleSaleCouponApplier =
        CouponBasedCouponApplier<MiracleSaleCoupon> { origin, coupon ->
            val discount = origin.totalPayment * coupon.discount / 100
            val totalPayment = origin.totalPayment - discount

            origin.copy(
                couponDiscount = discount,
                totalPayment = totalPayment,
            )
        }
}

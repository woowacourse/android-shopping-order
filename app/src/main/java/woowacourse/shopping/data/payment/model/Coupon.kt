package woowacourse.shopping.data.payment.model

data class Coupon(
    val name: String,
    val expirationDate: String,
    val minimumAmount: Int?,
    val isSelected: Boolean = false,
) {
    companion object {
        fun of(couponData: CouponData): Coupon {
            return when (couponData) {
                is CouponData.Fixed5000 ->
                    Coupon("5,000원 할인 쿠폰", couponData.expirationDate, 100_000)

                is CouponData.Bogo ->
                    Coupon("2개 구매 시 1개 무료 쿠폰", couponData.expirationDate, null)

                is CouponData.Freeshipping ->
                    Coupon("5만원 이상 구매 시 무료 배송 쿠폰", couponData.expirationDate, 50_000)

                is CouponData.Miraclesale ->
                    Coupon("미라클모닝 30% 할인 쿠폰", couponData.expirationDate, null)
            }
        }
    }
}

package woowacourse.shopping.domain.model.coupon

sealed class Coupon {
    abstract val code: String

    abstract val description: String

    abstract val discountType: String

    abstract val expirationDate: String

    abstract val id: Long
}

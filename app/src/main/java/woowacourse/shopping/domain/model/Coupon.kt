package woowacourse.shopping.domain.model

sealed interface Coupon {
    val detail: CouponDetail

    val isSelected: Boolean

    fun apply(products: Products): Price

    fun getIsAvailable(products: Products): Boolean

    fun copy(
        detail: CouponDetail = this.detail,
        isSelected: Boolean = this.isSelected,
    ): Coupon
}

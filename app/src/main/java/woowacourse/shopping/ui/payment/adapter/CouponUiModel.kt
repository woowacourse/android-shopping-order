package woowacourse.shopping.ui.payment.adapter

import woowacourse.shopping.domain.model.Coupon

data class CouponUiModel(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int? = null,
    val minimumOrderAmount: Int? = null,
    val isSelected: Boolean = false,
) {
    companion object {
        fun Coupon.toUiModel(isSelected: Boolean = false): CouponUiModel =
            when (this) {
                is Coupon.FixedDiscount ->
                    CouponUiModel(
                        id = id,
                        code = code,
                        description = description,
                        expirationDate = expirationDate,
                        discount = discount,
                        minimumOrderAmount = minimumOrderAmount,
                        isSelected = isSelected,
                    )

                is Coupon.BuyXGetYFree ->
                    CouponUiModel(
                        id = id,
                        code = code,
                        description = description,
                        expirationDate = expirationDate,
                        isSelected = isSelected,
                    )

                is Coupon.FreeShippingOver ->
                    CouponUiModel(
                        id = id,
                        code = code,
                        description = description,
                        expirationDate = expirationDate,
                        minimumOrderAmount = minimumOrderAmount,
                        isSelected = isSelected,
                    )

                is Coupon.PercentDiscount ->
                    CouponUiModel(
                        id = id,
                        code = code,
                        description = description,
                        expirationDate = expirationDate,
                        discount = discount,
                        isSelected = isSelected,
                    )
            }
    }
}

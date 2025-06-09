package woowacourse.shopping.ui.payment.adapter

import woowacourse.shopping.domain.model.CouponDetailInfo

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
        fun CouponDetailInfo.toUiModel(isSelected: Boolean = false): CouponUiModel =
            when (this) {
                is CouponDetailInfo.FixedDiscount ->
                    CouponUiModel(
                        id = id,
                        code = code,
                        description = description,
                        expirationDate = expirationDate,
                        discount = discount,
                        minimumOrderAmount = minimumOrderAmount,
                        isSelected = isSelected,
                    )

                is CouponDetailInfo.BuyXGetYFree ->
                    CouponUiModel(
                        id = id,
                        code = code,
                        description = description,
                        expirationDate = expirationDate,
                        isSelected = isSelected,
                    )

                is CouponDetailInfo.FreeShippingOver ->
                    CouponUiModel(
                        id = id,
                        code = code,
                        description = description,
                        expirationDate = expirationDate,
                        minimumOrderAmount = minimumOrderAmount,
                        isSelected = isSelected,
                    )

                is CouponDetailInfo.PercentDiscount ->
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

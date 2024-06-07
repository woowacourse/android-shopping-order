package woowacourse.shopping.ui.coupon

class CouponUiModels(val uiModels: List<CouponUiModel> = emptyList()) {
    fun isSelect(couponId: Int): Boolean {
        return find(couponId)?.isSelected ?: false
    }

    fun selectCoupon(couponId: Int): CouponUiModels {
        val newUiModels =
            uiModels.map {
                val isSelected = it.couponId == couponId
                it.copy(isSelected = isSelected)
            }
        return CouponUiModels(newUiModels)
    }

    fun unselectCoupon(couponId: Int): CouponUiModels {
        val newUiModels =
            uiModels.map {
                if (it.couponId == couponId) it.copy(isSelected = false) else it
            }
        return CouponUiModels(newUiModels)
    }

    private fun find(couponId: Int): CouponUiModel? = uiModels.find { it.couponId == couponId }
}

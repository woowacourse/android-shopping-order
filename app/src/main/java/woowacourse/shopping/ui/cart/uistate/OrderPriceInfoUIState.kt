package woowacourse.shopping.ui.cart.uistate

import woowacourse.shopping.data.discount.DiscountInfo
import woowacourse.shopping.domain.OrderPriceInfo
import java.lang.Integer.max

data class OrderPriceInfoUIState(
    val discountResult: List<DiscountResultUIState>,
    val originalPrice: Int,
    val orderPrice: Int
) {
    companion object {
        fun create(orderPriceInfo: OrderPriceInfo, originalPrice: Int): OrderPriceInfoUIState {
            val orderPrice = orderPriceInfo.discountResults
                .map { it.discountPrice }
                .fold(originalPrice) { total, discountPrice ->
                    total - discountPrice
                }

            return OrderPriceInfoUIState(
                orderPriceInfo.discountResults.map(DiscountResultUIState::from),
                originalPrice,
                max(0, orderPrice)
            )
        }

        fun create(discountInfo: DiscountInfo, originalPrice: Int): OrderPriceInfoUIState {
            val discountedPrice = discountInfo.calculateDiscountedPrice(originalPrice)
            return OrderPriceInfoUIState(
                discountInfo.discountResults.map(DiscountResultUIState::from),
                originalPrice,
                discountedPrice
            )
        }
    }
}

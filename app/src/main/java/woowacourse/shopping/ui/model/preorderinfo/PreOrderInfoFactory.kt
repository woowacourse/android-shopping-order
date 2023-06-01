package woowacourse.shopping.ui.model.preorderinfo

import woowacourse.shopping.domain.Basket

class PreOrderInfoFactory(private val orderBasket: Basket) {
    fun getPreOrderInfo(): UiPreOrderInfo = UiPreOrderInfo(
        representativeImageUrl = orderBasket.products.first().product.imageUrl,
        representativeExceptCount = orderBasket.getTotalProductsCount(),
        representativeTitle = orderBasket.products.first().product.name,
        orderTotalPrice = orderBasket.getTotalPrice()
    )
}

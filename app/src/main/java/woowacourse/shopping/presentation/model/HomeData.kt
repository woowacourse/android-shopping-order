package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.RecentlyViewedProduct
import woowacourse.shopping.presentation.ui.home.adapter.HomeViewType

sealed interface HomeData {
    val viewType: HomeViewType
}

data class ProductItem(val cartProduct: CartProduct) : HomeData {
    override val viewType: HomeViewType = HomeViewType.PRODUCT
    val cartId: Long get() = cartProduct.cartItem.id
    val productId: Long get() = cartProduct.product.id
    val itemImage: String get() = cartProduct.product.imageUrl
    val name: String get() = cartProduct.product.name
    val price: Int get() = cartProduct.product.price
    val quantity: Int get() = cartProduct.cartItem.quantity
}

data class RecentlyViewedItem(val recentlyViewedProducts: List<RecentlyViewedProduct>) : HomeData {
    override val viewType: HomeViewType = HomeViewType.RECENTLY_VIEWED
}

data class ShowMoreItem(
    override val viewType: HomeViewType = HomeViewType.SHOW_MORE,
) : HomeData

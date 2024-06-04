package woowacourse.shopping.view.state

import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.view.cart.list.ShoppingCartViewItem
import woowacourse.shopping.view.home.product.HomeViewItem

sealed class UIState<out T> {
    data class Success<T>(val data: T) : UIState<T>()

    data object Loading : UIState<Nothing>()

    data class Error(val exception: Throwable) : UIState<Nothing>()
}

data class HomeProductUiState(
    val isLoading: Boolean = true,
    val currentPageNumber: Int = 0,
    val productItems: List<HomeViewItem.ProductViewItem> = emptyList(),
    val cartItems: List<CartItemDomain> = emptyList(),
    val totalCartQuantity: Int = 0,
    val loadMoreAvailable: Boolean = false,
)

data class RecentProductUiState(
    val isLoading: Boolean = true,
    val isEmpty: Boolean = true,
    val productItems: List<RecentProduct> = emptyList(),
)

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: ProductItemDomain? = null,
    val lastlyViewedProduct: RecentProduct? = null,
    val cartItems: List<CartItemDomain> = emptyList(),
    val quantity: Int = 1,
)

data class CartUiState(
    val isCheckboxVisible: Boolean = true,
    val isEntireCheckboxSelected: Boolean = false,
    val totalPrice: Int = 0,
    val isActivated: Boolean = false,
)

data class CartListUiState(
    val isLoading: Boolean = true,
    val currentPageIndex: Int = 0,
    val cartViewItems: List<ShoppingCartViewItem.CartViewItem> = emptyList(),
    val previousPageEnabled: Boolean = true,
    val nextPageEnabled: Boolean = true,
)

data class RecommendListUiState(
    val isLoading: Boolean = true,
    val recommendedProducts: List<HomeViewItem.ProductViewItem> = emptyList(),
)


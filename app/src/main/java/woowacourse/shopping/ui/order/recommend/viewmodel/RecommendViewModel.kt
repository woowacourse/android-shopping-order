package woowacourse.shopping.ui.order.recommend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem
import woowacourse.shopping.ui.home.viewmodel.HomeViewModel
import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.ui.order.recommend.action.RecommendNavigationActions
import woowacourse.shopping.ui.order.recommend.listener.RecommendClickListener
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.state.UiState
import kotlin.math.min

class RecommendViewModel(
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderViewModel: OrderViewModel,
) : ViewModel(), RecommendClickListener {
    private val _recommendUiState =
        MutableLiveData<UiState<List<HomeViewItem.ProductViewItem>>>(UiState.Loading)
    val recommendUiState: LiveData<UiState<List<HomeViewItem.ProductViewItem>>>
        get() = _recommendUiState

    private val _recommendNavigationActions = MutableLiveData<Event<RecommendNavigationActions>>()
    val recommendNavigationActions: LiveData<Event<RecommendNavigationActions>>
        get() = _recommendNavigationActions

    fun generateRecommendProductViewItems() {
        runCatching {
            val mostRecentProduct = recentProductRepository.findMostRecentProduct()
            productRepository.getProducts(
                mostRecentProduct?.category,
                0,
                Int.MAX_VALUE,
                HomeViewModel.ASCENDING_SORT_ORDER,
            )
        }.onSuccess { productResponse ->
            val selectedProducts =
                orderViewModel.selectedCartViewItems.value?.map { selectedCartViewItem -> selectedCartViewItem.cartItem.product }
                    ?: return@onSuccess
            var sameCategoryProducts =
                productResponse.getOrNull()?.products
                    ?: return@onSuccess
            sameCategoryProducts =
                sameCategoryProducts.filter { sameCategoryProduct ->
                    !selectedProducts.contains(sameCategoryProduct)
                }.shuffled()

            val numberOfRecommend =
                min(OrderViewModel.DEFAULT_NUMBER_OF_RECOMMEND, sameCategoryProducts.size)
            val recommendProductViewItems =
                sameCategoryProducts.subList(0, numberOfRecommend)
                    .map(HomeViewItem::ProductViewItem)
            _recommendUiState.value = UiState.Success(recommendProductViewItems)
        }
    }

    override fun onProductClick(productId: Int) {
        _recommendNavigationActions.value =
            Event(RecommendNavigationActions.NavigateToDetail(productId))
    }

    override fun onPlusButtonClick(product: Product) {
        val recommendState = recommendUiState.value
        if (recommendState is UiState.Success) {
            var cartItemId: Int = -1
            runCatching {
                cartItemId = cartRepository.addCartItem(product.productId, 1).getOrNull() ?: return
            }.onSuccess {
                var updatedCartViewItem = CartViewItem(CartItem(cartItemId, 1, product))
                updatedCartViewItem = updatedCartViewItem.check()

                val newCatViewItems =
                    orderViewModel.cartViewItems.value?.toMutableList() ?: return
                newCatViewItems.add(updatedCartViewItem)
                orderViewModel.updateCartViewItems(newCatViewItems)

                val newSelectedCatViewItems =
                    orderViewModel.selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems.add(updatedCartViewItem)
                orderViewModel.updateSelectedCartViewItems(newSelectedCatViewItems)

                val recommendProductViewItems = recommendState.data
                val recommendPosition =
                    recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                        recommendProductViewItem.product.productId == product.productId
                    }
                val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
                newRecommendProductViewItems[recommendPosition] =
                    HomeViewItem.ProductViewItem(product, 1)
                _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
            }
        }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        orderViewModel.onQuantityPlusButtonClick(productId)

        val updatedCartViewItem = orderViewModel.getCartViewItemByProductId(productId) ?: return

        val recommendState = recommendUiState.value
        if (recommendState is UiState.Success) {
            val recommendProductViewItems = recommendState.data
            val recommendPosition =
                recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                    recommendProductViewItem.product.productId == updatedCartViewItem.cartItem.product.productId
                }
            val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
            newRecommendProductViewItems[recommendPosition] =
                HomeViewItem.ProductViewItem(
                    updatedCartViewItem.cartItem.product,
                    updatedCartViewItem.cartItem.quantity,
                )
            _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
        }
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        val updatedCartItem = orderViewModel.getCartViewItemByProductId(productId) ?: return

        orderViewModel.onQuantityMinusButtonClick(productId)

        val recommendState = recommendUiState.value
        if (updatedCartItem.cartItem.quantity >= 1 && recommendState is UiState.Success) {
            val recommendProductViewItems = recommendState.data
            val recommendPosition =
                recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                    recommendProductViewItem.product.productId == updatedCartItem.cartItem.product.productId
                }
            val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
            newRecommendProductViewItems[recommendPosition] =
                HomeViewItem.ProductViewItem(
                    updatedCartItem.cartItem.product,
                    updatedCartItem.cartItem.quantity - 1,
                )
            _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
        }
    }
}

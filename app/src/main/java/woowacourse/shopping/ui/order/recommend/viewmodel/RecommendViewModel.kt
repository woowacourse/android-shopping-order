package woowacourse.shopping.ui.order.recommend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem.ProductViewItem
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
        MutableLiveData<UiState<List<ProductViewItem>>>(UiState.Loading)
    val recommendUiState: LiveData<UiState<List<ProductViewItem>>>
        get() = _recommendUiState

    private val _recommendProductViewItems = MutableLiveData<List<ProductViewItem>>(emptyList())
    val recommendProductViewItems: LiveData<List<ProductViewItem>>
        get() = _recommendProductViewItems

    private val _recommendNavigationActions = MutableLiveData<Event<RecommendNavigationActions>>()
    val recommendNavigationActions: LiveData<Event<RecommendNavigationActions>>
        get() = _recommendNavigationActions

    fun updateRecommendProductViewItems() {
        if (recommendUiState.value is UiState.Success) {
            viewModelScope.launch {
                val totalQuantity = cartRepository.getCartTotalQuantity().getOrNull() ?: 0
                cartRepository.getCartItems(0, totalQuantity, OrderViewModel.DESCENDING_SORT_ORDER)
                    .onSuccess { cartItems ->
                        val recommendProductIds =
                            _recommendProductViewItems.value?.map { recommendProductViewItem -> recommendProductViewItem.product.productId }
                                ?: return@onSuccess

                        val updatedRecommendProductViewItems =
                            cartItems.filter { cartItem ->
                                recommendProductIds.contains(cartItem.product.productId)
                            }.map { updatedCartItem ->
                                ProductViewItem(updatedCartItem.product, updatedCartItem.quantity)
                            }

                        val newRecommendProductViewItems =
                            recommendProductViewItems.value?.toMutableList() ?: return@onSuccess

                        updatedRecommendProductViewItems.forEach { updatedRecommendProductViewItem ->
                            val updatePosition =
                                newRecommendProductViewItems.indexOfFirst {
                                    it.product.productId == updatedRecommendProductViewItem.product.productId
                                }
                            if (updatePosition != -1) {
                                newRecommendProductViewItems[updatePosition] =
                                    updatedRecommendProductViewItem
                            }
                        }

                        val newCartViewItems =
                            cartItems.map { cartItem ->
                                CartViewItem(cartItem).check()
                            }
                        orderViewModel.updateSelectedCartViewItems(newCartViewItems)

                        _recommendProductViewItems.value = newRecommendProductViewItems
                        _recommendUiState.value =
                            UiState.Success(recommendProductViewItems.value ?: emptyList())
                    }
            }
        }
    }

    fun generateRecommendProductViewItems() {
        viewModelScope.launch {
            val mostRecentProductCategory =
                recentProductRepository.findMostRecentProduct().getOrNull()?.category
                    ?: return@launch
            productRepository.getProducts(
                mostRecentProductCategory,
                0,
                Int.MAX_VALUE,
                HomeViewModel.ASCENDING_SORT_ORDER,
            ).onSuccess {
                val selectedProducts =
                    orderViewModel.selectedCartViewItems.value?.map { selectedCartViewItem -> selectedCartViewItem.cartItem.product }
                        ?: return@onSuccess
                var sameCategoryProducts = it.products
                sameCategoryProducts =
                    sameCategoryProducts.filter { sameCategoryProduct ->
                        !selectedProducts.contains(sameCategoryProduct)
                    }.shuffled()

                val numberOfRecommend =
                    min(OrderViewModel.DEFAULT_NUMBER_OF_RECOMMEND, sameCategoryProducts.size)
                _recommendProductViewItems.value =
                    sameCategoryProducts.subList(0, numberOfRecommend)
                        .map(HomeViewItem::ProductViewItem)
                _recommendUiState.value =
                    UiState.Success(recommendProductViewItems.value ?: emptyList())
            }
        }
    }

    override fun onProductClick(productId: Int) {
        _recommendNavigationActions.value =
            Event(RecommendNavigationActions.NavigateToDetail(productId))
    }

    override fun onPlusButtonClick(product: Product) {
        val recommendState = recommendUiState.value
        if (recommendState is UiState.Success) {
            viewModelScope.launch {
                cartRepository.addCartItem(product.productId, 1)
                    .onSuccess { cartItemId ->
                        var updatedCartViewItem = CartViewItem(CartItem(cartItemId, 1, product))
                        updatedCartViewItem = updatedCartViewItem.check()

                        val newCatViewItems =
                            orderViewModel.cartViewItems.value?.toMutableList() ?: return@onSuccess
                        newCatViewItems.add(updatedCartViewItem)
                        orderViewModel.updateCartViewItems(newCatViewItems)

                        val newSelectedCatViewItems =
                            orderViewModel.selectedCartViewItems.value?.toMutableList()
                                ?: return@onSuccess
                        newSelectedCatViewItems.add(updatedCartViewItem)
                        orderViewModel.updateSelectedCartViewItems(newSelectedCatViewItems)

                        val recommendProductViewItems = recommendState.data
                        val recommendPosition =
                            recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                                recommendProductViewItem.product.productId == product.productId
                            }
                        val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
                        newRecommendProductViewItems[recommendPosition] =
                            ProductViewItem(product, 1)
                        _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
                    }
            }
        }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
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
                ProductViewItem(
                    updatedCartViewItem.cartItem.product,
                    updatedCartViewItem.cartItem.quantity + 1,
                )
            _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
        }

        orderViewModel.onQuantityPlusButtonClick(productId)
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        val updatedCartItem = orderViewModel.getCartViewItemByProductId(productId) ?: return

        val recommendState = recommendUiState.value
        if (updatedCartItem.cartItem.quantity >= 1 && recommendState is UiState.Success) {
            val recommendProductViewItems = recommendState.data
            val recommendPosition =
                recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                    recommendProductViewItem.product.productId == updatedCartItem.cartItem.product.productId
                }
            val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
            newRecommendProductViewItems[recommendPosition] =
                ProductViewItem(
                    updatedCartItem.cartItem.product,
                    updatedCartItem.cartItem.quantity - 1,
                )
            _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
        }

        orderViewModel.onQuantityMinusButtonClick(productId)
    }
}

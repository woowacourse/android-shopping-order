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
import woowacourse.shopping.ui.order.recommend.action.RecommendNotifyingActions
import woowacourse.shopping.ui.order.recommend.action.RecommendShareActions
import woowacourse.shopping.ui.order.recommend.listener.RecommendClickListener
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.state.UiState
import kotlin.math.min

class RecommendViewModel(
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), RecommendClickListener {
    private val _recommendUiState =
        MutableLiveData<UiState<List<ProductViewItem>>>(UiState.Loading)
    val recommendUiState: LiveData<UiState<List<ProductViewItem>>>
        get() = _recommendUiState

    private var recommendProductViewItems: List<ProductViewItem> = emptyList()

    private var sharedCartViewItems: List<CartViewItem> = emptyList()

    private val _recommendShareActions = MutableLiveData<Event<RecommendShareActions>>()
    val recommendShareActions: LiveData<Event<RecommendShareActions>>
        get() = _recommendShareActions

    private val _recommendNavigationActions = MutableLiveData<Event<RecommendNavigationActions>>()
    val recommendNavigationActions: LiveData<Event<RecommendNavigationActions>>
        get() = _recommendNavigationActions

    private val _recommendNotifyingActions = MutableLiveData<Event<RecommendNotifyingActions>>()
    val recommendNotifyingActions: LiveData<Event<RecommendNotifyingActions>>
        get() = _recommendNotifyingActions

    init {
        generateRecommendProductViewItems()
    }

    fun updateRecommendProductViewItems() {
        if (recommendUiState.value is UiState.Success) {
            viewModelScope.launch {
                val totalQuantity = cartRepository.getCartTotalQuantity().getOrNull() ?: 0
                cartRepository.getCartItems(0, totalQuantity, OrderViewModel.DESCENDING_SORT_ORDER)
                    .onSuccess { cartItems ->
                        val recommendProductIds =
                            recommendProductViewItems.map { recommendProductViewItem -> recommendProductViewItem.product.productId }

                        val updatedRecommendProductViewItems =
                            cartItems.filter { cartItem ->
                                recommendProductIds.contains(cartItem.product.productId)
                            }.map { updatedCartItem ->
                                ProductViewItem(updatedCartItem.product, updatedCartItem.quantity)
                            }

                        val newRecommendProductViewItems =
                            recommendProductViewItems.toMutableList()

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
                        _recommendShareActions.value =
                            Event(RecommendShareActions.UpdateNewCartViewItems(newCartViewItems))

                        _recommendUiState.value =
                            UiState.Success(newRecommendProductViewItems)
                    }.onFailure {
                        _recommendNotifyingActions.value =
                            Event(RecommendNotifyingActions.NotifyError)
                    }
            }
        }
    }

    fun updateSharedCartViewItems(cartViewItems: List<CartViewItem>) {
        this.sharedCartViewItems = cartViewItems
    }

    fun generateRecommendProductViewItems() {
        viewModelScope.launch {
            val mostRecentProductCategory =
                recentProductRepository.findMostRecentProduct().getOrNull()?.category
            productRepository.getProducts(
                mostRecentProductCategory,
                0,
                Int.MAX_VALUE,
                HomeViewModel.ASCENDING_SORT_ORDER,
            ).onSuccess {
                val selectedProducts =
                    sharedCartViewItems.map { selectedCartViewItem -> selectedCartViewItem.cartItem.product }

                var sameCategoryProducts = it.products
                sameCategoryProducts =
                    sameCategoryProducts.filter { sameCategoryProduct ->
                        !selectedProducts.contains(sameCategoryProduct)
                    }.shuffled()

                val numberOfRecommend =
                    min(OrderViewModel.DEFAULT_NUMBER_OF_RECOMMEND, sameCategoryProducts.size)
                recommendProductViewItems =
                    sameCategoryProducts.subList(0, numberOfRecommend)
                        .map(HomeViewItem::ProductViewItem)
                _recommendUiState.value =
                    UiState.Success(recommendProductViewItems)
            }.onFailure {
                _recommendNotifyingActions.value =
                    Event(RecommendNotifyingActions.NotifyError)
            }
        }
    }

    private fun updateRecommendStateQuantity(
        recommendProductViewItems: List<ProductViewItem>,
        updatedCartViewItem: CartViewItem,
        updateQuantity: Int,
    ) {
        val recommendPosition =
            recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                recommendProductViewItem.product.productId == updatedCartViewItem.cartItem.product.productId
            }
        val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
        newRecommendProductViewItems[recommendPosition] =
            ProductViewItem(
                updatedCartViewItem.cartItem.product,
                updatedCartViewItem.cartItem.quantity + updateQuantity,
            )
        _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
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

                        val newCartViewItems = sharedCartViewItems.toMutableList()
                        newCartViewItems.add(updatedCartViewItem)
                        _recommendShareActions.value =
                            Event(RecommendShareActions.UpdateNewCartViewItems(newCartViewItems))

                        val recommendProductViewItems = recommendState.data
                        val recommendPosition =
                            recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                                recommendProductViewItem.product.productId == product.productId
                            }
                        val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
                        newRecommendProductViewItems[recommendPosition] =
                            ProductViewItem(product, 1)
                        _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
                    }.onFailure {
                        _recommendNotifyingActions.value =
                            Event(RecommendNotifyingActions.NotifyError)
                    }
            }
        }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        val updatedCartViewItem =
            sharedCartViewItems.firstOrNull { cartViewItem ->
                cartViewItem.cartItem.product.productId == productId
            } ?: return

        val recommendState = recommendUiState.value
        if (recommendState is UiState.Success) {
            updateRecommendStateQuantity(recommendState.data, updatedCartViewItem, 1)
        }

        _recommendShareActions.value =
            Event(RecommendShareActions.PlusCartViewItemQuantity(productId))
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        val updatedCartViewItem =
            sharedCartViewItems.firstOrNull { cartViewItem ->
                cartViewItem.cartItem.product.productId == productId
            } ?: return

        val recommendState = recommendUiState.value
        if (updatedCartViewItem.cartItem.quantity >= 1 && recommendState is UiState.Success) {
            updateRecommendStateQuantity(recommendState.data, updatedCartViewItem, -1)
        }

        _recommendShareActions.value =
            Event(RecommendShareActions.MinusCartViewItemQuantity(productId))
    }
}

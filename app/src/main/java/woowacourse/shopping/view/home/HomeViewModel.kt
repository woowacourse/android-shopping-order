package woowacourse.shopping.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.model.toProduct
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.home.product.HomeViewItem
import woowacourse.shopping.view.state.HomeProductUiState
import woowacourse.shopping.view.state.HomeUiEvent
import woowacourse.shopping.view.state.RecentProductUiState

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(), HomeEventListener, QuantityEventListener {
    private val _homeProductUiState: MutableLiveData<HomeProductUiState> =
        MutableLiveData(HomeProductUiState())
    val homeProductUiState: LiveData<HomeProductUiState>
        get() = _homeProductUiState

    private val _recentProductUiState: MutableLiveData<RecentProductUiState> =
        MutableLiveData(RecentProductUiState())
    val recentProductUiState: LiveData<RecentProductUiState>
        get() = _recentProductUiState

    private val _homeUiEvent: MutableLiveData<Event<HomeUiEvent>> = MutableLiveData()
    val homeUiEvent: LiveData<Event<HomeUiEvent>>
        get() = _homeUiEvent

    init {
        loadRecentItems()
        loadProducts()
    }

    override fun addQuantity(cartItemId: Int) {
        val targetCartItem =
            homeProductUiState.value?.cartItems?.firstOrNull { it.product.id == cartItemId }
                ?: return
        val updatedQuantity = targetCartItem.quantity + 1
        updateQuantity(targetCartItem, updatedQuantity, cartItemId)
    }

    override fun subtractQuantity(cartItemId: Int) {
        val targetCartItem =
            homeProductUiState.value?.cartItems?.firstOrNull { it.product.id == cartItemId }
                ?: return
        val updatedQuantity = targetCartItem.quantity - 1
        if (updatedQuantity == 0) {
            cartRepository.deleteCartItem(
                cartItemId = targetCartItem.cartItemId,
                onSuccess = {
                    val cartItems = homeProductUiState.value?.cartItems?.filter {
                        it.product.id != cartItemId
                    } ?: return@deleteCartItem

                    val productItems = homeProductUiState.value?.productItems?.map {
                        if (it.product.id == cartItemId) {
                            it.copy(_quantity = updatedQuantity)
                        } else it
                    } ?: return@deleteCartItem

                    _homeProductUiState.value = homeProductUiState.value?.copy(
                        productItems = productItems,
                        cartItems = cartItems,
                        totalCartQuantity = cartItems.sumOf { it.quantity }
                    )
                },
                onFailure = {

                },
            )
        } else {
            updateQuantity(targetCartItem, updatedQuantity, cartItemId)
        }
    }

    private fun updateQuantity(
        targetCartItem: CartItemDomain,
        updatedQuantity: Int,
        productId: Int
    ) {
        println("$targetCartItem : $updatedQuantity")
        cartRepository.updateCartItem(
            cartItemId = targetCartItem.cartItemId,
            quantity = updatedQuantity,
            onSuccess = {
                println("successful - $targetCartItem : $updatedQuantity")
                val cartItems = homeProductUiState.value?.cartItems?.map {
                    if (it.product.id == productId) {
                        it.copy(quantity = updatedQuantity)
                    } else it
                } ?: return@updateCartItem

                val productItems = homeProductUiState.value?.productItems?.map {
                    if (it.product.id == productId) {
                        it.copy(_quantity = updatedQuantity)
                    } else it
                } ?: return@updateCartItem

                _homeProductUiState.value = homeProductUiState.value?.copy(
                    productItems = productItems,
                    cartItems = cartItems,
                    totalCartQuantity = cartItems.sumOf { it.quantity }
                )
            },
            onFailure = {

            },
        )
    }

    override fun navigateToDetail(productId: Int) {
        recentProductRepository.save(
            homeProductUiState.value?.productItems?.firstOrNull { it.product.id == productId }?.product?.toProduct()
                ?: return
        )
        _homeUiEvent.value = Event(HomeUiEvent.NavigateToDetail(productId))
    }

    override fun navigateToCart() {
        _homeUiEvent.value = Event(HomeUiEvent.NavigateToCart)
    }

    override fun loadMore() {
        loadProducts()
    }

    override fun addToCart(product: ProductItemDomain) {
        cartRepository.addCartItem(
            productId = product.id,
            quantity = 1,
            onSuccess = {
                cartRepository.getCartItems(
                    0,
                    homeProductUiState.value?.totalCartQuantity?.plus(1) ?: return@addCartItem,
                    DEFAULT_SORT_ORDER,
                    onSuccess = { cart ->
                        val cartItem = cart.cartItems.firstOrNull { it.product.id == product.id }
                            ?: return@getCartItems
                        val productItems = homeProductUiState.value?.productItems?.map {
                            if (it.product.id == cartItem.product.id) {
                                it.copy(_quantity = 1)
                            } else {
                                it
                            }
                        } ?: return@getCartItems
                        _homeProductUiState.value = homeProductUiState.value?.copy(
                            totalCartQuantity = homeProductUiState.value?.totalCartQuantity?.plus(1)
                                ?: return@getCartItems,
                            cartItems = cart.cartItems,
                            productItems = productItems
                        )
                    },
                    onFailure = {

                    },
                )
            },
            onFailure = {

            },
        )
    }

    fun loadRecentItems() {
        val recentProductItems = recentProductRepository.findAll(RECENT_PRODUCTS_LIMIT)
        _recentProductUiState.value = recentProductUiState.value?.copy(
            isLoading = false,
            isEmpty = recentProductItems.isEmpty(),
            productItems = recentProductItems
        )
    }

    private fun loadProducts() {
        productRepository.getProducts(
            page = homeProductUiState.value?.currentPageNumber ?: 0,
            size = PAGE_SIZE,
            category = CATEGORY_UNDEFINED,
            sort = DEFAULT_SORT_ORDER,
            onSuccess = ::addProductsWithQuantity,
            onFailure = {

            }
        )
    }

    private fun addProductsWithQuantity(productDomain: ProductDomain) {
        cartRepository.getCartTotalQuantity(
            onSuccess = { totalQuantity ->
                placeQuantitiesToProductItem(productDomain, totalQuantity)
            },
            onFailure = {

            }
        )
    }

    private fun placeQuantitiesToProductItem(productDomain: ProductDomain, totalQuantity: Int) {
        cartRepository.getCartItems(
            0,
            totalQuantity,
            DEFAULT_SORT_ORDER,
            onSuccess = { cart ->
                val productItems = productDomain.products.map { product ->
                    val quantity =
                        cart.cartItems.firstOrNull { it.product.id == product.id }?.quantity ?: 0
                    HomeViewItem.ProductViewItem(product, quantity)
                }
                _homeProductUiState.value =
                    homeProductUiState.value?.copy(
                        isLoading = false,
                        totalCartQuantity = totalQuantity,
                        currentPageNumber = homeProductUiState.value?.currentPageNumber?.plus(1)
                            ?: return@getCartItems,
                        productItems = homeProductUiState.value?.productItems?.plus(productItems)
                            ?: return@getCartItems,
                        cartItems = cart.cartItems,
                        loadMoreAvailable = productDomain.last.not()
                    )
            },
            onFailure = {

            },
        )
    }

    companion object {
        private val CATEGORY_UNDEFINED: String? = null
        private const val DEFAULT_SORT_ORDER = "asc"
        private const val PAGE_SIZE = 20
        private const val RECENT_PRODUCTS_LIMIT = 10
    }
}

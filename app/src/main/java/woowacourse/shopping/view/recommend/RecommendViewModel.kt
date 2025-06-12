package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductRecommendationStrategy
import woowacourse.shopping.domain.product.RecentViewedCategoryBasedStrategy

class RecommendViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private var recentProducts: List<Product> = emptyList()

    private var categoryProducts: List<Product> = emptyList()

    private var cartItems: List<CartItem> = emptyList()

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData()
    val totalPrice: LiveData<Int> = _totalPrice

    private val _totalQuantity: MutableLiveData<Int> = MutableLiveData()
    val totalQuantity: LiveData<Int> = _totalQuantity

    private val _recommendProducts: MutableLiveData<List<RecommendProduct>> = MutableLiveData()
    val recommendProducts: LiveData<List<RecommendProduct>> = _recommendProducts

    private val _event: MutableLiveData<RecommendEvent> = MutableLiveData()
    val event: LiveData<RecommendEvent> = _event

    private var _selectedItems: List<CartItem> = emptyList()
    val selectedItems: List<CartItem> get() = _selectedItems

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            runCatching {
                cartRepository.loadCart()
            }.onSuccess { cartItems: List<CartItem> ->
                this@RecommendViewModel.cartItems = cartItems
                loadRecentProducts()
            }.onFailure {
                _event.postValue(RecommendEvent.LOAD_SHOPPING_CART_FAILURE)
            }
        }
    }

    private fun loadProductsByCategory() {
        viewModelScope.launch {
            runCatching {
                productsRepository.loadProductsByCategory(
                    recentProducts.first().category,
                )
            }.onSuccess { categoryProducts ->
                this@RecommendViewModel.categoryProducts = categoryProducts
                _recommendProducts.postValue(
                    turnToRecommendProducts(RecentViewedCategoryBasedStrategy()),
                )
            }.onFailure {
                _event.postValue(RecommendEvent.LOAD_PRODUCT_FAILURE)
            }
        }
    }

    fun loadTotal(
        selectedItems: List<CartItem>
    ) {
        _selectedItems = selectedItems
        _totalQuantity.postValue(selectedItems.sumOf { it.quantity })
        _totalPrice.postValue(selectedItems.sumOf { it.price })
    }

    fun plusCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            val cartItemId = cartItems.find { it.productId == productId }?.id
            runCatching {
                if (cartItemId == null) {
                    cartRepository.addCartItem(productId, quantity)
                } else {
                    cartRepository.updateCartItemQuantity(cartItemId, quantity)
                }
            }.onSuccess {
                loadCart()
            }.onFailure {
                _event.postValue(RecommendEvent.PLUS_CART_ITEM_FAILURE)
            }
        }
    }

    fun minusCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            val cartItemId = cartItems.find { it.productId == productId }?.id

            if (cartItemId == null) {
                _event.postValue(
                    if (quantity == 0) {
                        RecommendEvent.REMOVE_CART_ITEM_FAILURE
                    } else {
                        RecommendEvent.MINUS_CART_ITEM_FAILURE
                    }
                )
                return@launch
            }

            runCatching {
                if (quantity == 0) {
                    cartRepository.remove(cartItemId)
                } else {
                    cartRepository.updateCartItemQuantity(cartItemId, quantity)
                }
            }.onSuccess {
                loadCart()
            }.onFailure {
                _event.postValue(
                    if (quantity == 0) {
                        RecommendEvent.REMOVE_CART_ITEM_FAILURE
                    } else {
                        RecommendEvent.MINUS_CART_ITEM_FAILURE
                    },
                )
            }
        }
    }

    private fun loadRecentProducts() {
        viewModelScope.launch {
            runCatching {
                productsRepository.loadRecentViewedProducts()
            }.onSuccess { products: List<Product> ->
                recentProducts = products
                loadProductsByCategory()
            }.onFailure {
                _event.postValue(RecommendEvent.LOAD_RECENT_PRODUCTS_FAILURE)
            }
        }
    }

    private fun turnToRecommendProducts(productRecommendationStrategy: ProductRecommendationStrategy): List<RecommendProduct> {
        val products: List<Product> =
            productRecommendationStrategy.recommendedProducts(
                products = categoryProducts,
                prohibitedProducts = recentProducts,
            )

        return products.map { product: Product ->
            RecommendProduct(
                cartItems
                    .find { cartItem: CartItem ->
                        cartItem.productId == product.id
                    }?.quantity ?: 0,
                product,
            )
        }
    }
}

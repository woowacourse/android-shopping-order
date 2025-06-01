package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductsRecommendAlgorithm
import woowacourse.shopping.domain.product.RecentViewedCategoryBasedAlgorithm

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

    init {
        loadCart()
    }

    private fun loadCart() {
        cartRepository.loadCart { result ->
            result
                .onSuccess { cartItems: List<CartItem> ->
                    this.cartItems = cartItems
                    _totalPrice.postValue(cartItems.sumOf { cartItem: CartItem -> cartItem.price })
                    _totalQuantity.postValue(cartItems.sumOf { cartItems: CartItem -> cartItems.quantity })
                    loadRecentProducts()
                }.onFailure {
                    _event.postValue(RecommendEvent.LOAD_SHOPPING_CART_FAILURE)
                }
        }
    }

    private fun loadProductsByCategory() {
        productsRepository.loadProductsByCategory(
            recentProducts.first().category,
        ) { result ->
            result
                .onSuccess { categoryProducts ->
                    this.categoryProducts = categoryProducts
                    _recommendProducts.postValue(
                        turnToRecommendProducts(
                            RecentViewedCategoryBasedAlgorithm(),
                        ),
                    )
                }.onFailure {
                    _event.postValue(RecommendEvent.LOAD_ALL_PRODUCTS_FAILURE)
                }
        }
    }

    fun plusCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartItemId = cartItems.find { it.productId == productId }?.id
        if (cartItemId == null) {
            cartRepository.addCartItem(productId, quantity) { result ->
                result
                    .onSuccess {
                        loadCart()
                    }.onFailure {
                        _event.postValue(RecommendEvent.ADD_CART_ITEM_FAILURE)
                    }
            }
        } else {
            cartRepository.updateCartItemQuantity(cartItemId, quantity) { result ->
                result
                    .onSuccess {
                        loadCart()
                    }.onFailure {
                        _event.postValue(RecommendEvent.PLUS_CART_ITEM_FAILURE)
                    }
            }
        }
    }

    fun minusCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartItemId = cartItems.find { it.productId == productId }?.id ?: error("")
        if (quantity == 0) {
            cartRepository.remove(
                cartItemId = cartItemId,
            ) { result ->
                result
                    .onSuccess {
                        loadCart()
                    }.onFailure {
                        _event.postValue(RecommendEvent.REMOVE_CART_ITEM_FAILURE)
                    }
            }
        } else {
            cartRepository.updateCartItemQuantity(cartItemId, quantity) { result ->
                result
                    .onSuccess {
                        loadCart()
                    }.onFailure {
                        _event.postValue(RecommendEvent.MINUS_CART_ITEM_FAILURE)
                    }
            }
        }
    }

    private fun loadRecentProducts() {
        productsRepository.loadRecentViewedProducts { result ->
            result
                .onSuccess { products: List<Product> ->
                    recentProducts = products
                    loadProductsByCategory()
                }.onFailure {
                    _event.postValue(RecommendEvent.LOAD_RECOMMEND_PRODUCTS_FAILURE)
                }
        }
    }

    private fun turnToRecommendProducts(productsRecommendAlgorithm: ProductsRecommendAlgorithm): List<RecommendProduct> {
        val products: List<Product> =
            productsRecommendAlgorithm.recommendedProducts(
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

package woowacourse.shopping.view.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.PagedProducts
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class ProductsViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private val _cartItemsSize: MutableLiveData<Int> = MutableLiveData(0)
    val cartItemsSize: LiveData<Int> get() = _cartItemsSize

    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductsEvent> get() = _event

    private val _productsItems: MutableLiveData<List<ProductsItem>> = MutableLiveData(emptyList())
    val productsItems: LiveData<List<ProductsItem>> get() = _productsItems

    val loading: MutableLiveData<Boolean> = MutableLiveData(true)

    private var cartItems: List<CartItem> = emptyList()

    private var page: Int = MIN_PAGE

    init {
        loadCart()
    }

    fun loadCart() {
        cartRepository.loadCart { result ->
            result
                .onSuccess { cartItems: List<CartItem> ->
                    this.cartItems = cartItems
                    _cartItemsSize.postValue(cartItems.sumOf { it.quantity })
                    loadRecentViewedProducts()
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_SHOPPING_CART_FAILURE)
                }
        }
    }

    private fun loadRecentViewedProducts() {
        productsRepository.loadRecentViewedProducts { result ->
            result
                .onSuccess { products: List<Product> ->
                    val recentViewedProductsItem = ProductsItem.RecentViewedProductsItem(products)
                    loadProducts(recentViewedProductsItem)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_RECENT_PRODUCTS_FAILURE)
                    loadProducts(ProductsItem.RecentViewedProductsItem(emptyList()))
                }
        }
    }

    private fun loadProducts(recentViewedProductsItem: ProductsItem.RecentViewedProductsItem) {
        productsRepository.loadPagedProducts(
            page = page,
            size = LOAD_PRODUCTS_SIZE,
        ) { result ->
            result
                .onSuccess { pagedProducts: PagedProducts ->
                    val productItems =
                        pagedProducts.products.map { product: Product ->
                            ProductsItem.ProductItem(
                                product = product,
                                quantity =
                                    cartItems
                                        .find { cartItem: CartItem ->
                                            cartItem.productId == product.id
                                        }?.quantity ?: 0,
                            )
                        }
                    val loadItem = ProductsItem.LoadItem(pagedProducts.loadable)
                    _productsItems.postValue(listOf(recentViewedProductsItem) + productItems + loadItem)
                    Thread.sleep(1000)
                    loading.postValue(false)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_MORE_PRODUCT_FAILURE)
                }
        }
    }

    fun loadMoreProducts() {
        productsRepository.loadPagedProducts(
            ++page,
            LOAD_PRODUCTS_SIZE,
        ) { result: Result<PagedProducts> ->
            result
                .onSuccess { pagedProducts: PagedProducts ->
                    val recentViewedProductsItem =
                        productsItems.value?.first() ?: ProductsItem.RecentViewedProductsItem(
                            emptyList(),
                        )
                    val oldProductItems =
                        productsItems.value?.filterIsInstance<ProductsItem.ProductItem>()
                            ?: emptyList()
                    val productItems =
                        pagedProducts.products.map { product: Product ->
                            ProductsItem.ProductItem(
                                product = product,
                                quantity =
                                    cartItems
                                        .find { cartItem: CartItem ->
                                            cartItem.productId == product.id
                                        }?.quantity ?: 0,
                            )
                        }
                    val loadItem = ProductsItem.LoadItem(pagedProducts.loadable)

                    _productsItems.postValue(listOf(recentViewedProductsItem) + oldProductItems + productItems + loadItem)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_MORE_PRODUCT_FAILURE)
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
                        _event.postValue(ProductsEvent.ADD_CART_ITEM_FAILURE)
                    }
            }
        } else {
            cartRepository.updateCartItemQuantity(cartItemId, quantity) { result ->
                result
                    .onSuccess {
                        loadCart()
                    }.onFailure {
                        _event.postValue(ProductsEvent.PLUS_CART_ITEM_FAILURE)
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
                        _event.postValue(ProductsEvent.REMOVE_CART_ITEM_FAILURE)
                    }
            }
        } else {
            cartRepository.updateCartItemQuantity(cartItemId, quantity) { result ->
                result
                    .onSuccess {
                        loadCart()
                    }.onFailure {
                        _event.postValue(ProductsEvent.MINUS_CART_ITEM_FAILURE)
                    }
            }
        }
    }

    companion object {
        private const val LOAD_PRODUCTS_SIZE = 20
        private const val MIN_PAGE = 0
    }
}

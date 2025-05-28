package woowacourse.shopping.view.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.CartRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultCartRepository
import woowacourse.shopping.domain.product.CartItem
import woowacourse.shopping.domain.product.PageableProducts
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class ProductsViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private val _cartItemsSize: MutableLiveData<Int> = MutableLiveData(MIN_PAGE)
    val cartItemsSize: LiveData<Int> get() = _cartItemsSize

    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductsEvent> get() = _event

    private val _productsItems: MutableLiveData<List<ProductsItem>> = MutableLiveData(emptyList())
    val productsItems: LiveData<List<ProductsItem>> get() = _productsItems

    private var cartItems: List<CartItem> = emptyList()

    private var page: Int = MIN_PAGE

    init {
        loadCartItemsSize()
        loadCart()
    }

    private fun loadCartItemsSize() {
        cartRepository.cartItemsSize { result ->
            result
                .onSuccess { size ->
                    _cartItemsSize.postValue(size)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_SHOPPING_CART_QUANTITY_FAILURE)
                }
        }
    }

    private fun loadCart() {
        cartRepository.loadCart { result ->
            result
                .onSuccess { cartItems: List<CartItem> ->
                    this.cartItems = cartItems
                    loadRecentViewedProducts()
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_SHOPPING_CART_FAILURE)
                }
        }
    }

    fun loadRecentViewedProducts() {
        productsRepository.loadRecentViewedProducts { result ->
            result
                .onSuccess { products: List<Product> ->
                    val recentViewedProductsItem = ProductsItem.RecentViewedProductsItem(products)
                    loadProducts(recentViewedProductsItem)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_RECENT_PRODUCTS_FAILURE)
                }
        }
    }

    fun loadProducts(recentViewedProductsItem: ProductsItem.RecentViewedProductsItem) {
        productsRepository.loadPageableProducts(
            page = page,
            size = LOAD_PRODUCTS_SIZE,
        ) { result ->
            result
                .onSuccess { pageableProducts: PageableProducts ->
                    val productItems =
                        pageableProducts.products.map { product: Product ->
                            ProductsItem.ProductItem(
                                product = product,
                                quantity =
                                    cartItems
                                        .find { cartItem: CartItem ->
                                            cartItem.productId == product.id
                                        }?.quantity ?: 0,
                            )
                        }
                    val loadItem = ProductsItem.LoadItem(pageableProducts.loadable)
                    _productsItems.postValue(listOf(recentViewedProductsItem) + productItems + loadItem)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_MORE_PRODUCT_FAILURE)
                }
        }
    }

    fun loadMoreProducts() {
        productsRepository.loadPageableProducts(
            ++page,
            LOAD_PRODUCTS_SIZE,
        ) { result: Result<PageableProducts> ->
            result
                .onSuccess { pageableProducts: PageableProducts ->
                    val recentViewedProductsItem =
                        productsItems.value?.first() ?: ProductsItem.RecentViewedProductsItem(
                            emptyList(),
                        )
                    val oldProductItems =
                        productsItems.value?.filterIsInstance<ProductsItem.ProductItem>()
                            ?: emptyList()
                    val productItems =
                        pageableProducts.products.map { product: Product ->
                            ProductsItem.ProductItem(
                                product = product,
                                quantity =
                                    cartItems
                                        .find { cartItem: CartItem ->
                                            cartItem.productId == product.id
                                        }?.quantity ?: 0,
                            )
                        }
                    val loadItem = ProductsItem.LoadItem(pageableProducts.loadable)

                    _productsItems.postValue(listOf(recentViewedProductsItem) + oldProductItems + productItems + loadItem)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_MORE_PRODUCT_FAILURE)
                }
        }
    }

    fun getCartItemId(product: Product): Long = 0L

    fun updateRecentViewedProducts() {
        TODO("Not yet implemented")
    }

    //    private var shoppingCart: List<CartItem> = emptyList()
//
//    private val _shoppingCartSize: MutableLiveData<Int> = MutableLiveData(0)
//    val shoppingCartSize: LiveData<Int> get() = _shoppingCartSize
//
//    private val _productItems: MutableLiveData<List<ProductsItem>> = MutableLiveData(emptyList())
//    val productItems: LiveData<List<ProductsItem>> get() = _productItems
//
//    private val nextPage: Int get() = (_productItems.value?.size ?: 0) / LOAD_PRODUCTS_SIZE
//
//    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
//    val event: SingleLiveData<ProductsEvent> get() = _event
//
//    private var recentProducts: RecentViewedProductsItem = RecentViewedProductsItem(emptyList())
//
//    init {
//        loadViewedRecentProducts()
//    }
//
//    private fun loadViewedRecentProducts() {
//        productsRepository.loadRecentViewedProducts { products ->
//            products
//                .onSuccess { recentViewedProducts ->
//                    recentProducts = RecentViewedProductsItem(recentViewedProducts)
//                    loadMoreProducts()
//                }.onFailure {
//                    _event.postValue(ProductsEvent.LOAD_RECENT_PRODUCTS_FAILURE)
//                }
//        }
//    }
//
//    fun updateRecentViewedProducts() {
//        productsRepository.loadRecentViewedProducts { products ->
//            products
//                .onSuccess { recentViewedProducts ->
//                    val lastList = productItems.value?.minus(recentProducts) ?: emptyList()
//                    recentProducts = RecentViewedProductsItem(recentViewedProducts)
//                    _productItems.postValue(listOf(recentProducts) + lastList)
//                }.onFailure {
//                    _event.postValue(ProductsEvent.LOAD_RECENT_PRODUCTS_FAILURE)
//                }
//        }
//    }
//
//    fun loadMoreProducts() {
//        productsRepository.loadPageableProducts(
//            nextPage,
//            LOAD_PRODUCTS_SIZE,
//        ) { result: Result<PageableProducts> ->
//            result
//                .onSuccess { pageableProducts: PageableProducts ->
//                    val recentViewedProducts = recentProducts
//                    val newProductItems: List<ProductItem> =
//                        pageableProducts.products.map { product: Product ->
//                            val quantity: Int =
//                                shoppingCart.find { it.productId == product.id }?.quantity ?: 0
//                            ProductItem(product, quantity)
//                        }
//                    val currentProductItems: List<ProductItem> =
//                        productItems.value?.filterIsInstance<ProductItem>() ?: emptyList()
//
//                    _productItems.postValue(
//                        listOf(recentViewedProducts) + currentProductItems + newProductItems +
//                            listOf(LoadItem(pageableProducts.loadable)),
//                    )
//                    loadCartItemQuantity()
//                }.onFailure {
//                    _productItems.postValue(emptyList())
//                    _event.postValue(ProductsEvent.UPDATE_PRODUCT_FAILURE)
//                }
//        }
//    }
//
//    private fun loadCartItemQuantity() {
//        shoppingCartRepository.cartItemsSize { result: Result<Int> ->
//            result
//                .onSuccess { quantity: Int ->
//                    _shoppingCartSize.postValue(quantity)
//                    loadShoppingCart(quantity)
//                }.onFailure {
//                    _event.postValue(ProductsEvent.LOAD_SHOPPING_CART_QUANTITY_FAILURE)
//                }
//        }
//    }
//
//    private fun loadShoppingCart(size: Int) {
//        shoppingCartRepository.loadPageableCartItems(0, size) { result: Result<PageableCartItems> ->
//            result
//                .onSuccess { pageableCartItems: PageableCartItems ->
//                    shoppingCart = pageableCartItems.cartItems
//                }.onFailure {
//                    shoppingCart = emptyList()
//                }
//        }
//    }
//
//    fun getCartItemId(product: Product): Long {
//        shoppingCart.find {
//            it.productId == product.id
//        }
//        TODO()
//    }
//
    companion object {
        private const val LOAD_PRODUCTS_SIZE = 20
        private const val LOAD_RECENT_VIEWED_PRODUCTS_SIZE = 10
        private const val MIN_PAGE = 0
    }
}

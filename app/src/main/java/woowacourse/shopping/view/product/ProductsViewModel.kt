package woowacourse.shopping.view.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.cart.PageableCartItems
import woowacourse.shopping.domain.product.CartItem
import woowacourse.shopping.domain.product.PageableProducts
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData
import woowacourse.shopping.view.product.ProductsItem.LoadItem
import woowacourse.shopping.view.product.ProductsItem.ProductItem
import woowacourse.shopping.view.product.ProductsItem.RecentViewedProductsItem

class ProductsViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository(),
) : ViewModel() {
    private var shoppingCart: List<CartItem> = emptyList()

    private val _shoppingCartSize: MutableLiveData<Int> = MutableLiveData(0)
    val shoppingCartSize: LiveData<Int> get() = _shoppingCartSize

    private val _productItems: MutableLiveData<List<ProductsItem>> = MutableLiveData(emptyList())
    val productItems: LiveData<List<ProductsItem>> get() = _productItems

    private val nextPage: Int get() = (_productItems.value?.size ?: 0) / LOAD_PRODUCTS_SIZE

    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductsEvent> get() = _event

    private var recentProducts: RecentViewedProductsItem = RecentViewedProductsItem(emptyList())

    init {
        loadViewedRecentProducts()
    }

    private fun loadViewedRecentProducts() {
        productsRepository.loadLastViewedProducts { products ->
            products
                .onSuccess { recentViewedProducts ->
                    recentProducts = RecentViewedProductsItem(recentViewedProducts)
                    loadMoreProducts()
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_RECENT_PRODUCTS_FAILURE)
                }
        }
    }

    fun updateRecentViewedProducts() {
        productsRepository.loadLastViewedProducts { products ->
            products
                .onSuccess { recentViewedProducts ->
                    val lastList = productItems.value?.minus(recentProducts) ?: emptyList()
                    recentProducts = RecentViewedProductsItem(recentViewedProducts)
                    _productItems.postValue(listOf(recentProducts) + lastList)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_RECENT_PRODUCTS_FAILURE)
                }
        }
    }

    fun loadMoreProducts() {
        productsRepository.load(
            nextPage,
            LOAD_PRODUCTS_SIZE,
        ) { result: Result<PageableProducts> ->
            result
                .onSuccess { pageableProducts: PageableProducts ->
                    val recentViewedProducts = recentProducts
                    val newProductItems: List<ProductItem> =
                        pageableProducts.products.map { product: Product ->
                            val quantity: Int =
                                shoppingCart.find { it.productId == product.id }?.quantity ?: 0
                            ProductItem(product, quantity)
                        }
                    val currentProductItems: List<ProductItem> =
                        productItems.value?.filterIsInstance<ProductItem>() ?: emptyList()

                    _productItems.postValue(
                        listOf(recentViewedProducts) + currentProductItems + newProductItems +
                            listOf(LoadItem(pageableProducts.hasNext)),
                    )
                    loadCartItemQuantity()
                }.onFailure {
                    _productItems.postValue(emptyList())
                    _event.postValue(ProductsEvent.UPDATE_PRODUCT_FAILURE)
                }
        }
    }

    private fun loadCartItemQuantity() {
        shoppingCartRepository.quantity { result: Result<Int> ->
            result
                .onSuccess { quantity: Int ->
                    _shoppingCartSize.postValue(quantity)
                    loadShoppingCart(quantity)
                }.onFailure {
                    _event.postValue(ProductsEvent.LOAD_SHOPPING_CART_QUANTITY_FAILURE)
                }
        }
    }

    private fun loadShoppingCart(size: Int) {
        shoppingCartRepository.load(0, size) { result: Result<PageableCartItems> ->
            result
                .onSuccess { pageableCartItems: PageableCartItems ->
                    shoppingCart = pageableCartItems.cartItems
                }.onFailure {
                    shoppingCart = emptyList()
                }
        }
    }

    fun updateShoppingCart(onUpdate: () -> Unit) {
        val productItems: List<ProductItem> =
            productItems.value?.filterIsInstance<ProductItem>() ?: return

        val cartItemsToUpdate: List<CartItem> =
            productItems
                .map { productItem: ProductItem ->
                    CartItem(productItem.product, productItem.quantity)
                }.filter { cartItem -> cartItem.quantity != 0 }

        shoppingCartRepository.update(cartItemsToUpdate) { result: Result<Unit> ->
            result
                .onSuccess {
                }.onFailure {
                    _event.postValue(ProductsEvent.UPDATE_PRODUCT_FAILURE)
                }
            onUpdate()
        }
    }

    fun getCartItemId(product: Product): Long {
        shoppingCart.find {
            it.productId == product.id
        }
    }

    companion object {
        private const val LOAD_PRODUCTS_SIZE = 20
        private const val LOAD_RECENT_VIEWED_PRODUCTS_SIZE = 10
    }
}

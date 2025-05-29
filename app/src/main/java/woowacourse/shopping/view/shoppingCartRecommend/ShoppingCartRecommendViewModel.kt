package woowacourse.shopping.view.shoppingCartRecommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.product.ProductsItem

class ShoppingCartRecommendViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
    private val productsRepository: ProductsRepository = DefaultProductsRepository.get(),
) : ViewModel() {
    private val _shoppingCartProductsToOrder: MutableLiveData<List<ShoppingCartProduct>> =
        MutableLiveData()
    val shoppingCartProductsToOrder: LiveData<List<ShoppingCartProduct>> get() = _shoppingCartProductsToOrder

    private val _totalPrice: MediatorLiveData<Int> = MediatorLiveData<Int>().apply { value = 0 }
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalQuantity: MediatorLiveData<Int> = MediatorLiveData<Int>().apply { value = 0 }
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private val _recommendProducts: MutableLiveData<List<ProductsItem.ProductItem>> =
        MutableLiveData()
    val recommendProducts: LiveData<List<ProductsItem.ProductItem>> get() = _recommendProducts

    private var recentWatchingProducts: List<Product> = emptyList()
    private var shoppingCartProducts: List<ShoppingCartProduct> = emptyList()

    init {
        _totalPrice.addSource(_shoppingCartProductsToOrder) { it ->
            _totalPrice.value =
                it
                    .sumOf { item -> item.price }
        }

        _totalQuantity.addSource(_shoppingCartProductsToOrder) {
            _totalQuantity.value =
                it
                    .sumOf { item -> item.quantity }
        }

        loadRecentWatchingProducts()
    }

    private fun loadRecentWatchingProducts() {
        productsRepository.getRecentRecommendWatchingProducts(MAX_RECENT_PRODUCT_LOAD_SIZE) { result ->
            result
                .onSuccess { products ->
                    recentWatchingProducts = products
                    loadShoppingCartProducts()
                }
        }
    }

    private fun loadShoppingCartProducts() {
        shoppingCartRepository.load(0, MAX_RECENT_PRODUCT_LOAD_SIZE) { result ->
            result
                .onSuccess { shoppingCarts ->
                    shoppingCartProducts = shoppingCarts.shoppingCartItems
                    getRecommendProducts()
                }
        }
    }

    private fun getRecommendProducts() {
        val cartProductIds: Set<Long> = shoppingCartProducts.map { it.product.id }.toSet()
        val recommended =
            recentWatchingProducts
                .filter { !cartProductIds.contains(it.id) }
                .map { ProductsItem.ProductItem(product = it) }
                .take(10)
        _recommendProducts.value = recommended
    }

    fun updateShoppingCartProductsToOrder(shoppingCartProductsToOrder: List<ShoppingCartProduct>) {
        _shoppingCartProductsToOrder.value = shoppingCartProductsToOrder
    }

    fun addProductToShoppingCart(
        item: ProductsItem.ProductItem,
        selectedQuantity: Int,
    ) {
    }

    fun minusProductToShoppingCart(
        item: ProductsItem.ProductItem,
        selectedQuantity: Int,
    ) {
    }

    companion object {
        private const val MAX_RECENT_PRODUCT_LOAD_SIZE = Int.MAX_VALUE
    }
}

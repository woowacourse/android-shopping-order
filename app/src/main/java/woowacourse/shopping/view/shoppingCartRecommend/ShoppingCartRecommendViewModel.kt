package woowacourse.shopping.view.shoppingCartRecommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.product.ProductsItem

class ShoppingCartRecommendViewModel(
    shoppingCartProductsToOrderList: List<ShoppingCartProduct>,
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
    private val productsRepository: ProductsRepository = DefaultProductsRepository.get(),
) : ViewModel() {
    private val _shoppingCartProductsToOrder: MutableLiveData<List<ShoppingCartProduct>> =
        MutableLiveData()
    val shoppingCartProductsToOrder: LiveData<List<ShoppingCartProduct>> get() = _shoppingCartProductsToOrder

    val totalPrice: LiveData<Int> get() = _shoppingCartProductsToOrder.map { item -> item.sumOf { it.price } }
    val totalQuantity: LiveData<Int> get() = _shoppingCartProductsToOrder.map { item -> item.sumOf { it.quantity } }

    private val _recommendProducts: MutableLiveData<List<ProductsItem.ProductItem>> =
        MutableLiveData()
    val recommendProducts: LiveData<List<ProductsItem.ProductItem>> get() = _recommendProducts

    init {
        _shoppingCartProductsToOrder.value = shoppingCartProductsToOrderList
        initRecentWatchingProducts()
    }

    private fun initRecentWatchingProducts() {
        viewModelScope.launch {
            val products =
                productsRepository.getRecentRecommendWatchingProducts(MAX_RECENT_PRODUCT_LOAD_SIZE)
            val shoppingCarts = shoppingCartRepository.load(0, MAX_RECENT_PRODUCT_LOAD_SIZE)

            val cartProductIds: Set<Long> =
                shoppingCarts.shoppingCartItems.map { it.product.id }.toSet()
            val recommended =
                products
                    .filter { !cartProductIds.contains(it.id) }
                    .map { ProductsItem.ProductItem(product = it) }
                    .take(10)
            _recommendProducts.value = recommended
        }
    }

    fun addProductToShoppingCart(
        item: ProductsItem.ProductItem,
        selectedQuantity: Int,
    ) {
        updateRecommendProducts(item, selectedQuantity + 1)
    }

    fun minusProductToShoppingCart(
        item: ProductsItem.ProductItem,
        selectedQuantity: Int,
    ) {
        updateRecommendProducts(item, selectedQuantity - 1)
    }

    private fun loadShoppingCartProducts(uploaded: ShoppingCartProduct) {
        viewModelScope.launch {
            val currentList = _shoppingCartProductsToOrder.value.orEmpty().toMutableList()

            val existingIndex =
                currentList.indexOfFirst { it.product.id == uploaded.product.id }

            if (existingIndex >= 0) {
                currentList[existingIndex] = uploaded
            } else {
                currentList.add(uploaded)
            }

            _shoppingCartProductsToOrder.value = currentList
            _recommendProducts.value =
                _recommendProducts.value?.map { item ->
                    if (item.product.id == uploaded.product.id) {
                        item.copy(
                            shoppingCartId = uploaded.id,
                            selectedQuantity = uploaded.quantity,
                        )
                    } else {
                        item
                    }
                }
        }
    }

    private fun updateRecommendProducts(
        item: ProductsItem.ProductItem,
        selectedQuantity: Int,
    ) {
        viewModelScope.launch {
            val uploaded =
                if (item.shoppingCartId == null) {
                    shoppingCartRepository.add(item.product, selectedQuantity)
                } else {
                    shoppingCartRepository.updateQuantity(item.shoppingCartId, selectedQuantity)
                }

            uploaded?.let {
                loadShoppingCartProducts(it)
            } ?: run {
                removeRecommendProduct(item)
            }
        }
    }

    private fun removeRecommendProduct(item: ProductsItem.ProductItem) {
        val newList =
            _shoppingCartProductsToOrder.value.orEmpty().filter {
                it.product.id != item.product.id
            }
        _shoppingCartProductsToOrder.value = newList
        _recommendProducts.value =
            _recommendProducts.value?.map {
                if (it.product.id == item.product.id) {
                    it.copy(selectedQuantity = 0)
                } else {
                    it
                }
            }
    }

    companion object {
        private const val MAX_RECENT_PRODUCT_LOAD_SIZE = Int.MAX_VALUE

        fun factory(shoppingCartProductsToOrder: List<ShoppingCartProduct>): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    ShoppingCartRecommendViewModel(
                        shoppingCartProductsToOrder,
                    )
                }
            }
    }
}

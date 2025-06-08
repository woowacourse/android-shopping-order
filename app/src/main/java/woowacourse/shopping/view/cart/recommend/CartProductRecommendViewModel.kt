package woowacourse.shopping.view.cart.recommend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.recommend.adapter.RecommendedProductItem
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class CartProductRecommendViewModel(
    selectedProducts: Set<CartProduct>,
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    CartProductRecommendEventHandler {
    val cartProducts = MutableLiveData(selectedProducts)

    private val _recommendedProducts = MutableLiveData<List<RecommendedProductItem>>()
    val recommendedProducts: LiveData<List<RecommendedProductItem>> get() = _recommendedProducts

    val totalPrice: LiveData<Int> =
        cartProducts.map { products ->
            products.sumOf { it.totalPrice }
        }

    val totalCount: LiveData<Int> =
        cartProducts.map { products ->
            products.sumOf { it.quantity }
        }

    private val _selectedProduct = MutableSingleLiveData<Product>()
    val selectedProduct: SingleLiveData<Product> get() = _selectedProduct

    init {
        viewModelScope.launch {
            cartProductRepository
                .getPagedProducts()
                .onSuccess {
                    loadRecommendedProducts(it.items)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    private fun loadRecommendedProducts(cartProducts: List<CartProduct>) {
        viewModelScope.launch {
            recentProductRepository
                .getLastViewedProduct()
                .onSuccess { recentProduct ->
                    if (recentProduct == null) return@launch
                    productRepository
                        .getPagedProducts()
                        .onSuccess { products ->
                            val cartProductIds = cartProducts.map { it.product.id }.toSet()
                            val recommended =
                                products.items
                                    .asSequence()
                                    .filter { it.category == recentProduct.product.category }
                                    .filter { it.id !in cartProductIds }
                                    .shuffled()
                                    .take(RECOMMEND_SIZE)
                                    .map { RecommendedProductItem(it) }
                                    .toList()

                            _recommendedProducts.postValue(recommended)
                        }.onFailure {
                            Log.e("error", it.message.toString())
                        }
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onProductClick(item: Product) {
        _selectedProduct.setValue(item)
    }

    override fun onPlusClick(item: Product) {
        viewModelScope.launch {
            cartProductRepository
                .insert(item.id, QUANTITY_TO_ADD)
                .onSuccess { cartProductId ->
                    val currentProducts = cartProducts.value.orEmpty()
                    val newItem = CartProduct(cartProductId, item, QUANTITY_TO_ADD)
                    cartProducts.postValue(currentProducts + newItem)
                    updateProductQuantity(item, QUANTITY_TO_ADD)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onQuantityIncreaseClick(item: Product) {
        updateCartProduct(item, QUANTITY_TO_ADD)
    }

    override fun onQuantityDecreaseClick(item: Product) {
        updateCartProduct(item, -QUANTITY_TO_ADD)
    }

    private fun updateCartProduct(
        item: Product,
        quantityDelta: Int,
    ) {
        viewModelScope.launch {
            val existing =
                cartProducts.value.orEmpty().firstOrNull { it.product.id == item.id }
                    ?: return@launch
            val newQuantity = existing.quantity + quantityDelta

            cartProductRepository
                .updateQuantity(existing, quantityDelta)
                .onSuccess {
                    val updatedList = cartProducts.value.orEmpty().toMutableSet()
                    updatedList.removeIf { it.product.id == item.id }
                    if (newQuantity > DEFAULT_COUNT) {
                        updatedList.add(existing.copy(quantity = newQuantity))
                    }
                    cartProducts.postValue(updatedList)
                    updateProductQuantity(item, quantityDelta)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    private fun updateProductQuantity(
        item: Product,
        quantityToAdd: Int,
    ) {
        val updatedList =
            recommendedProducts.value.orEmpty().map {
                if (it.product.id == item.id) it.copy(quantity = it.quantity + quantityToAdd) else it
            }
        _recommendedProducts.postValue(updatedList)
    }

    companion object {
        private const val RECOMMEND_SIZE = 10
        private const val QUANTITY_TO_ADD = 1
        private const val DEFAULT_COUNT = 0
    }
}

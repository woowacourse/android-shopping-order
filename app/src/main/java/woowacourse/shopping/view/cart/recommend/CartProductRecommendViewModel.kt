package woowacourse.shopping.view.cart.recommend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    CartProductRecommendEventHandler {
    private val cartProducts = mutableSetOf<CartProduct>()

    private val _recommendedProducts = MutableLiveData<List<RecommendedProductItem>>()
    val recommendedProducts: LiveData<List<RecommendedProductItem>> get() = _recommendedProducts

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> get() = _totalCount

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

    fun initShoppingCartInfo(
        cartProducts: Set<CartProduct>,
        totalPrice: Int?,
        totalCount: Int?,
    ) {
        this.cartProducts.addAll(cartProducts)
        _totalPrice.value = totalPrice ?: DEFAULT_PRICE
        _totalCount.value = totalCount ?: DEFAULT_COUNT
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
                    cartProducts.add(CartProduct(cartProductId, item, QUANTITY_TO_ADD))
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
            val existing = cartProducts.firstOrNull { it.product.id == item.id } ?: return@launch
            val newQuantity = existing.quantity + quantityDelta

            cartProductRepository
                .updateQuantity(existing, quantityDelta)
                .onSuccess {
                    cartProducts.removeIf { it.product.id == item.id }
                    if (newQuantity > DEFAULT_COUNT) {
                        cartProducts.add(existing.copy(quantity = newQuantity))
                    }
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
        _totalCount.postValue((totalCount.value ?: DEFAULT_COUNT) + quantityToAdd)
        _totalPrice.value = totalPrice.value?.plus(item.price * quantityToAdd)
    }

    companion object {
        private const val RECOMMEND_SIZE = 10
        private const val QUANTITY_TO_ADD = 1
        private const val DEFAULT_COUNT = 0
        private const val DEFAULT_PRICE = 0
    }
}

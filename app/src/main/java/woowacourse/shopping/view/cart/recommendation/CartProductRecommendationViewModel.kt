package woowacourse.shopping.view.cart.recommendation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.recommendation.adapter.ProductItem
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class CartProductRecommendationViewModel(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    CartProductRecommendationEventHandler {
    private val cartProducts: MutableSet<CartProduct> = mutableSetOf()

    private val _recommendedProducts = MutableLiveData<List<ProductItem>>()
    val recommendedProducts: LiveData<List<ProductItem>> get() = _recommendedProducts

    private val selectedCartIds: MutableSet<Int> = mutableSetOf()

    private val _totalPrice = MediatorLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalCount = MediatorLiveData<Int>()
    val totalCount: LiveData<Int> get() = _totalCount

    private val _selectedProduct = MutableSingleLiveData<Product>()
    val selectedProduct: SingleLiveData<Product> get() = _selectedProduct

    init {
        cartProductRepository.getPagedProducts { result ->
            result
                .onSuccess {
                    cartProducts.addAll(it.items)
                    loadRecommendedProducts()
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    fun initShoppingCartInfo(
        selectedIds: Set<Int>,
        totalPrice: Int?,
        totalCount: Int?,
    ) {
        selectedCartIds.addAll(selectedIds)
        _totalPrice.value = totalPrice ?: DEFAULT_PRICE
        _totalCount.value = totalCount ?: DEFAULT_COUNT

        _totalPrice.removeSource(_recommendedProducts)
        _totalCount.removeSource(_recommendedProducts)

        _totalPrice.addSource(_recommendedProducts) { list ->
            _totalPrice.value =
                totalPrice?.plus(list.sumOf { it.product.price * it.quantity })
        }
        _totalCount.addSource(_recommendedProducts) { list ->
            _totalCount.value = totalCount?.plus(list.sumOf { it.quantity })
        }
    }

    private fun loadRecommendedProducts() {
        recentProductRepository.getLastViewedProduct { result ->
            result
                .onSuccess { recentProduct ->
                    recentProduct ?: return@getLastViewedProduct
                    productRepository.getPagedProducts { result ->
                        result
                            .onSuccess { products ->
                                val cartProductIds =
                                    cartProducts.map { it.product.id }.toSet()
                                val recommended =
                                    products.items
                                        .asSequence()
                                        .filter { it.category == recentProduct.product.category }
                                        .filter { it.id !in cartProductIds }
                                        .take(RECOMMEND_SIZE)
                                        .map { ProductItem(it) }
                                        .toList()

                                _recommendedProducts.postValue(recommended)
                            }.onFailure {
                                Log.e("error", it.message.toString())
                            }
                    }
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onProductClick(item: Product) {
        _selectedProduct.setValue(item)
    }

    override fun onAddClick(item: Product) {
        cartProductRepository.insert(item.id, QUANTITY_TO_ADD) { result ->
            result
                .onSuccess { cartProductId ->
                    cartProducts.add(CartProduct(cartProductId, item, QUANTITY_TO_ADD))
                    selectedCartIds.add(cartProductId)
                    updateQuantity(item, QUANTITY_TO_ADD)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onQuantityIncreaseClick(item: Product) {
        val cartProduct = cartProducts.firstOrNull { it.product.id == item.id } ?: return
        cartProductRepository.updateQuantity(cartProduct, cartProduct.quantity + QUANTITY_TO_ADD) {
            cartProducts.remove(cartProduct)
            cartProducts.add(cartProduct.copy(quantity = cartProduct.quantity + QUANTITY_TO_ADD))
            updateQuantity(item, QUANTITY_TO_ADD)
        }
    }

    override fun onQuantityDecreaseClick(item: Product) {
        val cartProduct = cartProducts.firstOrNull { it.product.id == item.id } ?: return
        cartProductRepository.updateQuantity(cartProduct, cartProduct.quantity - QUANTITY_TO_ADD) {
            cartProducts.remove(cartProduct)
            val newQuantity = cartProduct.quantity - QUANTITY_TO_ADD
            if (newQuantity > DEFAULT_COUNT) {
                cartProducts.add(cartProduct.copy(quantity = newQuantity))
            } else {
                selectedCartIds.remove(cartProduct.id)
            }
            updateQuantity(item, -QUANTITY_TO_ADD)
        }
    }

    private fun updateQuantity(
        item: Product,
        quantityToAdd: Int,
    ) {
        val updatedList =
            _recommendedProducts.value.orEmpty().map { recommendedProduct ->
                if (recommendedProduct.product.id == item.id) {
                    recommendedProduct.copy(quantity = recommendedProduct.quantity + quantityToAdd)
                } else {
                    recommendedProduct
                }
            }

        _recommendedProducts.postValue(updatedList)
    }

    fun finishOrder() {
        selectedCartIds.forEach { id ->
            cartProductRepository.delete(id) { result ->
                result.onFailure {
                    Log.e("error", it.message.toString())
                }
            }
        }
        cartProductRepository.deleteAll(selectedCartIds) { result ->
            result
                .onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    companion object {
        private const val RECOMMEND_SIZE = 10
        private const val QUANTITY_TO_ADD = 1
        private const val DEFAULT_COUNT = 0
        private const val DEFAULT_PRICE = 0
    }
}

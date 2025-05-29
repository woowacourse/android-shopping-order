package woowacourse.shopping.view.cart.recommendation

import androidx.lifecycle.LiveData
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

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> get() = _totalCount

    private val _selectedProduct = MutableSingleLiveData<Product>()
    val selectedProduct: SingleLiveData<Product> get() = _selectedProduct

    init {
        cartProductRepository.getPagedProducts { result ->
            cartProducts.addAll(result.items)
            loadRecommendedProducts()
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
    }

    private fun loadRecommendedProducts() {
        recentProductRepository.getLastViewedProduct { recentProduct ->
            recentProduct ?: return@getLastViewedProduct
            productRepository.getPagedProducts { products ->
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
            }
        }
    }

    override fun onProductClick(item: Product) {
        _selectedProduct.setValue(item)
    }

    override fun onAddClick(item: Product) {
        cartProductRepository.insert(item.id, QUANTITY_TO_ADD) { cartProductId ->
            cartProducts.add(CartProduct(cartProductId, item, QUANTITY_TO_ADD))
            selectedCartIds.add(cartProductId)
            updateQuantity(item, QUANTITY_TO_ADD)
        }
    }

    override fun onQuantityIncreaseClick(item: Product) {
        val cartProduct = cartProducts.first { it.product.id == item.id }
        cartProductRepository.updateQuantity(cartProduct, QUANTITY_TO_ADD) {
            cartProducts.remove(cartProduct)
            cartProducts.add(cartProduct.copy(quantity = cartProduct.quantity + QUANTITY_TO_ADD))
            updateQuantity(item, QUANTITY_TO_ADD)
        }
    }

    override fun onQuantityDecreaseClick(item: Product) {
        val cartProduct = cartProducts.first { it.product.id == item.id }
        cartProductRepository.updateQuantity(cartProduct, -QUANTITY_TO_ADD) {
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
        _totalCount.postValue((totalCount.value ?: DEFAULT_COUNT) + quantityToAdd)
        _totalPrice.value = totalPrice.value?.plus(item.price * quantityToAdd)
    }

    fun finishOrder() {
        selectedCartIds.forEach { id ->
            cartProductRepository.delete(id) {
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

package woowacourse.shopping.view.cart.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.recommendation.adapter.ProductItem
import woowacourse.shopping.view.cart.selection.adapter.CartProductItem

class CartProductRecommendationViewModel(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    CartProductRecommendationEventHandler {
    private val cartProducts: MutableSet<CartProductItem> = mutableSetOf()

    private val _recommendedProducts = MutableLiveData<List<ProductItem>>()
    val recommendedProducts: LiveData<List<ProductItem>> get() = _recommendedProducts

    private val selectedProductIds: MutableSet<Int> = mutableSetOf()

    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalCount = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    init {
        cartProductRepository.getPagedProducts { result ->
            cartProducts.addAll(result.items.map { CartProductItem(it, false) })
            loadRecommendedProducts()
        }
    }

    fun initShoppingCartInfo(
        selectedIds: Set<Int>,
        totalPrice: Int,
        totalCount: Int,
    ) {
        selectedProductIds.addAll(selectedIds)
        _totalPrice.value = totalPrice
        _totalCount.value = totalCount
    }

    private fun loadRecommendedProducts() {
        recentProductRepository.getLastViewedProduct { recentProduct ->
            recentProduct ?: return@getLastViewedProduct
            productRepository.getPagedProducts { products ->
                val cartProductIds =
                    cartProducts.map { it.cartProduct.product.id }.toSet()
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

    companion object {
        private const val RECOMMEND_SIZE = 10
    }

    override fun onProductClick(item: Product) {
    }

    override fun onAddClick(item: Product) {
    }

    override fun onQuantityIncreaseClick(item: Product) {
    }

    override fun onQuantityDecreaseClick(item: Product) {
    }
}

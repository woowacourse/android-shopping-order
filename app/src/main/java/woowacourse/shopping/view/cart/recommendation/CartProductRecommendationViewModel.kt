package woowacourse.shopping.view.cart.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.selection.adapter.CartProductItem

class CartProductRecommendationViewModel(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) {
    private val _products = MutableLiveData<List<CartProductItem>>()
    val products: LiveData<List<CartProductItem>> get() = _products

    private val _recommendedProducts = MutableLiveData<List<Product>>()
    val recommendedProducts: LiveData<List<Product>> get() = _recommendedProducts

    fun loadRecommendedProducts() {
        recentProductRepository.getLastViewedProduct { recentProduct ->
            recentProduct ?: return@getLastViewedProduct
            productRepository.getPagedProducts { products ->
                val cartProductIds =
                    _products.value?.map { it.cartProduct.product.id }?.toSet() ?: emptySet()
                val recommended =
                    products.items
                        .asSequence()
                        .filter { it.category == recentProduct.product.category }
                        .filter { it.id !in cartProductIds }
                        .take(RECOMMEND_SIZE)
                        .toList()

                _recommendedProducts.postValue(recommended)
            }
        }
    }

    companion object {
        private const val RECOMMEND_SIZE = 10
    }
}

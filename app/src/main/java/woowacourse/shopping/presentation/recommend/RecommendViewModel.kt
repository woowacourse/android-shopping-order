package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.product.ItemClickListener
import woowacourse.shopping.presentation.toPresentation

class RecommendViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    ItemClickListener,
    CartCounterClickListener {
    private lateinit var recentCategory: String

    private val _recommendProducts: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val recommendProducts: LiveData<List<CartItemUiModel>> = _recommendProducts
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        fetchData()
    }

    fun fetchData() {
        recentProductRepository.getMostRecentProduct { result ->
            result.onSuccess { recentProduct ->
                recentCategory = recentProduct?.category ?: ""

                productRepository.fetchPagingProducts(category = recentCategory) { result ->
                    result.onSuccess { products ->
                        val recommendProductsUiModel =
                            products
                                .asSequence()
                                .filter { it.quantity == 0 }
                                .map { it.toPresentation() }
                                .take(10)
                                .toList()
                        _recommendProducts.postValue(recommendProductsUiModel)
                    }
                }
            }
        }
    }

    override fun onClickProductItem(productId: Long) {
    }

    override fun onClickAddToCart(cartItem: CartItem) {
    }

    override fun onClickMinus(id: Long) {
    }

    override fun onClickPlus(id: Long) {
    }
}

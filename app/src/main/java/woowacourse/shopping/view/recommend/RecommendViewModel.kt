package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class RecommendViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
) : ViewModel() {
    private val _event: MutableSingleLiveData<RecommendEvent> = MutableSingleLiveData()
    val event: SingleLiveData<RecommendEvent> get() = _event

    private val _recommendedProducts: MutableLiveData<List<RecommendProduct>> = MutableLiveData()
    val recommendedProducts: LiveData<List<RecommendProduct>> get() = _recommendedProducts

    init {
        loadRecommendedProducts()
    }

    private fun loadRecommendedProducts() {
        productsRepository.loadRecommendedProducts(RECOMMEND_COUNT) { result ->
            result
                .onSuccess { recommendedProducts: List<Product> ->
                    _recommendedProducts.postValue(
                        recommendedProducts.map { product -> RecommendProduct(product) },
                    )
                }.onFailure {
                    _event.postValue(RecommendEvent.LOAD_RECOMMENDED_PRODUCTS_FAILURE)
                }
        }
    }

    companion object {
        private const val RECOMMEND_COUNT = 10
    }
}

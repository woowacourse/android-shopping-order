package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductsRecommendAlgorithm
import woowacourse.shopping.domain.product.RecentViewedCategoryBasedAlgorithm

class RecommendViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
) : ViewModel() {
    private var recentProducts: List<Product> = emptyList()

    private var categoryProducts: List<Product> = emptyList()

    private val _recommendProducts: MutableLiveData<List<RecommendProduct>> = MutableLiveData()
    val recommendProducts: LiveData<List<RecommendProduct>> = _recommendProducts

    private val _event: MutableLiveData<RecommendEvent> = MutableLiveData()
    val event: LiveData<RecommendEvent> = _event

    init {
        loadRecentProducts()
    }

    private fun loadProductsByCategory() {
        productsRepository.loadProductsByCategory(
            recentProducts.first().category,
        ) { result ->
            result
                .onSuccess { categoryProducts ->
                    this.categoryProducts = categoryProducts
                    _recommendProducts.postValue(
                        turnToRecommendProducts(
                            RecentViewedCategoryBasedAlgorithm(),
                        ),
                    )
                }.onFailure {
                    _event.postValue(RecommendEvent.LOAD_ALL_PRODUCTS_FAILURE)
                }
        }
    }

    private fun loadRecentProducts() {
        productsRepository.loadRecentViewedProducts { result ->
            result
                .onSuccess { products: List<Product> ->
                    recentProducts = products
                    loadProductsByCategory()
                }.onFailure {
                    _event.postValue(RecommendEvent.LOAD_RECOMMEND_PRODUCTS_FAILURE)
                }
        }
    }

    private fun turnToRecommendProducts(productsRecommendAlgorithm: ProductsRecommendAlgorithm): List<RecommendProduct> {
        val products: List<Product> =
            productsRecommendAlgorithm.recommendedProducts(
                products = categoryProducts,
                prohibitedProducts = recentProducts,
            )

        return products.map {
            RecommendProduct(
                0,
                it,
            )
        }
    }
}

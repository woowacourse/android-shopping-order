package woowacourse.shopping.feature.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product
import woowacourse.shopping.feature.common.state.ProductUiModel

class RecommendViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendUiState())
    val uiState: StateFlow<RecommendUiState> = _uiState.asStateFlow()

    private var products: List<Product> = emptyList()

    fun initialLoading(contentIds: List<Long>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadRecommendList(10, contentIds)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun loadRecommendList(
        pageSize: Int,
        contentIds: List<Long>,
    ) {
        viewModelScope.launch {
            val category = refreshRecentProducts()
            val serverCart = cartRepository.loadCart()

            products =
                productRepository
                    .loadProducts(
                        startIndex = products.size,
                        pageSize = pageSize,
                        sort = emptyList(),
                        category = category,
                    ).first

            val duplicationProducts =
                products.filter { !serverCart.getProductList().map { it.id }.contains(it.id) }
            val checkCart = serverCart.cartContents.filter { it.id in contentIds }

            _uiState.update {
                it.copy(
                    recommendList =
                        duplicationProducts.map { product ->
                            product.toProductUiModel()
                        },
                    totalPrice = checkCart.sumOf { it.quantity * it.product.priceAmount() },
                    totalCount = checkCart.size,
                )
            }
        }
    }

    fun increase(productId: Long) {
        val product =
            products.firstOrNull { it.id == productId }!!

        _uiState.update {
            it.copy(
                recommendList =
                    it.recommendList.map { product ->
                        if (product.id == productId) {
                            product.copy(quantity = product.quantity + 1)
                        } else {
                            product.copy(quantity = product.quantity)
                        }
                    },
                totalPrice = it.totalPrice + product.priceAmount(),
                totalCount = it.totalCount + if (it.recommendList.firstOrNull { it.id == productId }?.quantity == 0) 1 else 0,
            )
        }
    }

    fun decrease(productId: Long) {
        val product =
            products.firstOrNull { it.id == productId }!!

        _uiState.update {
            it.copy(
                recommendList =
                    it.recommendList.map { product ->
                        if (product.id == productId) {
                            product.copy(quantity = product.quantity - 1)
                        } else {
                            product.copy(quantity = product.quantity)
                        }
                    },
                totalPrice = it.totalPrice - product.priceAmount(),
                totalCount = it.totalCount - if (it.recommendList.firstOrNull { it.id == productId }?.quantity == 1) 1 else 0,
            )
        }
    }

    fun Product.toProductUiModel(quantity: Int = 0): ProductUiModel =
        ProductUiModel(
            name = this.name,
            price = this.priceAmount(),
            imageUrl = this.imageUrl,
            id = this.id,
            quantity = quantity,
        )

    suspend fun addRecommendToCart(contentIds: List<Long>): List<Long> {
        _uiState.value.recommendList.filter { it.quantity > 0 }.forEach {
            cartRepository.increase(
                product =
                    Product(
                        id = it.id,
                        name = it.name,
                        price = Money(it.price),
                        imageUrl = it.imageUrl,
                    ),
                quantity = it.quantity,
            )
        }
        val newContents = _uiState.value.recommendList.map { it.id }
        val serverCart = cartRepository.loadCart()
        return serverCart.cartContents
            .filter { cartContent ->
                cartContent.id in contentIds || cartContent.productId in newContents
            }.map { it.id }
    }

    private suspend fun refreshRecentProducts(): String {
        val recentProductIds = recentProductRepository.loadProducts()
        val mostRecentProductId = recentProductIds.firstOrNull() ?: return ""

        return productRepository.getProduct(mostRecentProductId).category
    }

    companion object {
        fun recommendFactory() =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    val appDependencies = runBlocking { app.appDependenciesDeferred.await() }
                    RecommendViewModel(
                        appDependencies.productRepository,
                        appDependencies.cartRepository,
                        appDependencies.recentProductRepository,
                    )
                }
            }
    }
}

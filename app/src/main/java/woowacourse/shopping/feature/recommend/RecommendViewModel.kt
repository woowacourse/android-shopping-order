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
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductNotFoundException
import woowacourse.shopping.feature.common.state.ProductUiModel

data class RecommendUiState(
    val recommendList: List<ProductUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val totalPrice: Int = 0,
)

class RecommendViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendUiState())
    val uiState: StateFlow<RecommendUiState> = _uiState.asStateFlow()

    private var products: List<Product> = emptyList()
    private var serverCart: Cart = Cart(emptyList())
    private var memoryCart: Cart = Cart(emptyList())
    private var initialTotalPrice = 0

    fun initialLoading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadRecommendList(10)
            refreshCart()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun refreshCart() {
        serverCart = cartRepository.loadCart()
    }

    fun loadRecommendList(pageSize: Int) {
        viewModelScope.launch {
            val category = refreshRecentProducts()

            products = productRepository.loadProducts(
                startIndex = products.size,
                pageSize = pageSize,
                sort = emptyList(),
                category = category,
            )

            _uiState.update {
                it.copy(
                    recommendList = products.map(::toProductUiModel),
                )
            }
        }
    }

    fun increase(productId: String) {
        val product = products.firstOrNull { it.id == productId }
            ?: throw ProductNotFoundException(productId)

        memoryCart = memoryCart.plusCartContent(
            CartContent(
                product = product,
                quantity = 1,
                id = productId,
            ),
        )
        _uiState.update {
            it.copy(
                recommendList = products.map(::toProductUiModel),
            )
        }

        priceAlter()
    }

    fun decrease(productId: String) {
        val product = products.firstOrNull { it.id == productId }
            ?: throw ProductNotFoundException(productId)

        memoryCart = memoryCart.minusCartContent(
            CartContent(
                product = product,
                quantity = 1,
                id = productId,
            ),
        )
        _uiState.update {
            it.copy(
                recommendList = products.map(::toProductUiModel),
            )
        }

        priceAlter()
    }

    fun priceAlter() {
        val totalPrice = products.sumOf { it.priceAmount() * memoryCart.quantityOf(it.id) }

        _uiState.update {
            it.copy(
                totalPrice = it.totalPrice + totalPrice,
            )
        }
    }

    fun toProductUiModel(product: Product): ProductUiModel {
        return ProductUiModel(
            name = product.name,
            price = product.priceAmount(),
            imageUrl = product.imageUrl,
            id = product.id,
            quantity = memoryCart.quantityOf(product.id),
        )
    }

    fun getTotalPrice(contentIds: List<String>): Int {

        val checkedCartItems = serverCart.cartContents.filter { contentIds.contains(it.id) }

        val checkPrice = checkedCartItems.sumOf { it.product.priceAmount() * it.quantity }

        val memoryPrice = memoryCart.cartContents.sumOf { it.product.priceAmount() * it.quantity }

        return checkPrice + memoryPrice
    }

    fun getTotalCount(contentIds: List<String>): Int {
        val memoryTotalCount = memoryCart.cartContents.size

        return contentIds.size + memoryTotalCount
    }

    fun order(cartContentIds: List<String>) {
        viewModelScope.launch {
            val products = memoryCart.cartContents.map { it.product }
            memoryCart.cartContents.forEach {
                cartRepository.insert(it.product.id, quantity = it.quantity)
            }

            refreshCart()

            val latestContents = serverCart.cartContents.filter { cartContent ->
                products.map { it.id }.contains(cartContent.product.id)
            }

            orderRepository.orders(
                cartItemIds = cartContentIds + latestContents.map { it.id },
            )
        }
    }

    private suspend fun refreshRecentProducts(): String {
        val recentProductIds = recentProductRepository.loadProducts()
        val mostRecentProductId = recentProductIds.first()

        return productRepository.getProduct(mostRecentProductId).category
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ShoppingApplication
                RecommendViewModel(
                    app.productRepository,
                    app.cartRepository,
                    app.orderRepository,
                    app.recentProductRepository,
                )
            }
        }
    }
}

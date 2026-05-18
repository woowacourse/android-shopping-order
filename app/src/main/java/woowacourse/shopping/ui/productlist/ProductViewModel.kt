package woowacourse.shopping.ui.productlist

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import woowacourse.shopping.data.remote.retrofit.awaitBody
import woowacourse.shopping.data.remote.retrofit.awaitCompletion
import woowacourse.shopping.data.mapper.toApiProduct
import woowacourse.shopping.data.mapper.toDomainProduct
import woowacourse.shopping.data.mapper.toDomainProducts
import woowacourse.shopping.data.mapper.toShoppingItem
import woowacourse.shopping.data.remote.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.productlist.ProductListState

class ProductViewModel(
    private val productRetrofitRepository: ProductRetrofitRepository,
) : ViewModel() {
    private val productRequestMutex = Mutex()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    private val _productDetails = MutableStateFlow<Map<Long, Product>>(emptyMap())
    val productDetails: StateFlow<Map<Long, Product>> = _productDetails.asStateFlow()

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()
    private var hasLoadedProductsOnce: Boolean = false
    private var lastProductsLoadedElapsedMs: Long = 0L

    fun requestProduct(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = DEFAULT_SORT,
        category: String? = null,
        force: Boolean = false,
    ) {
        if (shouldSkipProductRequest(force = force)) return

        viewModelScope.launch {
            productRequestMutex.withLock {
                if (shouldSkipProductRequest(force = force)) return@withLock

                _state.value =
                    _state.value.copy(
                        isLoading = true,
                        errorMessage = null,
                    )

                runCatching {
                    productRetrofitRepository
                        .requestProduct(
                            page = page,
                            size = size,
                            sort = sort,
                            category = category,
                        ).awaitBody(errorPrefix = "상품 조회 실패")
                }.onSuccess { response ->
                    val loadedProducts = response.toDomainProducts()
                    _products.value = loadedProducts

                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            products = loadedProducts.map { it.toShoppingItem() },
                            errorMessage = null,
                        )
                    _productDetails.update { cachedProducts ->
                        cachedProducts + loadedProducts.associateBy { product -> product.id }
                    }
                    markProductsLoaded()
                }.onFailure {
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            errorMessage = "상품 목록을 불러오지 못했습니다.",
                        )
                }
            }
        }
    }

    fun requestProductDetail(id: Long) {
        if (_productDetails.value.containsKey(id)) return
        viewModelScope.launch {
            runCatching {
                productRetrofitRepository
                    .requestProductDetail(
                        id = id,
                    ).awaitBody(errorPrefix = "상품 조회 실패")
            }.onSuccess { response ->
                val detailProduct = response.toDomainProduct()
                _productDetails.update { cachedProducts ->
                    cachedProducts + (detailProduct.id to detailProduct)
                }
            }
        }
    }

    private fun shouldSkipProductRequest(force: Boolean): Boolean {
        if (force) return false
        if (!hasLoadedProductsOnce) return false
        return isProductsCacheFresh()
    }

    private fun isProductsCacheFresh(): Boolean =
        SystemClock.elapsedRealtime() - lastProductsLoadedElapsedMs < PRODUCTS_CACHE_DURATION_MS

    private fun markProductsLoaded() {
        hasLoadedProductsOnce = true
        lastProductsLoadedElapsedMs = SystemClock.elapsedRealtime()
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            runCatching {
                productRetrofitRepository
                    .addProduct(
                        product = product.toApiProduct(),
                    ).awaitCompletion(errorPrefix = "상품 추가 실패")
            }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            runCatching {
                productRetrofitRepository
                    .deleteProduct(
                        id = id,
                    ).awaitCompletion(errorPrefix = "상품 삭제 실패")
            }
        }
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private val DEFAULT_SORT = listOf("id,asc")
        private const val PRODUCTS_CACHE_DURATION_MS = 30_000L
    }
}

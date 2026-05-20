package woowacourse.shopping.backend.retrofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.backend.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.mapper.toApiProduct
import woowacourse.shopping.mapper.toDomainProduct
import woowacourse.shopping.mapper.toDomainProducts
import woowacourse.shopping.mapper.toShoppingItem
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.state.ProductListUiState

class ProductViewModel(
    private val productRetrofitRepository: ProductRetrofitRepository,
) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    private val _productDetails = MutableStateFlow<Map<Long, Product>>(emptyMap())
    val productDetails: StateFlow<Map<Long, Product>> = _productDetails.asStateFlow()

    private val _state = MutableStateFlow(ProductListUiState())
    val state: StateFlow<ProductListUiState> = _state.asStateFlow()

    fun requestProduct(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = DEFAULT_SORT,
        category: String? = null,
    ) {
        _state.value =
            _state.value.copy(
                isLoading = true,
                errorMessage = null,
            )

        viewModelScope.launch {
            runCatching {
                productRetrofitRepository
                    .requestProduct(
                        page = page,
                        size = size,
                        sort = sort,
                        category = category,
                    )
            }.onSuccess { response ->
                val loadedProducts = response.toDomainProducts()
                _products.value = loadedProducts

                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        shoppingItems = loadedProducts.map { it.toShoppingItem() },
                        errorMessage = null,
                    )
                _productDetails.update { cachedProducts ->
                    cachedProducts + loadedProducts.associateBy { product -> product.id }
                }
            }.onFailure {
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        errorMessage = "상품 목록을 불러오지 못했습니다.",
                    )
            }
        }
    }

    fun requestProductDetail(id: Long) {
        viewModelScope.launch {
            runCatching {
                productRetrofitRepository
                    .requestProductDetail(
                        id = id,
                    )
            }.onSuccess { response ->
                val detailProduct = response.toDomainProduct()
                _productDetails.update { cachedProducts ->
                    cachedProducts + (detailProduct.id to detailProduct)
                }
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            runCatching {
                productRetrofitRepository
                    .addProduct(
                        product = product.toApiProduct(),
                    )
            }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            runCatching {
                productRetrofitRepository
                    .deleteProduct(
                        id = id,
                    )
            }
        }
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private val DEFAULT_SORT = listOf("id,asc")
    }
}

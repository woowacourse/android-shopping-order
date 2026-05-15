package woowacourse.shopping.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.retrofit.awaitBody
import woowacourse.shopping.data.remote.retrofit.awaitCompletion
import woowacourse.shopping.data.remote.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.data.mapper.toApiProduct
import woowacourse.shopping.data.mapper.toDomainProduct
import woowacourse.shopping.data.mapper.toDomainProducts
import woowacourse.shopping.data.mapper.toShoppingItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.productlist.ProductListState

/**
 * 로미가 작성한 패키지의 구조는 어떤 생각을 기반으로 작성이 된 것인지 공유해주실 수 있을까요?
 * 로미가 생각하기에 Repository와 ViewModel은 어떤 역할을 하는 친구들인가요?
 * 또한, 현재 패키지에 있는 ViewModel들과 ui 패키지 아래있는 ViewModel들은 어떤 차이가 있는 것일까요?
 */
class ProductViewModel(
    private val productRetrofitRepository: ProductRetrofitRepository,
) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    private val _productDetails = MutableStateFlow<Map<Long, Product>>(emptyMap())
    val productDetails: StateFlow<Map<Long, Product>> = _productDetails.asStateFlow()

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()

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
                    ).awaitBody(errorPrefix = "상품 조회 실패")
            }.onSuccess { response ->
                /**
                 * 이러한 코드는 프로덕션에 추가되어도 괜찮을까요?
                 */
                delay(3000) // 스켈레톤 ui 확인을 위한 딜레이
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
                    ).awaitBody(errorPrefix = "상품 조회 실패")
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
    }
}

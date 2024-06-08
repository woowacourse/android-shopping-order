package woowacourse.shopping.presentation.products

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.datasource.DataResponse.Companion.NULL_BODY_ERROR_CODE
import com.example.domain.datasource.map
import com.example.domain.datasource.onFailure
import com.example.domain.datasource.onSuccess
import com.example.domain.datasource.zip
import com.example.domain.model.Quantity
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.products.uimodel.ProductUiModel
import woowacourse.shopping.presentation.products.uimodel.RecentProductUiModel
import woowacourse.shopping.presentation.products.uimodel.toRecentProdutUiModels
import kotlin.concurrent.thread

class ProductsViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), ProductsActionHandler {
    private val _productsUiState = MutableLiveData(ProductsUiState())
    val productsUiState: LiveData<ProductsUiState> = _productsUiState

    private val _showLoadMore = MutableLiveData<Boolean>(false)
    val showLoadMore: LiveData<Boolean> get() = _showLoadMore

    private val _cartTotalCount: MutableLiveData<Int> = MutableLiveData()
    val cartTotalCount: LiveData<Int> get() = _cartTotalCount

    private val _recentProductUiModels = MutableLiveData<List<RecentProductUiModel>?>()
    val recentProductUiModels: LiveData<List<RecentProductUiModel>?> get() = _recentProductUiModels

    private val handler = Handler(Looper.getMainLooper())

    private var page: Int = INIT_PAGE

    init {
        loadPage()
    }

    fun loadPage() {
        thread {
            val pagingProduct = productRepository.findPage(page, PAGE_SIZE)
            val cartItems = cartRepository.findAll()
            val result =
                zip(pagingProduct, cartItems) { pagingProduct, cartItems ->
                    val list =
                        pagingProduct.products.map { product ->
                            val cartItem = cartItems.find { it.product == product }
                            val quantity = cartItem?.quantity ?: Quantity(0)
                            ProductUiModel(product, quantity)
                        }
                    list to pagingProduct.isLast
                }
            handler.post {
                result.onSuccess { (newProductUiModels, isLast) ->
                    val oldProductsUiModel = productsUiState.value?.productUiModels ?: listOf()
                    val productUiModels = oldProductsUiModel + newProductUiModels
                    val newProductsUiState =
                        productsUiState.value?.copy(
                            productUiModels = productUiModels,
                            isLast = isLast,
                            isLoading = false,
                            isError = false,
                        )
                    _productsUiState.postValue(newProductsUiState)
                }.onFailure { _, _ ->
                    val newProductsUiState =
                        productsUiState.value?.copy(isLoading = false, isError = true)
                    _productsUiState.postValue(newProductsUiState)
                }
                page++
            }
        }
    }

    fun loadRecentProducts() {
        thread {
            val result =
                recentProductRepository.findRecentProducts()
            handler.post {
                _recentProductUiModels.postValue(result.toRecentProdutUiModels())
            }
        }
    }

    private fun increaseQuantity(productId: Int) {
        thread {
            cartRepository.increaseQuantity(productId)
            updateQuantity(productId)
            updateTotalCount()
        }
    }

    private fun decreaseQuantity(productId: Int) {
        thread {
            cartRepository.decreaseQuantity(productId)
            updateQuantity(productId)
            updateTotalCount()
        }
    }

    private fun updateQuantity(productId: Int) {
        thread {
            val result = cartRepository.find(productId).map { it.quantity }
            handler.post {
                val oldUiModels =
                    productsUiState.value?.productUiModels?.toMutableList() ?: mutableListOf()
                val idx = oldUiModels.indexOfFirst { it.product.id == productId }
                result.onSuccess { quantity ->
                    oldUiModels[idx] = oldUiModels[idx].copy(quantity = quantity)
                    val newUiState = productsUiState.value?.copy(productUiModels = oldUiModels)
                    _productsUiState.postValue(newUiState)
                }.onFailure { code, error ->
                    if (code == NULL_BODY_ERROR_CODE) {
                        oldUiModels[idx] = oldUiModels[idx].copy(quantity = Quantity(0))
                        val newUiState = productsUiState.value?.copy(productUiModels = oldUiModels)
                        _productsUiState.postValue(newUiState)
                    }
                }
            }
        }
    }

    private fun updateTotalCount() {
        thread {
            val result = cartRepository.totalCartItemCount()
            handler.post {
                result.onSuccess { totalCount ->
                    _cartTotalCount.postValue(totalCount)
                }
            }
        }
    }

    override fun onClickProduct(productId: Int) {
        TODO("Not yet implemented")
    }

    override fun onClickLoadMoreButton() {
        loadPage()
    }

    override fun onClickPlusQuantityButton(productId: Int) {
        increaseQuantity(productId)
    }

    override fun onClickMinusQuantityButton(productId: Int) {
        decreaseQuantity(productId)
    }

    override fun onClickShoppingCart() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val INIT_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}

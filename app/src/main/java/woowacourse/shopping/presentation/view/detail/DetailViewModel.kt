package woowacourse.shopping.presentation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.common.model.ProductUiModel
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.common.util.MutableSingleLiveData
import woowacourse.shopping.presentation.common.util.SingleLiveData
import woowacourse.shopping.presentation.view.detail.event.DetailMessageEvent

class DetailViewModel(
    productId: Long,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val _toastEvent = MutableSingleLiveData<DetailMessageEvent>()
    val toastEvent: SingleLiveData<DetailMessageEvent> = _toastEvent

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _quantity = MutableLiveData(DEFAULT_QUANTITY)
    val quantity: LiveData<Int> = _quantity

    private val _recentProduct = MutableLiveData<ProductUiModel>()
    val recentProduct: LiveData<ProductUiModel> = _recentProduct

    private val _addToCartSuccessEvent = MutableSingleLiveData<Unit>()
    val addToCartSuccessEvent: SingleLiveData<Unit> = _addToCartSuccessEvent

    val isRecentProductVisible: LiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            addSource(_product) { updateVisibility() }
            addSource(_recentProduct) { updateVisibility() }
        }

    init {
        loadProduct(productId)
        loadRecentProduct()
    }

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            productRepository
                .fetchProduct(productId)
                .onSuccess {
                    _product.postValue(it.toUiModel())
                    updateRecentProduct(productId, it.category)
                }.onFailure { _toastEvent.postValue(DetailMessageEvent.FETCH_PRODUCT_FAILURE) }
        }
    }

    fun increaseQuantity() {
        val currentQuantity = _quantity.value ?: DEFAULT_QUANTITY
        _quantity.value = currentQuantity + QUANTITY_STEP
    }

    fun decreaseQuantity() {
        val currentQuantity = _quantity.value ?: DEFAULT_QUANTITY
        _quantity.value = (currentQuantity - QUANTITY_STEP).coerceAtLeast(DEFAULT_QUANTITY)
    }

    fun addProductToCart() {
        val product = _product.value ?: return
        val quantity = _quantity.value ?: DEFAULT_QUANTITY

        viewModelScope.launch {
            cartRepository
                .increaseQuantity(product.id, quantity)
                .onSuccess { _addToCartSuccessEvent.postValue(Unit) }
                .onFailure { _toastEvent.postValue(DetailMessageEvent.ADD_PRODUCT_FAILURE) }
        }
    }

    private fun loadRecentProduct() {
        viewModelScope.launch {
            recentProductRepository
                .getRecentProducts(1)
                .onSuccess { _recentProduct.postValue(it.firstOrNull()?.toUiModel()) }
                .onFailure { _toastEvent.postValue(DetailMessageEvent.FETCH_PRODUCT_FAILURE) }
        }
    }

    private fun updateRecentProduct(
        productId: Long,
        category: String,
    ) {
        viewModelScope.launch {
            recentProductRepository
                .insertAndTrimToLimit(productId, category, RECENT_PRODUCT_LIMIT)
                .onFailure { _toastEvent.postValue(DetailMessageEvent.FETCH_PRODUCT_FAILURE) }
        }
    }

    private fun MediatorLiveData<Boolean>.updateVisibility() {
        val currentProduct = _product.value
        val recent = _recentProduct.value
        value = recent != null && recent.id != currentProduct?.id
    }

    companion object {
        private const val DEFAULT_QUANTITY = 1
        private const val QUANTITY_STEP = 1
        private const val RECENT_PRODUCT_LIMIT = 10

        fun Factory(productId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T =
                    DetailViewModel(
                        productId,
                        RepositoryProvider.productRepository,
                        RepositoryProvider.cartRepository,
                        RepositoryProvider.recentProductRepository,
                    ) as T
            }
    }
}

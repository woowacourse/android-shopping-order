package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseProductQuantityUseCase
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.product.ItemClickListener
import woowacourse.shopping.presentation.toDomain
import woowacourse.shopping.presentation.toPresentation

class RecommendViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val increaseProductQuantityUseCase: IncreaseProductQuantityUseCase,
    private val decreaseProductQuantityUseCase: DecreaseProductQuantityUseCase,
    private val addToCartUseCase: AddToCartUseCase,
) : ViewModel(),
    ItemClickListener,
    CartCounterClickListener {
    private lateinit var recentCategory: String
    private val _recommendProducts: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val recommendProducts: LiveData<List<CartItemUiModel>> = _recommendProducts
    private val _selectedTotalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalPrice: LiveData<Int> = _selectedTotalPrice
    private val _selectedTotalCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalCount: LiveData<Int> = _selectedTotalCount
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage
    private val _navigateTo = SingleLiveData<Long>()
    val navigateTo: LiveData<Long> = _navigateTo

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            val recentProduct = recentProductRepository.getMostRecentProduct()
            recentProduct.onSuccess {
                recentCategory = it?.category ?: ""
                productRepository.fetchPagingProducts(category = recentCategory) { result ->
                    result
                        .onSuccess { products ->
                            val recommendProductsUiModel =
                                products
                                    .asSequence()
                                    .filter { it.quantity == 0 }
                                    .map { it.toPresentation() }
                                    .take(10)
                                    .toList()
                            _recommendProducts.postValue(recommendProductsUiModel)
                            if (recommendProductsUiModel.isEmpty()) {
                                _toastMessage.postValue(
                                    R.string.recommend_toast_load_not_enough_products,
                                )
                            }
                        }.onFailure {
                            _toastMessage.postValue(R.string.recommend_toast_load_fail)
                        }
                }
            }
        }
    }

    fun fetchSelectedInfo(
        price: Int,
        count: Int,
    ) {
        _selectedTotalPrice.value = price
        _selectedTotalCount.value = count
    }

    override fun onClickProductItem(productId: Long) {
        _navigateTo.value = productId
    }

    override fun onClickAddToCart(cartItemUiModel: CartItemUiModel) {
        addToCartUseCase(
            product = cartItemUiModel.product.toDomain(),
            quantity = 1,
            onSuccess = { updateQuantity(productId = cartItemUiModel.product.id, 1) },
            onFailure = { _toastMessage.value = R.string.product_toast_add_cart_fail },
        )
    }

    override fun onClickMinus(id: Long) {
        decreaseProductQuantityUseCase(
            id,
            onSuccess = { updateQuantity(id, -1) },
            onFailure = { _toastMessage.value = R.string.product_toast_decrease_fail },
        )
    }

    override fun onClickPlus(id: Long) {
        increaseProductQuantityUseCase(
            id,
            onSuccess = { updateQuantity(id, 1) },
            onFailure = { _toastMessage.value = R.string.product_toast_increase_fail },
        )
    }

    private fun updateQuantity(
        productId: Long,
        delta: Int,
    ) {
        val currentItems = _recommendProducts.value ?: return
        val oldPrice = _selectedTotalPrice.value ?: 0
        val oldCount = _selectedTotalCount.value ?: 0
        val updatedItem =
            currentItems.map {
                if (it.product.id == productId) {
                    fetchSelectedInfo(oldPrice + (it.product.price * delta), oldCount + delta)
                    it.copy(isSelected = true, quantity = it.quantity + delta)
                } else {
                    it
                }
            }
        _recommendProducts.postValue(updatedItem)
    }
}

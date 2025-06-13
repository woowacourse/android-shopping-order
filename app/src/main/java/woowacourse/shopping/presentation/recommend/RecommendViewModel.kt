package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseProductQuantityUseCase
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.product.ItemClickListener
import woowacourse.shopping.presentation.uimodel.CartItemUiModel
import woowacourse.shopping.presentation.uimodel.toDomain
import woowacourse.shopping.presentation.uimodel.toPresentation

class RecommendViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val increaseProductQuantityUseCase: IncreaseProductQuantityUseCase,
    private val decreaseProductQuantityUseCase: DecreaseProductQuantityUseCase,
    private val addToCartUseCase: AddToCartUseCase,
) : ViewModel(),
    ItemClickListener,
    CartCounterClickListener {
    private val _recommendProducts: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val recommendProducts: LiveData<List<CartItemUiModel>> = _recommendProducts
    private val _selectedTotalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalPrice: LiveData<Int> = _selectedTotalPrice
    private val _selectedTotalCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedTotalCount: LiveData<Int> = _selectedTotalCount
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage
    private val _navigateToDetail = SingleLiveData<Long>()
    val navigateToDetail: LiveData<Long> = _navigateToDetail
    private val _navigateToOrder = SingleLiveData<LongArray>()
    val navigateToOrder: LiveData<LongArray> = _navigateToOrder

    private val selectedRecommendedProductIds: MutableSet<Long> = mutableSetOf()
    private var cartIntentProductIds: Set<Long>? = null
    val isUpdated: Boolean
        get() = selectedRecommendedProductIds.isNotEmpty()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            val recentProduct = recentProductRepository.getMostRecentProduct()
            recentProduct.onSuccess {
                val recentCategory = it?.category ?: ""
                productRepository
                    .fetchPagingProducts(category = recentCategory)
                    .onSuccess { products ->
                        val recommendProductsUiModel =
                            products
                                .asSequence()
                                .filter { product -> product.quantity == 0 }
                                .map { product -> product.toPresentation() }
                                .take(10)
                                .toList()
                        _recommendProducts.value = recommendProductsUiModel
                        if (recommendProductsUiModel.isEmpty()) {
                            _toastMessage.value = R.string.recommend_toast_load_not_enough_products
                        }
                    }.onFailure {
                        _toastMessage.value = R.string.recommend_toast_load_fail
                    }
            }
        }
    }

    fun fetchSelectedInfo(productIds: LongArray) {
        runCatching {
            cartIntentProductIds = productIds.toSet()
            val cartItems =
                productIds.map {
                    cartRepository.fetchCartItemById(it) ?: throw NoSuchElementException()
                }

            updateSelectedInfo(
                price = cartItems.sumOf { it.totalPrice },
                count = cartItems.sumOf { it.quantity },
            )
        }.onFailure { _toastMessage.value = R.string.product_toast_load_failure }
    }

    private fun updateSelectedInfo(
        price: Int,
        count: Int,
    ) {
        _selectedTotalPrice.value = price
        _selectedTotalCount.value = count
    }

    fun navigateToOrder() {
        val orderProductIds =
            (selectedRecommendedProductIds + (cartIntentProductIds ?: emptySet())).toLongArray()
        _navigateToOrder.value = orderProductIds
    }

    override fun onClickProductItem(productId: Long) {
        _navigateToDetail.value = productId
    }

    override fun onClickAddToCart(cartItemUiModel: CartItemUiModel) {
        viewModelScope.launch {
            addToCartUseCase(
                product = cartItemUiModel.product.toDomain(),
                quantity = 1,
            ).onSuccess { updateQuantity(productId = cartItemUiModel.product.id, 1) }
                .onFailure { _toastMessage.value = R.string.product_toast_add_cart_fail }

            selectedRecommendedProductIds.add(cartItemUiModel.product.id)
        }
    }

    override fun onClickMinus(id: Long) {
        viewModelScope.launch {
            decreaseProductQuantityUseCase(
                id,
            ).onSuccess { updateQuantity(id, -1) }
                .onFailure { _toastMessage.value = R.string.product_toast_decrease_fail }
        }
    }

    override fun onClickPlus(id: Long) {
        viewModelScope.launch {
            increaseProductQuantityUseCase(
                id,
            ).onSuccess { updateQuantity(id, 1) }
                .onFailure { _toastMessage.value = R.string.product_toast_increase_fail }
        }
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
                    if (it.quantity + delta == 0) selectedRecommendedProductIds.remove(it.product.id)

                    updateSelectedInfo(oldPrice + (it.product.price * delta), oldCount + delta)
                    it.copy(isSelected = true, quantity = it.quantity + delta)
                } else {
                    it
                }
            }
        _recommendProducts.value = updatedItem
    }
}

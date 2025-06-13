package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseProductQuantityUseCase
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.uimodel.CartItemUiModel
import woowacourse.shopping.presentation.uimodel.toPresentation

class CartViewModel(
    private val cartRepository: CartRepository,
    private val increaseProductQuantityUseCase: IncreaseProductQuantityUseCase,
    private val decreaseProductQuantityUseCase: DecreaseProductQuantityUseCase,
) : ViewModel(),
    CartPageClickListener,
    CartCounterClickListener {
    private val _uiState: MutableLiveData<ResultState<Unit>> = MutableLiveData()
    val uiState: LiveData<ResultState<Unit>> = _uiState
    private val _cartItems: MutableLiveData<List<CartItemUiModel>> = MutableLiveData()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems
    val selectedTotalPrice: LiveData<Int> =
        cartItems.map { cartItems -> cartItems.filter { it.isSelected }.sumOf { it.totalPrice } }
    val selectedTotalCount =
        cartItems.map { cartItems -> cartItems.filter { it.isSelected }.sumOf { it.quantity } }
    val isCheckAll: LiveData<Boolean> =
        cartItems.map { cartItems -> cartItems.all { it.isSelected } }
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage
    private val _navigateTo = SingleLiveData<LongArray>()
    val navigateTo: LiveData<LongArray> = _navigateTo

    private var initCartItemStatus: List<CartItemUiModel>? = null
    val isUpdated: Boolean
        get() = hasChanges()

    init {
        loadItems()
    }

    fun loadItems(currentPage: Int = 0) {
        _uiState.value = ResultState.Loading

        viewModelScope.launch {
            val fetchedCartItems = cartRepository.fetchPagedCartItems(currentPage)
            fetchedCartItems
                .onSuccess {
                    val oldItemsMapper =
                        cartItems.value
                            .orEmpty()
                            .associateBy { cartItemUiModel -> cartItemUiModel.id }
                    val newItems =
                        it.map { newItem ->
                            oldItemsMapper[newItem.cartId] ?: newItem.toPresentation()
                        }

                    if (initCartItemStatus == null) initCartItemStatus = newItems
                    _cartItems.value = newItems
                    _uiState.value = ResultState.Success(Unit)
                }.onFailure { _toastMessage.value = R.string.cart_toast_load_fail }
        }
    }

    fun updateItems() {
        viewModelScope.launch {
            val fetchedCartItems = cartRepository.fetchPagedCartItems(0)
            fetchedCartItems
                .onSuccess {
                    val oldItemsMapper =
                        cartItems.value
                            .orEmpty()
                            .associateBy { cartItemUiModel -> cartItemUiModel.id }

                    val newItems =
                        it.map { newItem ->
                            oldItemsMapper[newItem.cartId]
                                ?: newItem.toPresentation(isSelected = true)
                        }

                    _cartItems.value = newItems
                    _uiState.value = ResultState.Success(Unit)
                }.onFailure { _toastMessage.value = R.string.cart_toast_load_fail }
        }
    }

    override fun onClickDelete(cartItem: CartItemUiModel) {
        viewModelScope.launch {
            cartRepository
                .deleteProduct(cartItem.product.id)
                .onSuccess {
                    _toastMessage.value = R.string.cart_toast_delete_success
                    loadItems()
                }.onFailure { _toastMessage.value = R.string.cart_toast_delete_fail }
        }
    }

    override fun onClickSelect(cartId: Long) {
        val cartItems = _cartItems.value ?: return
        val newCartItems =
            cartItems.map { if (it.id == cartId) it.copy(isSelected = !it.isSelected) else it }
        _cartItems.value = newCartItems
    }

    override fun onClickCheckAll() {
        val currentCheckState = isCheckAll.value ?: return
        val toggledState = !currentCheckState
        _cartItems.value = _cartItems.value?.map { it.copy(isSelected = toggledState) }.orEmpty()
    }

    override fun onClickRecommend() {
        if (selectedTotalCount.value == null || selectedTotalCount.value == 0) {
            _toastMessage.value = R.string.cart_toast_invalid_order_quantity
            return
        }

        val selectedProductIds: LongArray =
            cartItems.value
                ?.filter { it.isSelected }
                ?.map { it.product.id }
                ?.toLongArray()
                ?: longArrayOf()
        _navigateTo.value = selectedProductIds
    }

    override fun onClickMinus(id: Long) {
        val currentItems = cartItems.value ?: emptyList()
        val item = currentItems.find { it.product.id == id } ?: return

        if (item.quantity == 1) {
            _toastMessage.value = R.string.cart_toast_invalid_quantity
            return
        }

        viewModelScope.launch {
            decreaseProductQuantityUseCase(id)
                .onSuccess { updateQuantity(id, -1) }
                .onFailure { _toastMessage.value = R.string.cart_toast_decrease_fail }
        }
    }

    override fun onClickPlus(id: Long) {
        viewModelScope.launch {
            increaseProductQuantityUseCase(id)
                .onSuccess { updateQuantity(id, 1) }
                .onFailure { _toastMessage.value = R.string.cart_toast_increase_fail }
        }
    }

    private fun updateQuantity(
        productId: Long,
        amount: Int,
    ) {
        val currentItems = cartItems.value ?: emptyList()
        val updatedItem =
            currentItems.map { cartItem ->
                if (cartItem.product.id == productId) {
                    cartItem.copy(
                        quantity = cartItem.quantity + amount,
                        totalPrice = (cartItem.quantity + amount) * (cartItem.totalPrice / cartItem.quantity),
                    )
                } else {
                    cartItem
                }
            }
        _cartItems.value = updatedItem
    }

    private fun hasChanges(): Boolean = cartItems.value?.containsAll(initCartItemStatus ?: emptyList())?.not() ?: true
}

package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.data.cart.remote.DefaultCartItemRepository
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.ui.ResponseHandler.handleResponseResult
import woowacourse.shopping.ui.cart.listener.OnAllCartItemSelectedListener
import woowacourse.shopping.ui.cart.listener.OnCartItemDeleteListener
import woowacourse.shopping.ui.cart.listener.OnCartItemSelectedListener
import woowacourse.shopping.ui.cart.listener.OnNavigationOrderListener
import woowacourse.shopping.ui.mapper.CartItemMapper.toDomain
import woowacourse.shopping.ui.mapper.CartItemMapper.toUiModel
import woowacourse.shopping.ui.model.CartItemUiModel
import woowacourse.shopping.ui.model.OrderInformation

class ShoppingCartViewModel(
    private val cartItemRepository: CartItemRepository,
) : ViewModel(),
    OnCartItemDeleteListener,
    OnItemQuantityChangeListener,
    OnCartItemSelectedListener,
    OnAllCartItemSelectedListener,
    OnNavigationOrderListener {
    private var _cartItems = MutableLiveData<List<CartItemUiModel>>()
    val cartItems: LiveData<List<CartItemUiModel>> get() = _cartItems

    private var _deletedItemId: MutableSingleLiveData<Long> = MutableSingleLiveData()
    val deletedItemId: SingleLiveData<Long> get() = _deletedItemId

    private var _isAllSelected = MutableLiveData(false)
    val isAllSelected: LiveData<Boolean> get() = _isAllSelected

    private var _selectedCartItemsTotalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val selectedCartItemsTotalPrice: LiveData<Int> get() = _selectedCartItemsTotalPrice

    private var _selectedCartItemsCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedCartItemsCount: LiveData<Int> get() = _selectedCartItemsCount

    private var _navigationOrderEvent = MutableSingleLiveData<OrderInformation>()
    val navigationOrderEvent: SingleLiveData<OrderInformation> get() = _navigationOrderEvent

    private var _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadAll() {
        viewModelScope.launch {
            handleResponseResult(
                responseResult = cartItemRepository.loadCartItems(),
                onSuccess = { cartItems ->
                    _cartItems.value = cartItems.map { it.toUiModel() }
                    _isLoading.value = false
                },
                onError = { message ->
                    _errorMessage.value = message
                },
            )
        }
    }

    fun deleteItem(cartItemId: Long) {
        viewModelScope.launch {
            handleResponseResult(cartItemRepository.delete(cartItemId), { }, { })
            handleResponseResult(
                responseResult = cartItemRepository.loadCartItems(),
                onSuccess = { cartItems ->
                    _cartItems.value = cartItems.map { it.toUiModel() }
                },
                onError = { message ->
                    _errorMessage.value = message
                },
            )
        }
        updateSelectedCartItemsCount()
    }

    private fun updateSelectedCartItemsCount() {
        _selectedCartItemsCount.value = cartItems.value?.count { it.checked }
    }

    override fun navigateToOrder() {
        if (selectedCartItemsCount.value == 0) return

        val selectedCartItems = cartItems.value?.filter { it.checked }?.map { it.toDomain() } ?: return
        _navigationOrderEvent.setValue(
            OrderInformation(
                selectedCartItems,
            ),
        )
    }

    override fun delete(cartItemId: Long) {
        _deletedItemId.setValue(cartItemId)
    }

    override fun onIncrease(
        cartItemId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartItemRepository.updateCartItemQuantity(cartItemId, quantity)
            updateCartItems()
            updateTotalPrice()
        }
    }

    override fun onDecrease(
        cartItemId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartItemRepository.updateCartItemQuantity(cartItemId, quantity)
            updateCartItems()
            updateTotalPrice()
            updateSelectedCartItemsCount()
        }
    }

    private suspend fun updateCartItems() {
        val cartItems = cartItems.value ?: return
        handleResponseResult(
            responseResult = cartItemRepository.loadCartItems(),
            onSuccess = { currentItems ->
                _cartItems.value =
                    currentItems.map { cartItem ->
                        cartItem.toUiModel().copy(checked = cartItems.first { it.id == cartItem.id }.checked)
                    }
            },
            onError = { message ->
                _errorMessage.value = message
            },
        )
    }

    override fun selected(cartItemId: Long) {
        val selectedItem =
            cartItems.value?.find { it.id == cartItemId } ?: throw IllegalStateException()
        val changedItem = selectedItem.copy(checked = !selectedItem.checked)

        _cartItems.value =
            cartItems.value?.map {
                if (it.id == cartItemId) {
                    changedItem
                } else {
                    it
                }
            }
        updateTotalPrice()
        updateSelectedCartItemsCount()
        _isAllSelected.value = cartItems.value?.all { it.checked }
    }

    private fun updateTotalPrice() {
        val cartItems = cartItems.value ?: return
        _selectedCartItemsTotalPrice.value =
            cartItems.filter { it.checked }.sumOf {
                it.product.price * it.quantity
            }
    }

    override fun selectedAll() {
        val isAllSelected = isAllSelected.value ?: false
        updateCartItemsChecked(checked = isAllSelected.not())
        updateTotalPrice()
        _isAllSelected.value = isAllSelected.not()
        if (isAllSelected.not()) updateSelectedCartItemsCount()
    }

    private fun updateCartItemsChecked(checked: Boolean) {
        _cartItems.value =
            cartItems.value?.map { cartItem ->
                cartItem.copy(checked = checked)
            }
    }

    companion object {
        private const val TAG = "ShoppingCartViewModel"

        fun factory(
            cartItemRepository: CartItemRepository =
                DefaultCartItemRepository(
                    cartItemDataSource = ShoppingApp.cartSource,
                ),
        ): UniversalViewModelFactory =
            UniversalViewModelFactory {
                ShoppingCartViewModel(cartItemRepository)
            }
    }
}

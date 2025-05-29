package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
import kotlin.math.max

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItemUiModel>>()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems

    private val _deleteState = MutableLiveData<Long>()
    val deleteState: LiveData<Long> = _deleteState

    private val _itemUpdateEvent = MutableLiveData<CartItemUiModel>()
    val itemUpdateEvent: LiveData<CartItemUiModel> = _itemUpdateEvent

    private val _page = MutableLiveData(START_PAGE)
    val page: LiveData<Int> = _page

    private val _hasMore = MutableLiveData<Boolean>()
    val hasMore: LiveData<Boolean> = _hasMore

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> = _totalCount

    private val _allSelected = MutableLiveData<Boolean>()
    val allSelected: LiveData<Boolean> = _allSelected

    private val selectedStates = mutableMapOf<Long, Boolean>()

    init {
        fetchShoppingCart(false)
    }

    fun fetchShoppingCart(
        isNextPage: Boolean,
        isRefresh: Boolean = false,
    ) {
        val currentPage = _page.value ?: DEFAULT_PAGE
        val newPage = calculatePage(isNextPage, currentPage, isRefresh)

        cartRepository.getCartItems(
            page = newPage - DEFAULT_PAGE,
            limit = PAGE_SIZE,
        ) { products, hasMore ->
            val updatedItems =
                products.map {
                    it.toCartItemUiModel().copy(isSelected = selectedStates[it.cartId] ?: false)
                }
            _cartItems.postValue(updatedItems)
            _page.postValue(newPage)
            _hasMore.postValue(hasMore)
            updateSelectionInfo()
        }
    }

    fun deleteProduct(cartItem: CartItemUiModel) {
        selectedStates.remove(cartItem.cartItem.cartId)
        cartRepository.deleteCartItem(cartItem.cartItem.cartId) {
            _deleteState.postValue(it)
        }
    }

    fun increaseAmount(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        cartRepository.increaseCartItem(cartItem) { id ->
            getCartItemById(id) {
                it?.let { item ->
                    val updatedItem =
                        item.toCartItemUiModel().copy(
                            isSelected = selectedStates[item.cartId] ?: false,
                        )
                    _itemUpdateEvent.postValue(updatedItem)
                    updateProducts(updatedItem)
                }
            }
        }
    }

    fun decreaseAmount(product: ProductUiModel) {
        val cartItem = product.toCartItem().toCartItemUiModel()
        if (cartItem.cartItem.amount <= 1) {
            deleteProduct(cartItem)
        } else {
            cartRepository.decreaseCartItem(cartItem.cartItem) { id ->
                getCartItemById(id) {
                    it?.let { item ->
                        val updatedItem =
                            item.toCartItemUiModel().copy(
                                isSelected = selectedStates[item.cartId] ?: false,
                            )
                        _itemUpdateEvent.postValue(updatedItem)
                        updateProducts(updatedItem)
                    }
                }
            }
        }
    }

    fun setCartItemSelection(
        cartItem: CartItemUiModel,
        isSelected: Boolean,
    ) {
        selectedStates[cartItem.cartItem.cartId] = isSelected
        _cartItems.postValue(
            _cartItems.value?.map {
                if (it.cartItem.cartId == cartItem.cartItem.cartId) {
                    it.copy(isSelected = isSelected)
                } else {
                    it
                }
            },
        )
        updateSelectionInfo()
    }

    fun setAllSelections(selectAll: Boolean) {
        cartRepository.getAllCartItems { allItems ->
            allItems?.forEach { cartItem ->
                selectedStates[cartItem.cartId] = selectAll
                _cartItems.postValue(
                    _cartItems.value?.map {
                        if (it.cartItem.cartId == cartItem.cartId) {
                            it.copy(isSelected = selectAll)
                        } else {
                            it
                        }
                    },
                )
            }
            updateSelectionInfo()
            fetchShoppingCart(false, true)
        }
    }

    private fun calculatePage(
        isNextPage: Boolean,
        currentPage: Int,
        isRefresh: Boolean,
    ): Int =
        when {
            isRefresh -> currentPage
            isNextPage -> currentPage + DEFAULT_PAGE
            else -> max(DEFAULT_PAGE, currentPage - DEFAULT_PAGE)
        }

    private fun getCartItemById(
        id: Long,
        callback: (CartItem?) -> Unit,
    ) {
        cartRepository.getAllCartItems { items ->
            val foundItem = items?.find { it.cartId == id }
            callback(foundItem)
        }
    }

    private fun updateProducts(updatedItem: CartItemUiModel) {
        _cartItems.value =
            _cartItems.value?.map { cartItem ->
                if (cartItem.cartItem.cartId == updatedItem.cartItem.cartId) {
                    updatedItem
                } else {
                    cartItem
                }
            }
        updateSelectionInfo()
    }

    private fun updateSelectionInfo() {
        cartRepository.getAllCartItems { allItems ->
            val selectedItemIds = selectedStates.filter { it.value }.map { it.key }.toSet()
            val selectedItems =
                (allItems.orEmpty()).filter { selectedItemIds.contains(it.cartId) }
            _totalPrice.postValue(selectedItems.sumOf { it.totalPrice })
            _totalCount.postValue(selectedItems.sumOf { it.amount })
            _allSelected.postValue(selectedItems.size == allItems?.size)
        }
    }

    companion object {
        private const val START_PAGE = 0
        private const val DEFAULT_PAGE = 1
        private const val PAGE_SIZE = 5

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val repository = RepositoryProvider.cartRepository
                    return CartViewModel(repository) as T
                }
            }
    }
}

package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.model.toProductUiModel
import woowacourse.shopping.presentation.model.toUiModel
import kotlin.math.max

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItemUiModel>>()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems

    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>()
    val recommendedProducts: LiveData<List<ProductUiModel>> = _recommendedProducts

    private val _deleteState = MutableLiveData<Long>()
    val deleteState: LiveData<Long> = _deleteState

    private val _itemUpdateEvent = MutableLiveData<ProductUiModel>()
    val itemUpdateEvent: LiveData<ProductUiModel> = _itemUpdateEvent

    private val _page = MutableLiveData(START_PAGE)
    val page: LiveData<Int> = _page

    private var _hasMore = MutableLiveData<Boolean>()
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

        viewModelScope.launch {
            cartRepository
                .getCartItems(
                    page = newPage - DEFAULT_PAGE,
                    limit = PAGE_SIZE,
                ).onSuccess { (items, hasMore) ->
                    val updatedItems =
                        items.map {
                            val isSelected = selectedStates[it.cartId] ?: false
                            it.toCartItemUiModel().copy(isSelected = isSelected)
                        }
                    _cartItems.postValue(updatedItems)
                    _page.postValue(newPage)
                    _hasMore.postValue(hasMore)
                    updateSelectionInfo()
                }
        }
    }

    fun initialAddToCart(product: ProductUiModel) {
        viewModelScope.launch {
            cartRepository
                .addOrIncreaseCartItem(product.id)
                .onSuccess { cartId ->
                    selectedStates[cartId] = true
                    val updatedCartItem = product.toCartItem().toCartItemUiModel().copy(cartId = cartId, amount = 1, isSelected = true)
                    updateProductAmountInLists(cartId, 1)
                    _itemUpdateEvent.postValue(updatedCartItem.toProductUiModel())
                    updateProducts(updatedCartItem)
                    fetchShoppingCart(isNextPage = false, isRefresh = true)
                }
        }
    }

    fun deleteProduct(cartItem: CartItemUiModel) {
        val cartId = cartItem.cartId

        selectedStates.remove(cartId)
        _cartItems.postValue(
            _cartItems.value?.filterNot {
                it.cartId == cartId
            },
        )

        updateProductAmountInLists(cartId, 0)
        viewModelScope.launch {
            cartRepository
                .deleteCartItem(cartId)
                .onSuccess {
                    _deleteState.postValue(it)
                    updateSelectionInfo()
                }
        }
    }

    private fun updateProductAmountInLists(
        cartId: Long,
        newAmount: Int,
    ) {
        _recommendedProducts.value =
            _recommendedProducts.value
                ?.map { product ->
                    if (product.cartId == cartId) {
                        val updated = product.copy(amount = newAmount)
                        _itemUpdateEvent.postValue(updated)
                        updated
                    } else {
                        product
                    }
                }

        _cartItems.postValue(
            _cartItems.value
                ?.map {
                    if (it.cartId == cartId) {
                        it.copy(amount = newAmount)
                    } else {
                        it
                    }
                }?.toList(),
        )
    }

    fun increaseAmount(product: ProductUiModel) {
        val cartItem = product.toCartItem()

        viewModelScope.launch {
            cartRepository.increaseCartItem(cartItem).onSuccess { cartId ->
                val item = getCartItemById(cartId)
                item?.let {
                    val updatedItem =
                        it.toCartItemUiModel().copy(
                            isSelected = selectedStates[it.cartId] ?: false,
                        )
                    updateProductAmountInLists(it.cartId, updatedItem.amount)
                    _itemUpdateEvent.postValue(updatedItem.toProductUiModel())
                    updateProducts(updatedItem)
                }
            }
        }
    }

    fun decreaseAmount(product: ProductUiModel) {
        val cartItem = product.toCartItem()

        viewModelScope.launch {
            if (cartItem.amount <= 1) {
                deleteProduct(cartItem.toCartItemUiModel())
                updateProductAmountInLists(cartItem.cartId, 0)
            } else {
                cartRepository.decreaseCartItem(cartItem).onSuccess { cartId ->
                    val item = getCartItemById(cartId)
                    item?.let {
                        val updatedItem =
                            it.toCartItemUiModel().copy(
                                isSelected = selectedStates[it.cartId] ?: false,
                            )
                        updateProductAmountInLists(it.cartId, updatedItem.amount)
                        _itemUpdateEvent.postValue(updatedItem.toProductUiModel())
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
        selectedStates[cartItem.cartId] = isSelected
        _cartItems.postValue(
            _cartItems.value?.map {
                if (it.cartId == cartItem.cartId) {
                    it.copy(isSelected = isSelected)
                } else {
                    it
                }
            },
        )
        updateSelectionInfo()
    }

    fun setAllSelections(selectAll: Boolean) {
        viewModelScope.launch {
            cartRepository
                .getAllCartItems()
                .onSuccess { allItems ->
                    allItems.forEach {
                        selectedStates[it.cartId] = selectAll
                    }
                    _cartItems.postValue(
                        _cartItems.value?.map {
                            it.copy(isSelected = selectAll)
                        },
                    )
                    updateSelectionInfo()
                    fetchShoppingCart(false, true)
                }
        }
    }

    fun fetchRecommendedProducts() {
        viewModelScope.launch {
            productRepository
                .getMostRecentProduct()
                .onSuccess { recentProduct ->
                    val category = recentProduct.category

                    val cartItems = cartRepository.getAllCartItems().getOrNull().orEmpty()
                    val cartProductIds = cartItems.map { it.product.id }.toSet()

                    productRepository
                        .loadProductsByCategory(category)
                        .onSuccess { allProducts ->
                            val filtered =
                                allProducts
                                    .filterNot { it.id in cartProductIds }
                                    .take(10)
                            _recommendedProducts.postValue(filtered.map { it.toUiModel() })
                        }
                }
        }
    }

    fun getSelectedCartItems(): List<CartItemUiModel> =
        _cartItems.value
            .orEmpty()
            .filter { selectedStates[it.cartId] == true }

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

    private suspend fun getCartItemById(id: Long): CartItem? = cartRepository.getAllCartItems().getOrNull()?.find { it.cartId == id }

    private fun updateProducts(updatedItem: CartItemUiModel) {
        _cartItems.value =
            _cartItems.value?.map { cartItem ->
                if (cartItem.cartId == updatedItem.cartId) {
                    updatedItem
                } else {
                    cartItem
                }
            }
        updateSelectionInfo()
    }

    private fun updateSelectionInfo() {
        viewModelScope.launch {
            cartRepository
                .getAllCartItems()
                .onSuccess { allItems ->
                    val selectedItemIds = selectedStates.filterValues { it }.keys
                    val selectedItems = allItems.filter { it.cartId in selectedItemIds }
                    _totalPrice.postValue(selectedItems.sumOf { it.totalPrice })
                    _totalCount.postValue(selectedItems.sumOf { it.amount })
                    _allSelected.postValue(selectedItems.size == allItems.size)
                }
        }
    }

    companion object {
        private const val START_PAGE = 0
        private const val DEFAULT_PAGE = 1
        private const val PAGE_SIZE = 5

        fun factory(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T = CartViewModel(productRepository, cartRepository) as T
            }
    }
}

package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
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

    private val _itemUpdateEvent = MutableLiveData<CartItemUiModel>()
    val itemUpdateEvent: LiveData<CartItemUiModel> = _itemUpdateEvent

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

    fun increaseQuantity(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        if (product.quantity == 0) {
            addToCart(cartItem.toCartItemUiModel())
            return
        }
        cartRepository.increaseQuantity(cartItem) { id ->
            getCartItemById(id) { foundItem ->
                foundItem?.let {
                    val updatedItem =
                        it.toCartItemUiModel().copy(isSelected = selectedStates[it.cartId] ?: false)
                    _itemUpdateEvent.postValue(updatedItem)
                    updateProducts(updatedItem)
                }
            }
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        if (cartItem.quantity == 1) {
            removeFromCart(cartItem.toCartItemUiModel())
            return
        }
        cartRepository.decreaseQuantity(cartItem) { id ->
            getCartItemById(id) { foundItem ->
                foundItem?.let {
                    val updatedItem =
                        it.toCartItemUiModel().copy(isSelected = selectedStates[it.cartId] ?: false)
                    _itemUpdateEvent.postValue(updatedItem)
                    updateProducts(updatedItem)
                }
            }
        }
    }

    private fun addToCart(cartItem: CartItemUiModel) {
        val newItem = cartItem.cartItem.copy(quantity = 1)
        cartRepository.addCartItem(newItem) {
            cartRepository.getAllCartItems { cartItems ->
                val found = cartItems?.find { it.product.id == newItem.product.id }
                if (found != null) {
                    _itemUpdateEvent.postValue(found.toCartItemUiModel().copy(isSelected = true))
                    setCartItemSelection(found.toCartItemUiModel(), true)
                } else {
                    _itemUpdateEvent.postValue(newItem.toCartItemUiModel().copy(isSelected = true))
                    fetchRecommendedProducts()
                }
            }
        }
    }

    fun removeFromCart(cartItem: CartItemUiModel) {
        val removedItem = cartItem.cartItem.copy(quantity = 0)
        selectedStates.remove(cartItem.cartItem.cartId)
        cartRepository.deleteCartItem(cartItem.cartItem.cartId) { id ->
            cartRepository.getAllCartItems { cartItems ->
                val found = cartItems?.find { it.product.id == removedItem.product.id }
                if (found != null) {
                    _itemUpdateEvent.postValue(found.toCartItemUiModel().copy(isSelected = false))
                } else {
                    _itemUpdateEvent.postValue(removedItem.toCartItemUiModel().copy(isSelected = false))
                    fetchRecommendedProducts()
                }
            }
            _deleteState.postValue(id)
        }
        updateSelectionInfo()
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

    fun fetchRecommendedProducts() {
        productRepository.getMostRecentProduct { mostRecentProduct ->
            val recommendedCategory = mostRecentProduct?.category
            productRepository.loadProductsByCategory(recommendedCategory.orEmpty()) { productsInRecommendedCategory ->
                cartRepository.getAllCartItems { allCartItems ->
                    val recommendedProducts =
                        productsInRecommendedCategory
                            .filterNot { productInRecommendedCategory ->
                                allCartItems
                                    .orEmpty()
                                    .map { cartItem ->
                                        cartItem.product.id
                                    }.contains(productInRecommendedCategory.id)
                            }.take(10)
                    _recommendedProducts.postValue(recommendedProducts.map { it.toUiModel() })
                }
            }
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
        cartRepository.getAllCartItems { cartItems ->
            val foundItem = cartItems?.find { cartItem -> cartItem.cartId == id }
            callback(foundItem)
        }
    }

    private fun updateProducts(updatedItem: CartItemUiModel) {
        _cartItems.postValue(
            _cartItems.value?.map { cartItem ->
                if (cartItem.cartItem.cartId == updatedItem.cartItem.cartId) {
                    updatedItem
                } else {
                    cartItem
                }
            },
        )
        updateSelectionInfo()
    }

    private fun updateSelectionInfo() {
        cartRepository.getAllCartItems { cartItems ->
            val selectedItemIds = selectedStates.filter { it.value }.map { it.key }.toSet()
            val selectedItems = (cartItems.orEmpty()).filter { selectedItemIds.contains(it.cartId) }
            _totalPrice.postValue(selectedItems.sumOf { it.totalPrice })
            _totalCount.postValue(selectedItems.sumOf { it.quantity })
            _allSelected.postValue(selectedItems.size == cartItems?.size)
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
                    val productRepository = RepositoryProvider.productRepository
                    val cartRepository = RepositoryProvider.cartRepository
                    return CartViewModel(productRepository, cartRepository) as T
                }
            }
    }
}

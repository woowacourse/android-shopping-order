package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.data.RepositoryProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.model.toProductUiModel
import kotlin.math.max

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItemUiModel>>()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems

    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>()
    val recommendedProducts: LiveData<List<ProductUiModel>> = _recommendedProducts

    private val _deleteEvent = MutableLiveData<Long>()
    val deleteEvent: LiveData<Long> = _deleteEvent

    private val _itemUpdateEvent = MutableLiveData<CartItemUiModel>()
    val itemUpdateEvent: LiveData<CartItemUiModel> = _itemUpdateEvent

    private val _page = MutableLiveData(START_PAGE)
    val page: LiveData<Int> = _page

    private var _isFirstPage = MutableLiveData<Boolean>()
    val isFirstPage: LiveData<Boolean> = _isFirstPage

    private var _isLastPage = MutableLiveData<Boolean>()
    val isLastPage: LiveData<Boolean> = _isLastPage

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> = _totalCount

    private val _allSelected = MutableLiveData<Boolean>()
    val allSelected: LiveData<Boolean> = _allSelected

    private val selectionStatus = mutableMapOf<Long, Boolean>()

    init {
        fetchShoppingCart(false)
    }

    fun fetchShoppingCart(
        isNextPage: Boolean,
        isRefresh: Boolean = false,
    ) {
        val currentPage = _page.value ?: DEFAULT_PAGE
        val newPage = calculatePage(isNextPage, currentPage, isRefresh)

        cartRepository.loadPageOfCartItems(
            pageIndex = newPage - DEFAULT_PAGE,
            pageSize = PAGE_SIZE,
        ) { products, isFirstPage, isLastPage ->
            val updatedItems =
                products.map { product ->
                    product
                        .toCartItemUiModel()
                        .copy(isSelected = selectionStatus[product.cartId] ?: false)
                }
            _cartItems.postValue(updatedItems)
            _page.postValue(newPage)
            _isFirstPage.postValue(isFirstPage)
            _isLastPage.postValue(isLastPage)
            updateSelectionInfo()
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        if (product.quantity == 0) {
            addToCart(cartItem.toCartItemUiModel())
            return
        }
        cartRepository.increaseQuantity(cartItem) {
            val updatedItem =
                CartItemUiModel(
                    cartItem.copy(quantity = cartItem.quantity + 1),
                    isSelected = selectionStatus[cartItem.cartId] ?: false,
                )
            _itemUpdateEvent.postValue(updatedItem)
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        if (cartItem.quantity == 1) {
            removeFromCart(cartItem.toCartItemUiModel())
            return
        }
        cartRepository.decreaseQuantity(cartItem) {
            val updatedItem =
                CartItemUiModel(
                    cartItem.copy(quantity = cartItem.quantity - 1),
                    isSelected = selectionStatus[cartItem.cartId] ?: false,
                )
            _itemUpdateEvent.postValue(updatedItem)
        }
    }

    private fun addToCart(cartItem: CartItemUiModel) {
        val newItem = cartItem.cartItem.copy(quantity = 1)
        cartRepository.addCartItem(newItem) { addedItem ->
            if (addedItem != null) {
                _itemUpdateEvent.postValue(addedItem.toCartItemUiModel().copy(isSelected = true))
                setCartItemSelection(addedItem.toCartItemUiModel(), true)
            }
        }
    }

    fun removeFromCart(cartItem: CartItemUiModel) {
        val removedItem = cartItem.cartItem.copy(quantity = 0)
        cartRepository.deleteCartItem(removedItem.cartId) {
            _deleteEvent.postValue(removedItem.cartId)
            selectionStatus.remove(removedItem.cartId)
            updateSelectionInfo()
            fetchRecommendedProducts()
        }
    }

    fun setCartItemSelection(
        cartItem: CartItemUiModel,
        isSelected: Boolean,
    ) {
        selectionStatus[cartItem.cartItem.cartId] = isSelected
        _cartItems.postValue(
            _cartItems.value?.map { item ->
                if (item.cartItem.cartId == cartItem.cartItem.cartId) {
                    item.copy(isSelected = isSelected)
                } else {
                    item
                }
            },
        )
        updateSelectionInfo()
    }

    fun setAllSelections(selectAll: Boolean) {
        cartRepository.loadAllCartItems { allItems ->
            allItems.forEach { cartItem ->
                selectionStatus[cartItem.cartId] = selectAll
                _cartItems.postValue(
                    _cartItems.value?.map { item ->
                        if (item.cartItem.cartId == cartItem.cartId) {
                            item.copy(isSelected = selectAll)
                        } else {
                            item
                        }
                    },
                )
            }
            updateSelectionInfo()
            fetchShoppingCart(isNextPage = false, isRefresh = true)
        }
    }

    fun fetchRecommendedProducts() {
        productRepository.getMostRecentProduct { mostRecentProduct ->
            val recommendedCategory = mostRecentProduct?.category
            productRepository.loadProductsByCategory(recommendedCategory.orEmpty()) { productsInRecommendedCategory ->
                cartRepository.loadAllCartItems { allCartItems ->
                    val recommendedProducts =
                        productsInRecommendedCategory
                            .filterNot { productInRecommendedCategory ->
                                allCartItems
                                    .map { cartItem ->
                                        cartItem.product.id
                                    }.contains(productInRecommendedCategory.id)
                            }.take(10)
                    _recommendedProducts.postValue(recommendedProducts.map { it.toProductUiModel() })
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
        cartRepository.loadAllCartItems { cartItems ->
            val selectedItemIds = selectionStatus.filter { it.value }.map { it.key }.toSet()
            val selectedItems = cartItems.filter { selectedItemIds.contains(it.cartId) }
            _totalPrice.postValue(selectedItems.sumOf { it.totalPrice })
            _totalCount.postValue(selectedItems.sumOf { it.quantity })
            _allSelected.postValue(selectedItems.isNotEmpty() && selectedItems.size == cartItems.size)
        }
    }

    companion object {
        private const val START_PAGE = 0
        private const val DEFAULT_PAGE = 1
        private const val PAGE_SIZE = 5

        @Suppress("UNCHECKED_CAST")
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

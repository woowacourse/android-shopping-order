package woowacourse.shopping.ui.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.recommender.ProductRecommender
import woowacourse.shopping.ui.common.model.LoadState
import woowacourse.shopping.ui.common.model.ProductUiModel
import java.io.IOException

class CartViewModel(
    private val recentProductRepo: RecentProductRepository,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository,
    private val pageSize: Int,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState(pageSize = pageSize))
    private val _events = Channel<String>(Channel.BUFFERED)
    val uiState = _uiState.asStateFlow()
    val events = _events.receiveAsFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadState = LoadState.Loading) }
            try {
                refreshData()
                _uiState.update {
                    it.copy(
                        loadState = if (it.totalCartItemCount == 0) LoadState.Empty else LoadState.Success
                    )
                }
            } catch (e: Exception) {
                handleError("loadCart", e, "장바구니를 불러올 수 없어요.")
                _uiState.update { it.copy(loadState = LoadState.Error) }
            }
        }
    }

    fun increase(item: CartItem) {
        viewModelScope.launch {
            try {
                val cartItemId = item.id
                    ?: throw IllegalArgumentException("상품(${item.product.name})의 카트 아이템 아이디가 null 입니다.")
                cartRepo.updateQuantity(cartItemId, item.quantity + 1)
                _uiState.update { state ->
                    val updatedItems = state.pagedItems.map {
                        if (it.id == cartItemId) it.copy(quantity = it.quantity + 1) else it
                    }
                    val isSelected = cartItemId in state.selectedItemIds
                    state.copy(
                        pagedItems = updatedItems,
                        totalSelectedCount = if (isSelected) state.totalSelectedCount + 1 else state.totalSelectedCount,
                        totalSelectedPrice = if (isSelected) state.totalSelectedPrice + item.product.price.value else state.totalSelectedPrice
                    )
                }
            } catch (e: Exception) {
                handleError("increase", e, "수량을 변경할 수 없어요.")
            }
        }
    }

    fun decrease(item: CartItem) {
        viewModelScope.launch {
            try {
                val cartItemId = item.id
                    ?: throw IllegalArgumentException("상품(${item.product.name})의 카트 아이템 아이디가 null 입니다.")
                if (item.quantity <= 1) {
                    cartRepo.delete(cartItemId)
                    refreshData()
                    _uiState.update {
                        it.copy(
                            loadState = if (it.totalCartItemCount == 0) LoadState.Empty else LoadState.Success
                        )
                    }
                } else {
                    cartRepo.updateQuantity(cartItemId, item.quantity - 1)
                    _uiState.update { state ->
                        val updatedItems = state.pagedItems.map {
                            if (it.id == cartItemId) it.copy(quantity = it.quantity - 1) else it
                        }
                        val isSelected = cartItemId in state.selectedItemIds
                        state.copy(
                            pagedItems = updatedItems,
                            totalSelectedCount = if (isSelected) state.totalSelectedCount - 1 else state.totalSelectedCount,
                            totalSelectedPrice = if (isSelected) state.totalSelectedPrice - item.product.price.value else state.totalSelectedPrice
                        )
                    }
                }
            } catch (e: Exception) {
                handleError("decrease", e, "수량을 변경할 수 없어요.")
            }
        }
    }

    fun delete(item: CartItem) {
        val cartItemId = item.id
            ?: throw IllegalArgumentException("상품(${item.product.name})의 카트 아이템 아이디가 null 입니다.")

        viewModelScope.launch {
            try {
                cartRepo.delete(cartItemId)

                val state = _uiState.value
                val wasSelected = cartItemId in state.selectedItemIds
                val newItems = state.pagedItems.filterNot { it.id == cartItemId }

                if (newItems.isEmpty() && state.currentPage > 0) {
                    _uiState.update { it.copy(currentPage = it.currentPage - 1) }
                    refreshData()
                    _uiState.update {
                        it.copy(
                            loadState = if (it.totalCartItemCount == 0) LoadState.Empty else LoadState.Success
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    val newCount = it.totalCartItemCount - 1
                    it.copy(
                        pagedItems = newItems,
                        selectedItemIds = it.selectedItemIds - cartItemId,
                        totalCartItemCount = newCount,
                        totalSelectedPrice = if (wasSelected) it.totalSelectedPrice - item.product.price.value else it.totalSelectedPrice,
                        totalSelectedCount = if (wasSelected) it.totalSelectedCount - 1 else it.totalSelectedCount,
                        loadState = if (newCount == 0) LoadState.Empty else it.loadState,
                    )
                }
            } catch (e: Exception) {
                handleError("delete", e, "상품을 삭제할 수 없어요.")
            }
        }
    }

    fun nextPage() {
        val state = _uiState.value
        if (state.currentPage + 1 >= state.totalPages) return
        viewModelScope.launch {
            try {
                val page = cartRepo.getPagedItems(state.currentPage + 1, pageSize)
                _uiState.update {
                    it.copy(currentPage = page.currentPage, pagedItems = page.items)
                }
            } catch (e: Exception) {
                handleError("nextPage", e, "페이지를 불러올 수 없어요.")
            }
        }
    }

    fun previousPage() {
        val state = _uiState.value
        if (state.currentPage <= 0) return
        viewModelScope.launch {
            try {
                val page = cartRepo.getPagedItems(state.currentPage - 1, pageSize)
                _uiState.update {
                    it.copy(currentPage = page.currentPage, pagedItems = page.items)
                }
            } catch (e: Exception) {
                handleError("previousPage", e, "페이지를 불러올 수 없어요.")
            }
        }
    }

    fun toggleItemSelection(itemId: Long, isSelected: Boolean) {
        _uiState.update { state ->
            val newSelected = if (isSelected) state.selectedItemIds + itemId
            else state.selectedItemIds - itemId
            state.copy(
                selectedItemIds = newSelected,
                isAllSelected = newSelected.isNotEmpty() && newSelected.size == state.totalCartItemCount,
            )
        }
        viewModelScope.launch {
            try {
                recalculateTotals()
            } catch (e: Exception) {
                handleError("toggleItemSelection", e, "선택 정보를 갱신할 수 없어요.")
            }
        }
    }

    fun toggleAllItemsSelection(isSelected: Boolean) {
        viewModelScope.launch {
            try {
                val cartItems = cartRepo.getAllCartItems()
                val allIds = cartItems.items
                    .map { it.id ?: throw IllegalArgumentException("아이템에 id가 없습니다.") }
                    .toSet()

                _uiState.update {
                    if (isSelected && cartItems.items.isNotEmpty()) {
                        it.copy(selectedItemIds = allIds, isAllSelected = true)
                    } else {
                        it.copy(selectedItemIds = emptySet(), isAllSelected = false)
                    }
                }
                recalculateTotals()
            } catch (e: Exception) {
                handleError("toggleAllItemsSelection", e, "전체 선택을 처리할 수 없어요.")
            }
        }
    }

    fun changeScreen() {
        val state = _uiState.value
        if (state.isCartScreen && state.selectedItemIds.isNotEmpty()) {
            _uiState.update { it.copy(recommendItems = emptyList(), isCartScreen = false) }
            getRecommendProducts()
        } else {
            _uiState.update { it.copy(isCartScreen = true) }
        }
    }

    fun order(selectedIds: List<Long>) {
        viewModelScope.launch {
            try {
                orderRepo.requestOrder(selectedIds)
                _events.send("주문이 완료되었어요.")
            } catch (e: Exception) {
                handleError("order", e, "주문에 실패했어요.")
            }
        }
    }

    fun increaseInRecommendScreen(uiModel: ProductUiModel) {
        viewModelScope.launch {
            try {
                val cartItemId = if (uiModel.cartItemId == null) {
                    cartRepo.add(uiModel.product.id, quantity = 1)
                } else {
                    cartRepo.updateQuantity(uiModel.cartItemId, uiModel.quantity + 1)
                    uiModel.cartItemId
                }

                _uiState.update { state ->
                    val newUiModels = state.recommendItems.map {
                        if (it.product.id == uiModel.product.id) {
                            it.copy(quantity = uiModel.quantity + 1, cartItemId = cartItemId)
                        } else it
                    }
                    state.copy(
                        recommendItems = newUiModels,
                        selectedItemIds = state.selectedItemIds + cartItemId,
                        totalSelectedCount = state.totalSelectedCount + 1,
                        totalSelectedPrice = state.totalSelectedPrice + uiModel.product.price.value
                    )
                }
            } catch (e: Exception) {
                handleError("increaseInRecommendScreen", e, "장바구니에 담을 수 없어요.")
            }
        }
    }

    fun decreaseInRecommendScreen(uiModel: ProductUiModel) {
        viewModelScope.launch {
            try {
                val cartItemId = uiModel.cartItemId
                    ?: throw IllegalArgumentException("상품(${uiModel.product.name})에 카트아이템 아이디가 없습니다.")

                if (uiModel.quantity > 1) {
                    cartRepo.updateQuantity(cartItemId, uiModel.quantity - 1)
                } else {
                    cartRepo.delete(cartItemId)
                }

                _uiState.update { state ->
                    val newUiModels = state.recommendItems.map {
                        if (it.product.id == uiModel.product.id) {
                            it.copy(
                                quantity = maxOf(0, uiModel.quantity - 1),
                                cartItemId = if (uiModel.quantity == 1) null else cartItemId
                            )
                        } else it
                    }
                    state.copy(
                        recommendItems = newUiModels,
                        selectedItemIds = if (uiModel.quantity == 1) state.selectedItemIds - cartItemId
                        else state.selectedItemIds,
                        totalSelectedPrice = state.totalSelectedPrice - uiModel.product.price.value,
                        totalSelectedCount = state.totalSelectedCount - 1
                    )
                }
            } catch (e: Exception) {
                handleError("decreaseInRecommendScreen", e, "수량을 변경할 수 없어요.")
            }
        }
    }

    private fun getRecommendProducts() {
        viewModelScope.launch {
            try {
                val products = ProductRecommender.getRecommendProducts(
                    lastViewedItem = recentProductRepo.getLastViewedProduct(),
                    allProductItems = productRepo.getProducts(0, 50).items,
                    allCartItem = cartRepo.getAllCartItems().items,
                )
                val uiModels = products.map { ProductUiModel(product = it) }
                _uiState.update { it.copy(recommendItems = uiModels) }
            } catch (e: Exception) {
                handleError("getRecommendProducts", e, "추천 상품을 불러올 수 없어요.")
            }
        }
    }

    private suspend fun handleError(tag: String, e: Exception, defaultMessage: String) {
        if (e is CancellationException) throw e
        Log.e(TAG, "$tag 에러", e)
        val msg = when (e) {
            is IOException -> "네트워크 연결을 확인해주세요."
            is HttpException -> when (e.code()) {
                401, 403 -> "다시 로그인이 필요해요."
                in 500..599 -> "서버에 일시적 문제가 있어요."
                else -> defaultMessage
            }
            else -> defaultMessage
        }
        _events.send(msg)
    }

    private suspend fun refreshData() {
        val requestedPage = _uiState.value.currentPage.coerceAtLeast(0)
        val initialPage = cartRepo.getPagedItems(requestedPage, pageSize)

        val finalPage = if (initialPage.totalPages in 1..requestedPage) {
            cartRepo.getPagedItems(initialPage.totalPages - 1, pageSize)
        } else {
            initialPage
        }

        val totalPrice = calculatePrice()
        val totalSelectedCount = calculateTotalSelectedCount()

        _uiState.update {
            it.copy(
                currentPage = finalPage.currentPage,
                pagedItems = finalPage.items,
                totalCartItemCount = finalPage.totalElements,
                totalPages = finalPage.totalPages,
                totalSelectedPrice = totalPrice,
                totalSelectedCount = totalSelectedCount,
                isAllSelected = it.selectedItemIds.isNotEmpty()
                        && it.selectedItemIds.size == finalPage.totalElements,
            )
        }
    }

    private suspend fun recalculateTotals() {
        val totalPrice = calculatePrice()
        val totalSelectedCount = calculateTotalSelectedCount()
        _uiState.update {
            it.copy(totalSelectedPrice = totalPrice, totalSelectedCount = totalSelectedCount)
        }
    }

    private suspend fun calculatePrice(): Long {
        val selectedIds = _uiState.value.selectedItemIds
        if (selectedIds.isEmpty()) return 0
        val cart = cartRepo.getAllCartItems()
        return selectedIds.sumOf { itemId ->
            cart.items.find { it.id == itemId }?.totalPrice?.value ?: 0
        }
    }

    private suspend fun calculateTotalSelectedCount(): Int {
        val selectedIds = _uiState.value.selectedItemIds
        if (selectedIds.isEmpty()) return 0
        val cart = cartRepo.getAllCartItems()
        return selectedIds.sumOf { itemId ->
            cart.items.find { it.id == itemId }?.quantity ?: 0
        }
    }

    companion object {
        private const val TAG = "CartViewModel"

        fun provideFactory(
            cartRepo: CartRepository,
            productRepo: ProductRepository,
            orderRepo: OrderRepository,
            recentProductRepo: RecentProductRepository,
            pageSize: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CartViewModel(
                        cartRepo = cartRepo,
                        pageSize = pageSize,
                        recentProductRepo = recentProductRepo,
                        productRepo = productRepo,
                        orderRepo = orderRepo,
                    ) as T
            }
    }
}

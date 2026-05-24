package woowacourse.shopping.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.feature.common.state.CartItemUiModel
import woowacourse.shopping.feature.common.state.ProductUiModel
import java.io.IOException


class CartViewModel(
    private val initialPageSize: Int = 5,
    private val cartRepository: CartRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _event = Channel<CartEvent>(Channel.BUFFERED)
    val event: Flow<CartEvent> = _event.receiveAsFlow()


    init {
        viewModelScope.launch {
            initialLoading()
        }
    }

    private var cart: Cart = Cart(emptyList())

    fun initialLoading() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                cart = cartRepository.loadCart()
                val cartContents = pagination(
                    page = 1,
                )
                val checkMap: Map<Long, Boolean> =
                    cart.cartContents.map { it.id }.associateWith { true }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        paginatedCartContents = cartContents,
                        checkMap = checkMap,
                        totalPrice = cart.cartContents
                            .filter { checkMap[it.id] == true }
                            .sumOf { it.product.priceAmount() * it.quantity },
                        totalCount = checkMap.count { it.value },
                        isFirstPage = it.page == 1,
                        isLastPage = it.page >= lastPage(initialPageSize),
                    )
                }
            } catch (e: HttpException) {
                _event.send(CartEvent.FatalError("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }

    }

    private suspend fun getCart(): Cart {
        val cart = cartRepository.loadCart()
        return cart
    }

    private fun lastPage(pageSize: Int): Int {
        val size = cart.cartContentsSizeOf()
        if (size == 0) return 1
        return (size + pageSize - 1) / pageSize
    }

    fun moveToPreviousPage() {
        viewModelScope.launch {
            try {
                val page = uiState.value.page - 1
                val cartContents = pagination(page)
                _uiState.update {
                    it.copy(
                        page = page,
                        paginatedCartContents = cartContents,
                        isFirstPage = page == 1,
                        isLastPage = page >= lastPage(initialPageSize),
                    )
                }
            } catch (e: Exception) {
                _event.send(CartEvent.FatalError("페이지 이동에 실패했습니다."))
            }
        }
    }

    fun moveToNextPage() {
        viewModelScope.launch {
            try {
                val page = uiState.value.page + 1
                val cartContents = pagination(page)
                _uiState.update {
                    it.copy(
                        page = page,
                        paginatedCartContents = cartContents,
                        isFirstPage = page == 1,
                        isLastPage = page >= lastPage(initialPageSize),
                    )
                }
            } catch (e: Exception) {
                _event.send(CartEvent.FatalError("페이지 이동에 실패했습니다."))
            }
        }
    }

    private suspend fun pagination(
        page: Int,
        pageSize: Int = 5,
    ): List<CartItemUiModel> {
        val cartContents =
            cartRepository
                .pagination(page - 1, pageSize)
                .map(::toCartItemUiModel)
        return cartContents
    }

    fun increase(contentId: Long) {
        viewModelScope.launch {
            try {
                val cartContent = CartContent(
                    product = cart.cartContents.first { it.id == contentId }.product,
                    quantity = 1,
                    id = contentId,
                )
                val product = cartContent.product

                cartRepository.increase(product)
                cart = cart.plusCartContent(CartContent(product, 1, contentId))

                val cartContents = pagination(uiState.value.page)
                val updateCartContents = getCartContents(cartContents)

                _uiState.update {
                    it.copy(
                        paginatedCartContents = updateCartContents,
                        totalPrice = cart.cartContents
                            .filter { uiState.value.checkMap[it.id] == true }
                            .sumOf { it.product.priceAmount() * it.quantity },
                    )
                }
            } catch (e: Exception) {
                _event.send(CartEvent.FatalError("수량 변경에 실패했습니다."))
            }
        }
    }

    fun decrease(contentId: Long) {
        viewModelScope.launch {
            try {
                cart = getCart()
                val cartContent = CartContent(
                    product = cart.cartContents.first { it.id == contentId }.product,
                    quantity = 1,
                    id = contentId,
                )
                if (!cart.hasCartContent(cartContent)) {
                    _event.send(CartEvent.MinusEvent("해당 상품은 존재하지 않는 상품입니다."))
                    return@launch
                }
                val product = cartContent.product

                cart = cart.minusCartContent(cartContent)
                cartRepository.decrease(product.id)

                val cartContents = pagination(uiState.value.page)
                val updateCartContents = getCartContents(cartContents)
                val page = getPage(cartContents)

                val updateContentKeys = cart.cartContents.map { it.id }
                val newCheckMap =
                    _uiState.value.checkMap.filter { it.key in updateContentKeys }.toMap()

                _uiState.update {
                    it.copy(
                        paginatedCartContents = updateCartContents,
                        checkMap = newCheckMap,
                        totalPrice = cart.cartContents
                            .filter { newCheckMap[it.id] == true }
                            .sumOf { it.product.priceAmount() * it.quantity },
                        totalCount = newCheckMap.count { it.value },
                        isFirstPage = page == 1,
                        isLastPage = page >= lastPage(initialPageSize),
                        page = page,
                    )
                }
            } catch (e: Exception) {
                _event.send(CartEvent.FatalError("수량 변경에 실패했습니다."))
            }
        }
    }

    fun deleteCartItem(productId: Long) {
        viewModelScope.launch {
            try {
                val contentId = cart.cartContents.firstOrNull { it.productId == productId }?.id ?: 0
                cartRepository.remove(contentId)
                val cartContents = pagination(uiState.value.page)
                val updateCartContents = getCartContents(cartContents)
                val page = getPage(cartContents)

                cart = getCart()
                val updateContentKeys = cart.cartContents.map { it.id }
                val newCheckMap = _uiState.value.checkMap.filter { it.key in updateContentKeys }.toMap()

                _uiState.update {
                    it.copy(
                        paginatedCartContents = updateCartContents,
                        page = page,
                        isFirstPage = page == 1,
                        isLastPage = page >= lastPage(initialPageSize),
                        checkMap = newCheckMap,
                        totalCount = newCheckMap.count { it.value },
                        totalPrice = cart.cartContents
                            .filter { newCheckMap[it.id] == true }
                            .sumOf { it.product.priceAmount() * it.quantity },
                    )
                }

                _event.send(CartEvent.RemoveEvent("상품이 삭제되었습니다."))
            } catch (e: Exception) {
                _event.send(CartEvent.FatalError("상품 삭제에 실패했습니다."))
            }
        }
    }

    fun toCartItemUiModel(cartContent: CartContent): CartItemUiModel {
        val product = cartContent.product
        return CartItemUiModel(
            contentId = cartContent.id,
            productUiModel =
                ProductUiModel(
                    name = product.name,
                    price = product.priceAmount(),
                    imageUrl = product.imageUrl,
                    id = product.id,
                    quantity = cartContent.quantity,
                ),
        )
    }

    fun cartItemCheck(contentId: Long) {
        val checkMap = _uiState.value.checkMap.toMutableMap()
        checkMap[contentId] = checkMap[contentId]?.not()
            ?: false
        val totalPrice =
            cart.cartContents
                .filter { checkMap[it.id] == true }
                .sumOf { it.product.priceAmount() * it.quantity }
        _uiState.update {
            it.copy(
                checkMap = checkMap.toMap(),
                totalPrice = totalPrice,
                totalCount = checkMap.filter { it.value }.size,
            )
        }
    }

    fun totalCheck() {
        val isAllChecked = _uiState.value.checkMap.all { it.value }
        val nextCheckState = !isAllChecked

        val newCheckMap = _uiState.value.checkMap.keys.associateWith { nextCheckState }

        val totalPrice = if (nextCheckState) {
            cart.cartContents.sumOf {
                it.product.priceAmount() * it.quantity
            }
        } else {
            0
        }
        val totalCount = if (nextCheckState) newCheckMap.size else 0

        _uiState.update {
            it.copy(
                checkMap = newCheckMap.toMap(),
                totalPrice = totalPrice,
                totalCount = totalCount
            )
        }
    }

    private fun getPage(cartContents: List<CartItemUiModel>): Int = maxOf(
        1,
        if (cartContents.isEmpty()) uiState.value.page - 1 else uiState.value.page,
    )

    private suspend fun getCartContents(cartContents: List<CartItemUiModel>): List<CartItemUiModel> {
        val page = getPage(cartContents)

        return if (cartContents.isEmpty() && page != uiState.value.page) {
            pagination(page)
        } else {
            cartContents
        }
    }

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    val appDependencies = runBlocking { app.appDependenciesDeferred.await() }
                    CartViewModel(5, appDependencies.cartRepository)
                }
            }
    }
}


sealed interface CartEvent {
    data class FatalError(
        val message: String,
    ) : CartEvent

    data class RemoveEvent(val message: String) : CartEvent

    data class MinusEvent(val message: String) : CartEvent
}

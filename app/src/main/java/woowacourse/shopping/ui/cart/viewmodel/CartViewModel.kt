package woowacourse.shopping.ui.cart.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.Fail
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.domain.result.onException
import woowacourse.shopping.domain.result.onFail
import woowacourse.shopping.domain.result.onSuccess
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartError
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartItemsUiState
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartUiModel
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiState
import woowacourse.shopping.ui.utils.AddCartClickListener
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData
import woowacourse.shopping.ui.utils.viewModelLaunch

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(), CountButtonClickListener, AddCartClickListener {
    private val _recommendProducts: MutableLiveData<List<ProductWithQuantity>> = MutableLiveData()
    val recommendProducts: LiveData<List<ProductWithQuantity>> = _recommendProducts

    private val _error: MutableSingleLiveData<CartError> = MutableSingleLiveData()
    val error: SingleLiveData<CartError> get() = _error

    val cartOfRecommendProductCount: LiveData<Int> =
        _recommendProducts.map {
            it.sumOf { it.quantity.value }
        }

    private val _cart: MutableLiveData<CartItemsUiState> = MutableLiveData()
    val cart: LiveData<CartItemsUiState> = _cart

    val totalPrice: MediatorLiveData<Int> =
        MediatorLiveData<Int>().apply {
            addSource(_cart) { value = totalPrice() }
            addSource(_recommendProducts) { value = totalPrice() }
        }

    val isTotalChbChecked: LiveData<Boolean> =
        _cart.map {
            it.cartItems.all { it.isChecked }
        }

    val checkedItemCount: LiveData<Int> =
        _cart.map {
            it.cartItems.filter { it.isChecked }.size
        }

    val isRecommendPage: MutableLiveData<Boolean> = MutableLiveData(false)

    val productWithQuantity: MediatorLiveData<ProductWithQuantityUiState> = MediatorLiveData()

    val noRecommendProductState: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun currentCartState(): CartItemsUiState =
        _cart.value ?: CartItemsUiState(emptyList(), true)

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelLaunch(::cartExceptionHandler) {
            _cart.value = currentCartState().copy(isLoading = true)
            cartRepository.allCartItemsResponse().onSuccess { carts ->
                _cart.value =
                    currentCartState().copy(
                        cartItems = carts.map { it.toUiModel(isAlreadyChecked(it.product.id)) },
                        isLoading = false,
                    )
            }.checkError { _error.setValue(it) }
        }
    }

    fun loadRecommendProducts() {
        viewModelLaunch(::recommendExceptionHandler) {
            val recentProductId: Long = recentProductRepository.mostRecentProductOrNull()?.productId
                ?: DEFAULT_RECENT_PRODUCT_ID
            val category = productRepository.productByIdOrNull(recentProductId)?.category
                ?: DEFAULT_RECOMMEND_CATEGORY
            productRepository.allProductsByCategoryResponse(category).onSuccess {
                _recommendProducts.value = it.map { ProductWithQuantity(product = it) }
                noRecommendProductState.value = false
            }.checkError {
                _error.setValue(it)
                noRecommendProductState.value = true
            }
        }

    }

    private fun totalPrice(): Int {
        val carts = _cart.value?.cartItems?.filter { it.isChecked }?.sumOf { it.totalPrice }
            ?: DEFAULT_TOTAL_PRICE
        val recommends =
            _recommendProducts.value?.filter { it.quantity.value >= 1 }?.sumOf { it.totalPrice }
                ?: DEFAULT_TOTAL_PRICE
        return carts + recommends
    }

    fun removeCartItem(productId: Long) {
        viewModelLaunch(::updateCartExceptionHandler) {
            cartRepository.deleteCartItem(findCartIdByProductId(productId)).onSuccess {
                loadCartItems()
            }.checkError { _error.setValue(it) }
        }
    }

    private fun findIsCheckedByProductId(productId: Long): Boolean {
        val current = requireNotNull(_cart.value)
        return current.cartItems.first { it.productId == productId }.isChecked
    }

    fun clickCheckBox(productId: Long) {
        val checkedCart =
            _cart.value?.cartItems?.firstOrNull { it.productId == productId }
                ?: error("해당하는 카트 아이템이 없습니다.")
        val currentList = requireNotNull(_cart.value?.cartItems?.toMutableList())
        currentList[currentList.indexOf(checkedCart)] =
            checkedCart.copy(isChecked = !checkedCart.isChecked)
        _cart.value = CartItemsUiState(currentList, isLoading = false)
    }

    fun totalCheckBoxCheck(isChecked: Boolean) {
        val currentCarts = requireNotNull(_cart.value)
        _cart.value =
            CartItemsUiState(
                currentCarts.cartItems.map {
                    it.copy(isChecked = isChecked)
                },
                isLoading = false,
            )
    }

    override fun addCart(productId: Long) {
        viewModelLaunch(::updateCartExceptionHandler) {
            cartRepository.postCartItems(productId, INITIAL_CART_COUNT).onSuccess {
                changeRecommendProductCount(productId)
                loadCartItems()
            }.checkError { _error.setValue(it) }
        }
    }

    override fun plusCount(productId: Long) {
        viewModelLaunch(::updateCartExceptionHandler) {
            val cartItem = cartRepository.cartItem(productId)
            cartRepository.patchCartItem(
                cartItem.id,
                cartItem.quantity.value.inc(),
            ).onSuccess {
                if (requireNotNull(isRecommendPage.value)) {
                    changeRecommendProductCount(productId)
                }
                loadCartItems()
            }.checkError {
                _error.setValue(it)
            }
        }
    }

    override fun minusCount(productId: Long) {
        viewModelLaunch(::updateCartExceptionHandler) {
            val cartItem = cartRepository.cartItem(productId)
            if (cartItem.quantity.value <= 0) return@viewModelLaunch
            cartRepository.patchCartItem(
                cartItem.id,
                cartItem.quantity.value.dec(),
            ).onSuccess {
                if (requireNotNull(isRecommendPage.value)) {
                    changeRecommendProductCount(productId)
                }
                loadCartItems()
            }.checkError {
                _error.setValue(it)
            }
        }

    }

    private fun changeRecommendProductCount(productId: Long) {
        viewModelLaunch(::cartExceptionHandler) {
            cartRepository.cartItemResponse(productId).onSuccess { cartItem ->
                val current = productWithQuantities(productId, cartItem.quantity)
                _recommendProducts.value = current
            }.onSuccess {
                val current = productWithQuantities(productId, it.quantity)
                _recommendProducts.value = current
            }.onFail { error ->
                if (error is Fail.NotFound) {
                    val current = productWithQuantities(productId, Quantity())
                    _recommendProducts.value = current
                } else {
                    _error.setValue(error.toUiError())
                }
            }.onException {
                _error.setValue(CartError.UnKnown)
            }

        }
    }

    private fun productWithQuantities(
        productId: Long,
        quantity: Quantity,
    ): MutableList<ProductWithQuantity> {
        val changedRecommend =
            requireNotNull(_recommendProducts.value?.firstOrNull { it.product.id == productId })
        val current = _recommendProducts.value?.toMutableList() ?: mutableListOf()
        current[current.indexOf(changedRecommend)] =
            changedRecommend.copy(quantity = quantity)
        return current
    }

    private fun findCartIdByProductId(productId: Long): Long {
        return cart.value?.cartItems?.firstOrNull { it.productId == productId }?.id
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    private fun cartExceptionHandler(throwable: Throwable) {
        _error.setValue(CartError.LoadCart)
    }

    private fun recommendExceptionHandler(throwable: Throwable) {
        _error.setValue(CartError.LoadRecommend)
    }

    private fun updateCartExceptionHandler(throwable: Throwable) {
        _error.setValue(CartError.UpdateCart)
    }

    private fun CartWithProduct.toUiModel(isChecked: Boolean) =
        CartUiModel(
            this.id,
            this.product.id,
            this.product.name,
            this.product.price,
            this.quantity.value,
            this.product.imageUrl,
            isChecked,
        )

    private fun isAlreadyChecked(productId: Long): Boolean =
        _cart.value?.cartItems?.firstOrNull {
            it.productId == productId
        }?.isChecked ?: false || _recommendProducts.value?.any { it.product.id == productId && it.quantity.value > 0 } ?: false

    private inline fun <reified T : Any?> Result<T>.checkError(excute: (CartError) -> Unit) = apply {
        when (this) {
            is Result.Success -> {}
            is Fail.InvalidAuthorized -> excute(CartError.InvalidAuthorized)
            is Fail.Network -> excute(CartError.Network)
            is Fail.NotFound -> {
                when (T::class) {
                    Product::class -> excute(CartError.LoadRecommend)
                    CartWithProduct::class -> excute(CartError.LoadCart)
                    else -> excute(CartError.UnKnown)
                }
            }

            is Result.Exception -> {
                Log.d(this.javaClass.simpleName, "${this.e}")
                excute(CartError.UnKnown)
            }
        }
    }

    private inline fun <reified T : Any?> Fail<T>.toUiError() =
        when (this) {
            is Fail.InvalidAuthorized -> CartError.InvalidAuthorized
            is Fail.Network -> CartError.Network
            is Fail.NotFound -> {
                when (T::class) {
                    Product::class -> CartError.LoadRecommend
                    CartWithProduct::class -> CartError.LoadCart
                    else -> CartError.UnKnown
                }
            }
        }



    companion object {
        private const val DEFAULT_RECENT_PRODUCT_ID = 0L
        private const val DEFAULT_RECOMMEND_CATEGORY = "fashion"
        private const val DEFAULT_TOTAL_PRICE = 0
        private const val INITIAL_CART_COUNT = 1
    }
}

package woowacourse.shopping.ui.cart.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.ApiResponse
import woowacourse.shopping.data.repository.Error
import woowacourse.shopping.data.repository.onError
import woowacourse.shopping.data.repository.onException
import woowacourse.shopping.data.repository.onSuccess
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.cartitem.CartItemsUiState
import woowacourse.shopping.ui.cart.cartitem.CartUiModel
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiState
import woowacourse.shopping.ui.utils.AddCartClickListener
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(), CountButtonClickListener, AddCartClickListener {
    private val _recommendProducts: MutableLiveData<List<ProductWithQuantity>> = MutableLiveData()
    val recommendProducts: LiveData<List<ProductWithQuantity>> = _recommendProducts

    private val _error: MutableSingleLiveData<Error<Nothing>> = MutableSingleLiveData()
    val error: SingleLiveData<Error<Nothing>> get() = _error

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
        viewModelScope.launch {
            runCatching {
                _cart.value = currentCartState().copy(isLoading = true)
                cartRepository.getAllCartItemsWithProduct()
            }.onSuccess { carts ->
                _cart.value =
                    currentCartState().copy(
                        cartItems = carts.map { it.toUiModel(isAlreadyChecked(it.product.id)) },
                        isLoading = false,
                    )
            }
        }
    }

    fun loadRecommendProducts() {
        viewModelScope.launch {
            val recentProductId = recentProductRepository.findMostRecentProduct()?.productId ?: 0
            val category = productByIdOrNull(recentProductId)?.category ?: "fashion"
            productRepository.productsByCategory(category).onSuccess {
                _recommendProducts.value = it.map { ProductWithQuantity(product = it) }
                noRecommendProductState.value = false
            }.onError {
                noRecommendProductState.value = true
                _error.setValue(it)
            }.onException {
                noRecommendProductState.value = true
                Log.d(this.javaClass.simpleName, "${it.e}")
            }

        }
    }

    private suspend fun productByIdOrNull(productId: Long): Product? {
        val response = viewModelScope.async { productRepository.find(productId) }.await()
        return when (response) {
            is ApiResponse.Success -> response.data
            is Error -> null
            is ApiResponse.Exception -> {
                Log.d(this.javaClass.simpleName, "${response.e}")
                null
            }
        }
    }

    private fun totalPrice(): Int {
        val carts = _cart.value?.cartItems?.filter { it.isChecked }?.sumOf { it.totalPrice } ?: 0
        val recommends =
            _recommendProducts.value?.filter { it.quantity.value >= 1 }?.sumOf { it.totalPrice }
                ?: 0
        return carts + recommends
    }

    fun removeCartItem(productId: Long) {
        viewModelScope.launch {
            runCatching {
                cartRepository.deleteCartItem(findCartIdByProductId(productId))
            }.onSuccess {
                _cart.value =
                    CartItemsUiState(
                        cartRepository.getAllCartItemsWithProduct()
                            .map { it.toUiModel(findIsCheckedByProductId(it.product.id)) },
                        isLoading = false,
                    )
            }.onFailure {
                Log.d(this.javaClass.simpleName, "${it.message}")
            }
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
        viewModelScope.launch {
            runCatching {
                cartRepository.postCartItems(productId, 1)
            }.onSuccess {
                changeRecommendProductCount(productId)
                loadCartItems()
            }.onFailure {
                Log.d(this.javaClass.simpleName, "${it.message}")
            }
        }
    }

    override fun plusCount(productId: Long) {
        viewModelScope.launch {
            runCatching {
                val cartItem = cartRepository.getCartItem(productId)
                cartRepository.patchCartItem(
                    cartItem.id,
                    cartItem.quantity.value.inc(),
                )
            }.onSuccess {
                if (requireNotNull(isRecommendPage.value)) {
                    changeRecommendProductCount(productId)
                }
                loadCartItems()
            }.onFailure {
                Log.d(this.javaClass.simpleName, "${it.message}")
            }
        }
    }

    override fun minusCount(productId: Long) {
        viewModelScope.launch {
            runCatching {
                val cartItem = cartRepository.getCartItem(productId)
                if (cartItem.quantity.value > 0) {
                    cartRepository.patchCartItem(
                        cartItem.id,
                        cartItem.quantity.value.dec(),
                    )
                }
            }.onSuccess {
                if (requireNotNull(isRecommendPage.value)) {
                    changeRecommendProductCount(productId)
                }
                loadCartItems()
            }.onFailure {
                Log.d(this.javaClass.simpleName, "${it.message}")
            }
        }
    }

    private fun changeRecommendProductCount(productId: Long) {
        viewModelScope.launch {
            runCatching {
                cartRepository.getCartItem(productId)
            }.onSuccess {
                val current = productWithQuantities(productId, it.quantity)
                _recommendProducts.value = current
            }.onFailure {
                val current = productWithQuantities(productId, Quantity())
                _recommendProducts.value = current
                Log.d(this.javaClass.simpleName, "${it.message}")
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
}

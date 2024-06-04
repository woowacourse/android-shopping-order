package woowacourse.shopping.ui.cart.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cart.CartWithProduct
import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.model.ProductWithQuantity
import woowacourse.shopping.model.Quantity
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.item.CartItemsUiState
import woowacourse.shopping.ui.cart.item.CartUiModel
import woowacourse.shopping.ui.products.ProductWithQuantityUiState
import woowacourse.shopping.ui.utils.AddCartClickListener
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val orderRepository: OrderRepository
) : ViewModel(), CountButtonClickListener, AddCartClickListener {

    private val _products: MutableLiveData<List<ProductWithQuantity>> = MutableLiveData()
    val products: LiveData<List<ProductWithQuantity>> = _products

    val cartOfRecommendProductCount: LiveData<Int> =
        _products.map {
            it.sumOf { it.quantity.value }
        }

    private val _cart: MutableLiveData<CartItemsUiState> = MutableLiveData()
    val cart: LiveData<CartItemsUiState> = _cart

    val totalPrice: MediatorLiveData<Int> =
        MediatorLiveData<Int>().apply {
            addSource(_cart) { value = totalPrice() }
            addSource(_products) { value = totalPrice() }
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

    private val _isOrderSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
    val isOrderSuccess: SingleLiveData<Boolean> get() = _isOrderSuccess

    private fun currentCartState(): CartItemsUiState =
        _cart.value ?: CartItemsUiState(emptyList(), true)

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        runCatching {
            _cart.value = currentCartState().copy(isLoading = true)
            cartRepository.getAllCartItemsWithProduct()
        }.onSuccess { carts ->
            _cart.value = currentCartState().copy(
                cartItems = carts.map { it.toUiModel(isAlreadyChecked(it.product.id)) },
                isLoading = false
            )
        }
    }

    fun loadRecommendProducts() {
        runCatching {
            val recentProductId =
                requireNotNull(recentProductRepository.findMostRecentProduct()).productId
            val category = productRepository.find(recentProductId).category
            productRepository.productsByCategory(category)
                .filterNot { product -> requireNotNull(_cart.value).cartItems.any { it.productId == product.id } }
        }.onSuccess {
            _products.value =
                it.map { ProductWithQuantity(product = it) }.subList(0, minOf(it.size, 10))
            noRecommendProductState.value = false
        }.onFailure {
            noRecommendProductState.value = true
        }
    }

    fun totalPrice(): Int {
        val carts = _cart.value?.cartItems?.filter { it.isChecked }?.sumOf { it.totalPrice } ?: 0
        val recommends =
            _products.value?.filter { it.quantity.value >= 1 }?.sumOf { it.totalPrice } ?: 0
        return carts + recommends
    }

    fun removeCartItem(productId: Long) {
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
            Log.d("테스트", "$it")
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
        runCatching {
            cartRepository.postCartItems(productId, 1)
        }.onSuccess {
            changeRecommendProductCount(productId)
            loadCartItems()
        }
    }

    override fun plusCount(productId: Long) {
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
        }

    }

    override fun minusCount(productId: Long) {
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
        }

    }

    private fun changeRecommendProductCount(productId: Long) {
        runCatching {
            cartRepository.getCartItem(productId)
        }.onSuccess {
            val current = productWithQuantities(productId, it.quantity)
            _products.value = current
        }.onFailure {
            val current = productWithQuantities(productId, Quantity())
            _products.value = current
        }
    }

    private fun productWithQuantities(
        productId: Long,
        quantity: Quantity,
    ): MutableList<ProductWithQuantity> {
        val changedRecommend =
            requireNotNull(_products.value?.firstOrNull { it.product.id == productId })
        val current = _products.value?.toMutableList() ?: mutableListOf()
        current[current.indexOf(changedRecommend)] =
            changedRecommend.copy(quantity = quantity)
        return current
    }

    fun order() {
        runCatching {
            val cartIds: List<Long> =
                _cart.value?.cartItems?.filter { it.isChecked }?.map { it.id } ?: emptyList()
            orderRepository.order(cartIds)
        }.onSuccess {
            _isOrderSuccess.setValue(true)
        }.onFailure {
            _isOrderSuccess.setValue(false)
        }
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
            this.quantity,
            this.product.imageUrl,
            isChecked,
        )

    private fun isAlreadyChecked(productId: Long): Boolean =
        (_cart.value?.cartItems?.firstOrNull { it.productId == productId }?.isChecked ?: false) || _products.value?.any { it.product.id == productId && it.quantity.value > 0 } ?: false
}

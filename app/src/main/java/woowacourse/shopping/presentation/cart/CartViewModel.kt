package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.domain.datasource.DataResponse
import com.example.domain.datasource.map
import com.example.domain.datasource.onFailure
import com.example.domain.datasource.onSuccess
import com.example.domain.model.Quantity
import com.example.domain.repository.CartRepository
import com.example.domain.repository.RecentProductRepository
import kotlinx.coroutines.launch
import woowacourse.shopping.common.Event
import woowacourse.shopping.common.emit
import woowacourse.shopping.presentation.cart.model.CartUiModel
import woowacourse.shopping.presentation.cart.model.CouponUiModels
import woowacourse.shopping.presentation.cart.model.toCartItem
import woowacourse.shopping.presentation.cart.model.toCartUiModel
import woowacourse.shopping.presentation.cart.model.toCartUiModels
import woowacourse.shopping.presentation.products.ProductCountActionHandler
import woowacourse.shopping.presentation.products.uimodel.ProductUiModel

class CartViewModel(
    private val recommendRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CartActionHandler, ProductCountActionHandler {
    private val _cartUiState = MutableLiveData<CartUiState>()
    val cartUiState: LiveData<CartUiState> get() = _cartUiState

    private val _changedCartEvent = MutableLiveData<Event<Unit>>()
    val changedCartEvent: LiveData<Event<Unit>> get() = _changedCartEvent

    val cartItemSelectedCount: LiveData<Int>
        get() = cartUiState.map { it.cartUiModels.count { cartUiModel -> cartUiModel.isSelected } }

    val cartItemAllSelected: LiveData<Boolean>
        get() =
            cartUiState.map { it.cartUiModels.all { cartUiModel -> cartUiModel.isSelected } }

    val totalPrice: LiveData<Int>
        get() =
            cartUiState.map { cartUiState ->
                cartUiState.cartUiModels.filter {
                    it.isSelected
                }.sumOf { it.product.price * it.quantity.count }
            }

    val orderButtonEnabled: LiveData<Boolean>
        get() =
            cartItemSelectedCount.map {
                it != 0
            }

    private val _recommendProductUiModels = MutableLiveData<List<ProductUiModel>>()
    val recommendProductUiModels: LiveData<List<ProductUiModel>> get() = _recommendProductUiModels

    private val _isSuccessCreateOrder = MutableLiveData<Event<Boolean>>()
    val isSuccessCreateOrder: LiveData<Event<Boolean>> get() = _isSuccessCreateOrder

    private val _navigateAction = MutableLiveData<Event<CartNavigateAction>>()
    val navigateAction: LiveData<Event<CartNavigateAction>> get() = _navigateAction

    private val _checkboxVisibility = MutableLiveData<Boolean>(true)
    val checkboxVisibility: LiveData<Boolean> get() = _checkboxVisibility

    private val _couponUiModels: MutableLiveData<CouponUiModels> = MutableLiveData()
    val couponUiModels: MutableLiveData<CouponUiModels> = MutableLiveData()

    init {
        loadAllCartItems()
    }

    private fun loadAllCartItems() {
        viewModelScope.launch {
            val result = cartRepository.findAll()
            result.onSuccess { cartItems ->
                val oldCartItems = cartUiState.value?.cartUiModels ?: cartItems.toCartUiModels()
                val newCartItems: MutableList<CartUiModel> = mutableListOf()
                cartItems.forEach { cartItem ->
                    val oldItem = oldCartItems.find { it.toCartItem().id == cartItem.id }
                    if (oldItem != null) {
                        newCartItems.add(oldItem.copy(quantity = cartItem.quantity))
                    } else {
                        newCartItems.add(cartItem.toCartUiModel())
                    }
                }
                val newUiState =
                    CartUiState(
                        newCartItems,
                        isLoading = false,
                        isFailure = false,
                        isSuccess = true,
                    )
                _cartUiState.value = newUiState
            }
        }
    }

    override fun deleteCartItem(productId: Int) {
        _changedCartEvent.value = Event(Unit)
        val cartUiModel = findCartUiModelByProductId(productId) ?: return
        viewModelScope.launch {
            cartRepository.deleteCartItem(cartUiModel.cartItemId)
            reloadCartUiState()
        }
    }

    private fun reloadCartUiState() {
        loadAllCartItems()
    }

    override fun increaseQuantity(productId: Int) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(productId)
            reloadCartUiState()
        }
    }

    override fun decreaseQuantity(productId: Int) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(productId)
            reloadCartUiState()
        }
    }

    override fun selectCartItem(
        productId: Int,
        isSelected: Boolean,
    ) {
        val oldCartUiModels = cartUiState.value?.cartUiModels ?: emptyList()
        val oldCartUiModel = findCartUiModelByProductId(productId) ?: return
        if (oldCartUiModel.isSelected == isSelected) return

        val newCartUiModels = oldCartUiModels.upsert(oldCartUiModel.copy(isSelected = isSelected))
        val newCartUiState =
            CartUiState(newCartUiModels, isLoading = false, isFailure = false, isSuccess = true)
        _cartUiState.value = newCartUiState
    }

    private fun List<CartUiModel>.upsert(cartUiModel: CartUiModel): List<CartUiModel> {
        val list = this.toMutableList()
        if (this.none { it.cartItemId == cartUiModel.cartItemId }) {
            list += cartUiModel
        } else {
            this.forEachIndexed { index, listItem ->
                if (listItem.cartItemId == cartUiModel.cartItemId) {
                    list[index] = cartUiModel
                }
            }
        }
        return list.toList()
    }

    override fun selectAllCartItem(isChecked: Boolean) {
        if (cartItemAllSelected.value == true && isChecked) return
        cartUiState.value?.cartUiModels?.forEach {
            selectCartItem(it.product.id, isSelected = isChecked)
        }
    }

    override fun navigateCartRecommend() {
        _navigateAction.emit(CartNavigateAction.RecommendNavigateAction)
        _checkboxVisibility.value = false
    }

    override fun navigatePurchase() {
        _navigateAction.emit(CartNavigateAction.PurchaseProductNavigateAction)
    }

    fun loadRecommendProductUiModels() {
        val cartItems = cartUiState.value?.cartUiModels?.map { it.toCartItem() } ?: return
        val recommendProducts = recommendRepository.getRecommendProducts(cartItems = cartItems)
        _recommendProductUiModels.value =
            recommendProducts.map { product ->
                val quantity = cartItems.find { it.product == product }?.quantity ?: Quantity(0)
                ProductUiModel(product, quantity)
            }
    }

    private fun findCartUiModelByProductId(productId: Int): CartUiModel? {
        return cartUiState.value?.cartUiModels?.find { it.product.id == productId }
    }

    override fun onClickProduct(productId: Int) {
        _navigateAction.emit(CartNavigateAction.ProductDetailNavigateAction(productId))
    }

    override fun onClickPlusQuantityButton(productId: Int) {
        increaseRecommendQuantity(productId)
    }

    override fun onClickMinusQuantityButton(productId: Int) {
        decreaseRecommendQuantity(productId)
    }

    private fun increaseRecommendQuantity(productId: Int) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(productId)
            updateRecommendQuantity(productId)
        }
    }

    private fun decreaseRecommendQuantity(productId: Int) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(productId)
            updateRecommendQuantity(productId)
        }
    }

    private fun updateRecommendQuantity(productId: Int) {
        viewModelScope.launch {
            val result = cartRepository.find(productId).map { it.quantity }
            val oldUiModels =
                recommendProductUiModels.value?.toMutableList() ?: mutableListOf()
            val idx = oldUiModels.indexOfFirst { it.product.id == productId }
            result.onSuccess { quantity ->
                oldUiModels[idx] = oldUiModels[idx].copy(quantity = quantity)
                _recommendProductUiModels.value = oldUiModels
            }.onFailure { code, error ->
                if (code == DataResponse.NULL_BODY_ERROR_CODE) {
                    oldUiModels[idx] = oldUiModels[idx].copy(quantity = Quantity(0))
                    _recommendProductUiModels.value = oldUiModels
                }
            }
        }
    }
}

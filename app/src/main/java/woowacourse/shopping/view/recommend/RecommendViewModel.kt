package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class RecommendViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private val _event: MutableSingleLiveData<RecommendEvent> = MutableSingleLiveData()
    val event: SingleLiveData<RecommendEvent> get() = _event

    private val _recommendedProducts: MutableLiveData<List<RecommendProduct>> = MutableLiveData()
    val recommendedProducts: LiveData<List<RecommendProduct>> get() = _recommendedProducts

    private var addedItems: Map<Long, Long> = mapOf()

    val selectedCartItems: MutableLiveData<List<CartItem>> = MutableLiveData()

    val totalPrice: LiveData<Int> =
        selectedCartItems.map { cart: List<CartItem> ->
            cart.sumOf(CartItem::price)
        }

    val cartSize: LiveData<Int> =
        selectedCartItems.map { cart: List<CartItem> ->
            cart.sumOf(CartItem::quantity)
        }

    init {
        loadRecommendedProducts()
    }

    fun addAlreadyAddedCartItems(alreadyAddedCartItems: List<CartItem>) {
        selectedCartItems.value = alreadyAddedCartItems
    }

    fun plusCartItemQuantity(recommend: RecommendProduct) {
        viewModelScope.launch {
            if (recommend.quantity == 0) {
                cartRepository
                    .addCartItem(recommend.productId, 1)
                    .onSuccess { cartItemId: Long? ->
                        cartItemId ?: return@onSuccess

                        addedItems = addedItems.plus(recommend.productId to cartItemId)
                        recommend.update(1)
                        selectedCartItems.postValue(
                            selectedCartItems.value?.plus(
                                CartItem(cartItemId, recommend.product, 1),
                            ),
                        )
                    }.onFailure { _event.postValue(RecommendEvent.MODIFY_CART_FAILURE) }
            } else {
                cartRepository
                    .updateCartItemQuantity(
                        addedItems[recommend.productId] ?: return@launch,
                        recommend.quantity + 1,
                    ).onSuccess {
                        recommend.update(recommend.quantity + 1)
                        selectedCartItems.postValue(
                            selectedCartItems.value
                                ?.minus(
                                    CartItem(
                                        addedItems[recommend.productId] ?: return@onSuccess,
                                        recommend.product,
                                        recommend.quantity,
                                    ),
                                )?.plus(
                                    CartItem(
                                        addedItems[recommend.productId] ?: return@onSuccess,
                                        recommend.product,
                                        recommend.quantity + 1,
                                    ),
                                ),
                        )
                    }.onFailure {
                        _event.postValue(RecommendEvent.MODIFY_CART_FAILURE)
                    }
            }
        }
    }

    fun minusCartItemQuantity(recommend: RecommendProduct) {
        viewModelScope.launch {
            if (recommend.quantity == 1) {
                cartRepository
                    .remove(
                        addedItems[recommend.productId] ?: return@launch,
                    ).onSuccess {
                        recommend.update(0)
                        selectedCartItems.postValue(
                            selectedCartItems.value?.minus(
                                CartItem(
                                    addedItems[recommend.productId] ?: return@onSuccess,
                                    recommend.product,
                                    1,
                                ),
                            ),
                        )
                        addedItems = addedItems.minus(recommend.productId)
                    }.onFailure {
                        _event.postValue(RecommendEvent.MODIFY_CART_FAILURE)
                    }
            } else {
                cartRepository
                    .updateCartItemQuantity(
                        addedItems[recommend.productId] ?: return@launch,
                        recommend.quantity - 1,
                    ).onSuccess {
                        recommend.update(recommend.quantity - 1)
                        selectedCartItems.postValue(
                            selectedCartItems.value
                                ?.minus(
                                    CartItem(
                                        addedItems[recommend.productId] ?: return@onSuccess,
                                        recommend.product,
                                        recommend.quantity,
                                    ),
                                )?.plus(
                                    CartItem(
                                        addedItems[recommend.productId] ?: return@onSuccess,
                                        recommend.product,
                                        recommend.quantity - 1,
                                    ),
                                ),
                        )
                    }.onFailure {
                        _event.postValue(RecommendEvent.MODIFY_CART_FAILURE)
                    }
            }
        }
    }

    private fun loadRecommendedProducts() {
        viewModelScope.launch {
            productsRepository
                .loadRecommendedProducts(RECOMMEND_COUNT)
                .onSuccess { recommendedProducts: List<Product> ->
                    _recommendedProducts.postValue(
                        recommendedProducts.map { product -> RecommendProduct(product) },
                    )
                }.onFailure {
                    _event.postValue(RecommendEvent.LOAD_RECOMMENDED_PRODUCTS_FAILURE)
                }
        }
    }

    private fun RecommendProduct.update(newQuantity: Int) {
        val index: Int? = recommendedProducts.value?.indexOf(this).takeIf { it != -1 }
        val newProducts: MutableList<RecommendProduct>? =
            recommendedProducts.value?.toMutableList()

        if (index != null && newProducts != null) {
            newProducts[index] =
                newProducts[index].copy(quantity = newQuantity)
            _recommendedProducts.postValue(newProducts)
        }
    }

    companion object {
        private const val RECOMMEND_COUNT = 10
    }
}

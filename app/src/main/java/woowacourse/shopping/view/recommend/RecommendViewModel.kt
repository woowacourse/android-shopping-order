package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.ShoppingCartRepositoryImpl
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_RECOMMEND_ITEM_SIZE
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_OFFSET
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_SIZE
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class RecommendViewModel(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyRepository: RecentlyProductRepository,
) : ViewModel(), OnClickCartItemCounter {
    private var _checkedShoppingCart = ShoppingCart()
    val checkedShoppingCart: ShoppingCart get() = _checkedShoppingCart

    private val _products: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val products: LiveData<List<Product>> get() = _products

    private val _errorEvent: MutableSingleLiveData<RecommendEvent.ErrorEvent> =
        MutableSingleLiveData()
    val errorEvent: SingleLiveData<RecommendEvent.ErrorEvent> get() = _errorEvent

    private val _recommendEvent: MutableSingleLiveData<RecommendEvent.SuccessEvent> =
        MutableSingleLiveData()
    val recommendEvent: SingleLiveData<RecommendEvent.SuccessEvent> get() = _recommendEvent

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalCount: MutableLiveData<Int> = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    fun loadRecentlyProductToRecommend() {
        viewModelScope.launch {
            recentlyRepository.getMostRecentlyProduct()
                .onSuccess { recentlyProduct ->
                    loadRecommendData(recentlyProduct)
                }.onFailure {
                    _errorEvent.setValue(RecommendEvent.ErrorEvent.NotKnownError)
                }
        }
    }

    private fun loadRecommendData(recentlyProduct: RecentlyProduct) {
        viewModelScope.launch {
            runCatching {
                val myCartItemsResult =
                    shoppingCartRepository.loadPagingCartItems(
                        LOAD_SHOPPING_ITEM_OFFSET,
                        LOAD_SHOPPING_ITEM_SIZE,
                    ).getOrThrow()
                val loadDataResult =
                    productRepository.loadCategoryProducts(
                        size = LOAD_SHOPPING_ITEM_SIZE + LOAD_RECOMMEND_ITEM_SIZE,
                        category = recentlyProduct.category,
                    ).getOrThrow()
                Pair(myCartItemsResult, loadDataResult)
            }.onSuccess { (myCartItems, loadData) ->
                val recommendData = getFilteredRandomProducts(myCartItems, loadData)
                _products.value = recommendData
                updateCheckItemData()
            }.onFailure {
                _errorEvent.setValue(RecommendEvent.ErrorEvent.NotKnownError)
            }
        }
    }

    private fun deleteCartItem(product: Product) {
        viewModelScope.launch {
            runCatching {
                product.updateItemSelector(false)
                _checkedShoppingCart.deleteProductFromProductId(product.id)
            }.onSuccess {
                _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product))
                updateCheckItemData()
            }.onFailure {
                _errorEvent.setValue(RecommendEvent.UpdateProductEvent.Fail)
            }
        }
    }

    private fun getFilteredRandomProducts(
        myCartItems: List<CartItem>,
        loadData: List<Product>,
    ): List<Product> {
        val cartProductIds = myCartItems.map { it.product.id }.toSet()
        val filteredProducts = loadData.filter { it.id !in cartProductIds }
        return filteredProducts.shuffled().take(LOAD_RECOMMEND_ITEM_SIZE)
    }

    fun saveCheckedShoppingCarts(shoppingCart: ShoppingCart) {
        _checkedShoppingCart = shoppingCart
        updateCheckItemData()
    }

    private fun updateCheckItemData() {
        _totalPrice.value = _checkedShoppingCart.cartItems.value?.sumOf {
            it.product.cartItemCounter.itemCount * it.product.price
        } ?: ShoppingCartRepositoryImpl.DEFAULT_ITEM_SIZE
        _totalCount.value = _checkedShoppingCart.cartItems.value?.count {
            it.cartItemSelector.isSelected
        } ?: ShoppingCartRepositoryImpl.DEFAULT_ITEM_SIZE
    }

    override fun clickIncrease(product: Product) {
        viewModelScope.launch {
            shoppingCartRepository.increaseCartItem(product)
                .onSuccess {
                    product.updateCartItemCount(product.cartItemCounter.itemCount)
                    _recommendEvent.setValue(
                        RecommendEvent.UpdateProductEvent.Success(
                            product,
                        ),
                    )
                    _checkedShoppingCart.addProduct(CartItem(product = product))
                    updateCheckItemData()
                }.onFailure {
                    // Todo: handle exception
                }
        }
    }

    override fun clickDecrease(product: Product) {
        viewModelScope.launch {
            product.cartItemCounter.decrease()
            shoppingCartRepository.decreaseCartItem(product)
                .onSuccess {
                    if (product.cartItemCounter.itemCount == 0) {
                        deleteCartItem(product)
                        updateCheckItemData()
                    } else {
                        _recommendEvent.setValue(
                            RecommendEvent.UpdateProductEvent.Success(
                                product,
                            ),
                        )
                        updateCheckItemData()
                    }
                }.onFailure {
                    // Todo: handle exception
                }
        }
    }
}

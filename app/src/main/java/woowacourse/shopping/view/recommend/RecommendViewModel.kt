package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.model.CartItemEntity
import woowacourse.shopping.data.repository.ShoppingCartRepositoryImpl
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_RECOMMEND_ITEM_SIZE
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_OFFSET
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_SIZE
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.UpdateCartItemResult
import woowacourse.shopping.domain.model.cart.UpdateCartItemType
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.BaseViewModel
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class RecommendViewModel(
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyRepository: RecentlyProductRepository,
) : BaseViewModel(), OnClickCartItemCounter, OnClickRecommend {
    private var checkedShoppingCart = ShoppingCart()

    private val _products: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val products: LiveData<List<Product>> get() = _products

    private val _recommendEvent: MutableSingleLiveData<RecommendEvent> =
        MutableSingleLiveData()
    val recommendEvent: SingleLiveData<RecommendEvent> get() = _recommendEvent

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice
    private val _totalCount: MutableLiveData<Int> = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    fun loadRecommendData() =
        viewModelScope.launch {
            recentlyRepository.getMostRecentlyProduct()
                .onSuccess { recentlyProduct ->
                    loadCategoryProducts(recentlyProduct.category)
                }
                .onFailure {
                    handleException(ErrorEvent.LoadDataEvent())
                }
        }

    private fun loadMyCartItems(categoryProducts: List<Product>) =
        viewModelScope.launch {
            shoppingCartRepository.loadPagingCartItems(
                LOAD_SHOPPING_ITEM_OFFSET,
                LOAD_SHOPPING_ITEM_SIZE,
            )
                .onSuccess { myCartItems ->
                    val recommendData =
                        getFilteredRandomProducts(
                            myCartItems = myCartItems,
                            loadData = categoryProducts,
                        )
                    _products.value = recommendData
                    updateCheckItemData()
                }
                .onFailure {
                    handleException(ErrorEvent.LoadDataEvent())
                }
        }

    private fun loadCategoryProducts(category: String) =
        viewModelScope.launch {
            productRepository.loadCategoryProducts(
                size = LOAD_SHOPPING_ITEM_SIZE + LOAD_RECOMMEND_ITEM_SIZE,
                category = category,
            )
                .onSuccess { categoryProducts ->
                    loadMyCartItems(categoryProducts)
                }
                .onFailure {
                    handleException(ErrorEvent.LoadDataEvent())
                }
        }

    private fun updateCarItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ) = viewModelScope.launch {
        shoppingCartRepository.updateCartItem(
            product,
            updateCartItemType,
        )
            .onSuccess { updateCartItemResult ->
                when (updateCartItemResult) {
                    UpdateCartItemResult.ADD -> addCartItem(product)
                    is UpdateCartItemResult.DELETE -> deleteCartItem(product)
                    is UpdateCartItemResult.UPDATED ->
                        updateCartItem(
                            product = product,
                            itemCount = updateCartItemResult.cartItemResult.counter.itemCount,
                        )
                }
            }
            .onFailure {
                handleException(ErrorEvent.UpdateCartEvent())
            }
    }

    private fun updateCartItem(
        product: Product,
        itemCount: Int,
    ) {
        product.updateCartItemCount(itemCount)
        _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product))
        updateCheckItemData()
    }

    private fun addCartItem(product: Product) {
        runCatching {
            product.updateCartItemCount(CartItemEntity.DEFAULT_CART_ITEM_COUNT)
        }
            .onSuccess {
                product.updateItemSelector(true)
                _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product))
                checkedShoppingCart.addProduct(CartItem(product = product))
                updateCheckItemData()
            }
            .onFailure {
                handleException(ErrorEvent.AddCartEvent())
            }
    }

    private fun deleteCartItem(product: Product) {
        runCatching {
            checkedShoppingCart.deleteProductFromProductId(product.id)
        }
            .onSuccess {
                product.updateItemSelector(false)
                _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product))
            }
            .onFailure {
                handleException(ErrorEvent.DeleteCartEvent())
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
        checkedShoppingCart = shoppingCart
        updateCheckItemData()
    }

    private fun updateCheckItemData() {
        _totalPrice.value = checkedShoppingCart.cartItems.value?.sumOf {
            it.product.cartItemCounter.itemCount * it.product.price
        } ?: ShoppingCartRepositoryImpl.DEFAULT_ITEM_SIZE
        _totalCount.value = checkedShoppingCart.cartItems.value?.count {
            it.cartItemSelector.isSelected
        } ?: ShoppingCartRepositoryImpl.DEFAULT_ITEM_SIZE
    }

    override fun clickIncrease(product: Product) {
        updateCarItem(product, UpdateCartItemType.INCREASE)
    }

    override fun clickDecrease(product: Product) {
        updateCarItem(product, UpdateCartItemType.DECREASE)
    }

    override fun clickOrder() {
        _recommendEvent.setValue(RecommendEvent.OrderRecommends.Success(checkedShoppingCart))
    }
}

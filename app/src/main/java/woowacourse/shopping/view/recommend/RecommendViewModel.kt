package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.model.CartItemEntity
import woowacourse.shopping.data.repository.ShoppingCartRepositoryImpl
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.products.ProductListEvent

class RecommendViewModel(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyRepository: RecentlyProductRepository,
) : ViewModel() {
    private val checkedShoppingCart = ShoppingCart()
    private val _products: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val products: LiveData<List<Product>> get() = _products

    private val _errorEvent: MutableSingleLiveData<RecommendEvent.ErrorEvent> =
        MutableSingleLiveData()
    val errorEvent: SingleLiveData<RecommendEvent.ErrorEvent> get() = _errorEvent
    private val _recommendEvent: MutableSingleLiveData<RecommendEvent.SuccessEvent> =
        MutableSingleLiveData()
    val recommendEvent: SingleLiveData<RecommendEvent.SuccessEvent> get() = _recommendEvent

    val totalPrice: Int
        get() = checkedShoppingCart.cartItems.value?.sumOf {
            it.product.cartItemCounter.itemCount * it.product.price
        } ?: ShoppingCartRepositoryImpl.DEFAULT_ITEM_SIZE
    val totalCount: Int
        get() = checkedShoppingCart.cartItems.value?.count {
            it.cartItemSelector.isSelected
        } ?: ShoppingCartRepositoryImpl.DEFAULT_ITEM_SIZE

    private fun loadRecentlyProduct(): RecentlyProduct {
        return recentlyRepository.getMostRecentlyProduct()
    }

    fun loadRecommendData() {
        try {
            val recentlyProduct = loadRecentlyProduct()
            val loadData = productRepository.loadCategoryProducts(recentlyProduct.category)
            _products.value = loadData
        } catch (e: Exception) {
            _errorEvent.setValue(RecommendEvent.ErrorEvent.NotKnownError)
        }
    }

    fun increaseShoppingCart(product: Product) {
        updateCarItem(product, UpdateCartItemType.INCREASE)
    }

    fun decreaseShoppingCart(product: Product) {
        updateCarItem(product, UpdateCartItemType.DECREASE)
    }

    fun orderItems(){
        val ids = products.value?.map {it.id.toInt()}
        if (!ids.isNullOrEmpty()){
            orderRepository.orderShoppingCart(ids)
        }
    }

    private fun updateCarItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ) {
        try {
            val updateCartItemResult =
                shoppingCartRepository.updateCartItem(
                    product,
                    updateCartItemType,
                )
            when (updateCartItemResult) {
                UpdateCartItemResult.ADD -> addCartItem(product)
                is UpdateCartItemResult.DELETE -> deleteCartItem(product)
                is UpdateCartItemResult.UPDATED -> {
                    product.updateCartItemCount(updateCartItemResult.cartItemResult.counter.itemCount)
                    _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product.id))
                }
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchDataException ->
                    _errorEvent.setValue(RecommendEvent.UpdateProductEvent.Fail)

                else -> _errorEvent.setValue(RecommendEvent.ErrorEvent.NotKnownError)
            }
        }

    }

    private fun addCartItem(product: Product) {
        product.updateCartItemCount(CartItemEntity.DEFAULT_CART_ITEM_COUNT)
        product.updateItemSelector(true)
        _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product.id))
    }

    private fun deleteCartItem(product: Product) {
        try {
            product.updateItemSelector(false)
            _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product.id))
        } catch (e: Exception) {
            when (e) {
                is NoSuchDataException ->
                    _errorEvent.setValue(RecommendEvent.UpdateProductEvent.Fail)

                else -> _errorEvent.setValue(RecommendEvent.ErrorEvent.NotKnownError)
            }
        }
    }

}

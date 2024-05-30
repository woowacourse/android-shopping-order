package woowacourse.shopping.view.recommend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.model.CartItemEntity
import woowacourse.shopping.data.repository.ShoppingCartRepositoryImpl
import woowacourse.shopping.data.repository.real.RealShoppingCartRepositoryImpl.Companion.LOAD_RECOMMEND_ITEM_SIZE
import woowacourse.shopping.data.repository.real.RealShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_OFFSET
import woowacourse.shopping.data.repository.real.RealShoppingCartRepositoryImpl.Companion.LOAD_SHOPPING_ITEM_SIZE
import woowacourse.shopping.domain.model.CartItem
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

class RecommendViewModel(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyRepository: RecentlyProductRepository,
) : ViewModel() {
    private var checkedShoppingCart = ShoppingCart()

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

    private fun loadRecentlyProduct(): RecentlyProduct {
        return recentlyRepository.getMostRecentlyProduct()
    }

    fun loadRecommendData() {
        try {
            val recentlyProduct = loadRecentlyProduct()
            val myCartItems = shoppingCartRepository.loadPagingCartItems(
                LOAD_SHOPPING_ITEM_OFFSET,
                LOAD_SHOPPING_ITEM_SIZE,
            )

            val loadData = productRepository.loadCategoryProducts(
                size = LOAD_SHOPPING_ITEM_SIZE + LOAD_RECOMMEND_ITEM_SIZE,
                category = recentlyProduct.category,
            )

            val recommendData = getFilteredRandomProducts(
                myCartItems = myCartItems,
                loadData = loadData,
            )
            _products.value = recommendData
            updateCheckItemData()
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


    fun orderItems() {
        val ids = products.value?.map { it.id.toInt() }
        try {
            orderRepository.orderShoppingCart(ids ?: throw NoSuchDataException())
            _recommendEvent.setValue(RecommendEvent.OrderRecommends.Success)
        } catch (e: Exception) {
            _errorEvent.setValue(RecommendEvent.OrderRecommends.Fail)
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
                    _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product))
                    updateCheckItemData()
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
        try {
            product.updateCartItemCount(CartItemEntity.DEFAULT_CART_ITEM_COUNT)
            product.updateItemSelector(true)
            _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product))
            checkedShoppingCart.addProduct(CartItem(product = product))
            updateCheckItemData()
        } catch (e: Exception){
            when (e) {
                is NoSuchDataException ->
                    _errorEvent.setValue(RecommendEvent.UpdateProductEvent.Fail)

                else -> _errorEvent.setValue(RecommendEvent.ErrorEvent.NotKnownError)
            }
        }
    }

    private fun deleteCartItem(product: Product) {
        try {
            product.updateItemSelector(false)
            _recommendEvent.setValue(RecommendEvent.UpdateProductEvent.Success(product))
            checkedShoppingCart.deleteProductFromProductId(product.id)
            updateCheckItemData()
        } catch (e: Exception) {
            when (e) {
                is NoSuchDataException ->
                    _errorEvent.setValue(RecommendEvent.UpdateProductEvent.Fail)

                else -> _errorEvent.setValue(RecommendEvent.ErrorEvent.NotKnownError)
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
}

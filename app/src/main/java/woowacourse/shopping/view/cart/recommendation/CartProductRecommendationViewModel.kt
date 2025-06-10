package woowacourse.shopping.view.cart.recommendation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.recommendation.adapter.ProductItem
import woowacourse.shopping.view.util.Error
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class CartProductRecommendationViewModel(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    CartProductRecommendationEventHandler {
    private val cartProducts: MutableSet<CartProduct> = mutableSetOf()

    private val _recommendedProducts = MutableLiveData<List<ProductItem>>()
    val recommendedProducts: LiveData<List<ProductItem>> get() = _recommendedProducts

    private val _selectedProducts = MutableLiveData<List<CartProduct>>()
    val selectedProducts: LiveData<List<CartProduct>> get() = _selectedProducts

    private val _totalPrice = MediatorLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalCount = MediatorLiveData<Int>()
    val totalCount: LiveData<Int> get() = _totalCount

    private val _startOrder = MutableLiveData<List<CartProduct>>()
    val startOrder: LiveData<List<CartProduct>> get() = _startOrder

    private val _selectedProduct = MutableSingleLiveData<Product>()
    val selectedProduct: SingleLiveData<Product> get() = _selectedProduct

    private val _errorEvent = MutableSingleLiveData<Error>()
    val errorEvent: SingleLiveData<Error> get() = _errorEvent

    init {
        viewModelScope.launch {
            val result = cartProductRepository.getPagedProducts()

            result
                .onSuccess {
                    cartProducts.addAll(it.items)
                    loadRecommendedProducts()
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToLoadProduct)
                }
        }
    }

    fun initShoppingCartInfo(selectedCartProducts: List<CartProduct>) {
        _selectedProducts.value = _selectedProducts.value.orEmpty().plus(selectedCartProducts)
        _totalPrice.removeSource(_selectedProducts)
        _totalCount.removeSource(_selectedProducts)

        _totalPrice.addSource(_selectedProducts) { list ->
            _totalPrice.value =
                list.sumOf { it.product.price * it.quantity }
        }
        _totalCount.addSource(_selectedProducts) { list ->
            _totalCount.value =
                list.sumOf { it.quantity }
        }
    }

    private fun loadRecommendedProducts() {
        viewModelScope.launch {
            val deferredRecentProductResult = async { recentProductRepository.getLastViewedProduct() }
            val deferredPagedProductResult = async { productRepository.getPagedProducts() }

            deferredRecentProductResult.await()
                .onSuccess { recentProduct ->
                    recentProduct ?: return@launch

                    deferredPagedProductResult.await()
                        .onSuccess { products ->
                            _recommendedProducts.value =
                                getRecommendationProducts(products, recentProduct)
                        }.onFailure {
                            Log.e("error", it.message.toString())
                            _errorEvent.setValue(Error.FailToLoadProduct)
                        }
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToLoadProduct)
                }
        }
    }

    private fun getRecommendationProducts(
        products: PagedResult<Product>,
        recentProduct: RecentProduct,
    ): List<ProductItem> {
        val cartProductIds =
            cartProducts.map { it.product.id }.toSet()
        return products.items
            .asSequence()
            .filter { it.category == recentProduct.product.category }
            .filter { it.id !in cartProductIds }
            .take(RECOMMEND_SIZE)
            .map { ProductItem(it) }
            .toList()
    }

    override fun onProductClick(item: Product) {
        _selectedProduct.setValue(item)
    }

    override fun onAddClick(item: Product) {
        viewModelScope.launch {
            val result = cartProductRepository.insert(item.id, QUANTITY_STEP)

            result
                .onSuccess { cartProductId ->
                    cartProducts.add(CartProduct(cartProductId, item, QUANTITY_STEP))
                    _selectedProducts.value =
                        selectedProducts.value.orEmpty().plus(
                            CartProduct(
                                cartProductId,
                                item,
                                QUANTITY_STEP,
                            ),
                        )
                    updateQuantity(item, QUANTITY_STEP)
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToCart)
                }
        }
    }

    override fun onQuantityIncreaseClick(id: Int) {
        val cartProduct = cartProducts.firstOrNull { it.product.id == id } ?: return
        viewModelScope.launch {
            val newQuantity = cartProduct.quantity + QUANTITY_STEP
            val result =
                cartProductRepository.updateQuantity(
                    cartProduct,
                    newQuantity,
                )

            result
                .onSuccess {
                    cartProducts.remove(cartProduct)
                    cartProducts.add(cartProduct.copy(quantity = newQuantity))
                    _selectedProducts.value =
                        selectedProducts.value.orEmpty().minus(cartProduct).plus(
                            cartProduct.copy(quantity = newQuantity),
                        )
                    updateQuantity(cartProduct.product, newQuantity)
                }
                .onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToIncrease)
                }
        }
    }

    override fun onQuantityDecreaseClick(id: Int) {
        val cartProduct = cartProducts.firstOrNull { it.product.id == id } ?: return
        viewModelScope.launch {
            val newQuantity = cartProduct.quantity - QUANTITY_STEP
            val result =
                cartProductRepository.updateQuantity(
                    cartProduct,
                    newQuantity,
                )

            result
                .onSuccess {
                    cartProducts.remove(cartProduct)
                    if (newQuantity > DEFAULT_COUNT) {
                        cartProducts.add(cartProduct.copy(quantity = newQuantity))
                        _selectedProducts.value =
                            selectedProducts.value.orEmpty().minus(cartProduct).plus(
                                cartProduct.copy(quantity = newQuantity),
                            )
                    } else {
                        _selectedProducts.value = selectedProducts.value?.minus(cartProduct)
                    }
                    updateQuantity(cartProduct.product, newQuantity)
                }
                .onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToDecrease)
                }
        }
    }

    private fun updateQuantity(
        item: Product,
        newQuantity: Int,
    ) {
        val updatedList =
            _recommendedProducts.value.orEmpty().map { recommendedProduct ->
                if (recommendedProduct.product.id == item.id) {
                    recommendedProduct.copy(quantity = newQuantity)
                } else {
                    recommendedProduct
                }
            }

        _recommendedProducts.value = updatedList
    }

    fun startOrder() {
        _startOrder.value = selectedProducts.value.orEmpty()
    }

    companion object {
        private const val RECOMMEND_SIZE = 10
        private const val QUANTITY_STEP = 1
        private const val DEFAULT_COUNT = 0
    }
}

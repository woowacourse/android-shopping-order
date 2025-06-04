package woowacourse.shopping.view.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
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

    init {
        loadRecommendedProducts()
    }

    fun plusCartItemQuantity(product: RecommendProduct) {
        if (product.quantity == 0) {
            cartRepository.addCartItem(product.id, 1) { result ->
                result
                    .onSuccess { cartItemId: Long? ->
                        if (cartItemId != null) {
                            addedItems = addedItems.plus(product.id to cartItemId)
                        }
                        product.update(1)
                    }.onFailure { _event.postValue(RecommendEvent.MODIfY_CART_FAILURE) }
            }
        } else {
            cartRepository.updateCartItemQuantity(
                addedItems[product.id] ?: return,
                product.quantity + 1,
            ) { result ->
                result
                    .onSuccess {
                        product.update(product.quantity + 1)
                    }.onFailure {
                        _event.postValue(RecommendEvent.MODIfY_CART_FAILURE)
                    }
            }
        }
    }

    fun minusCartItemQuantity(product: RecommendProduct) {
        if (product.quantity == 1) {
            cartRepository.remove(
                addedItems[product.id] ?: return,
            ) { result ->
                result
                    .onSuccess {
                        addedItems = addedItems.minus(product.id)
                        product.update(0)
                    }.onFailure {
                        _event.postValue(RecommendEvent.MODIfY_CART_FAILURE)
                    }
            }
        } else {
            cartRepository.updateCartItemQuantity(
                addedItems[product.id] ?: return,
                product.quantity - 1,
            ) { result ->
                result
                    .onSuccess {
                        product.update(product.quantity - 1)
                    }.onFailure {
                        _event.postValue(RecommendEvent.MODIfY_CART_FAILURE)
                    }
            }
        }
    }

    private fun loadRecommendedProducts() {
        productsRepository.loadRecommendedProducts(RECOMMEND_COUNT) { result ->
            result
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

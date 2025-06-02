package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class RecommendViewModel(
    private val productsRepository: ProductsRepository,
    private val cartItemRepository: CartItemRepository,
    price: Int,
    count: Int,
) : ViewModel() {
    private val _items: MutableLiveData<List<ProductUiModel>> = MutableLiveData(emptyList())
    val items: LiveData<List<ProductUiModel>>
        get() = _items

    private val _price = MutableLiveData(price)
    val price: LiveData<Int> = _price

    private val _count = MutableLiveData(count)
    val count: LiveData<Int> = _count

    private val _updatedProduct = MutableLiveData<ProductUiModel>()
    val updatedProduct: LiveData<ProductUiModel>
        get() = _updatedProduct

    init {
        loadRecommendedProductsFromLastViewed()
    }

    private fun loadRecommendedProductsFromLastViewed() {
        val cartProductIds = cartItemRepository.getCartItemIds()

        productsRepository.getRecommendedProductsFromLastViewed(
            cartProductIds = cartProductIds
        ) { result ->
            result
                .onSuccess { recommendedProducts ->
                    _items.postValue(recommendedProducts)
                }
        }
    }

    fun toggleQuantity(product: ProductUiModel) {
        val toggled =
            product.copy(quantity = product.quantity + 1)

        cartItemRepository.addCartItem(toggled.id, toggled.quantity) { result ->
            result
                .onSuccess {
                    _updatedProduct.postValue(toggled)
                    applyProductChange(toggled)
                }
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        val newProduct = product.copy(quantity = product.quantity + 1)

        cartItemRepository.updateCartItemQuantity(newProduct.id, newProduct.quantity) { result ->
            result
                .onSuccess {
                    _updatedProduct.postValue(newProduct)
                    applyProductChange(newProduct)
                }
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val newQuantity = (product.quantity - 1).coerceAtLeast(0)
        val updated = product.copy(quantity = newQuantity)

        if (product.quantity == 0) {
            cartItemRepository.deleteCartItem(product.id) { result ->
                result
                    .onSuccess {
                        applyProductChange(product)
                    }
            }
        } else {
            cartItemRepository.updateCartItemQuantity(updated.id, updated.quantity) { result ->
                result
                    .onSuccess {
                        _updatedProduct.postValue(updated)
                        applyProductChange(updated)
                    }
            }
        }
    }

    private fun applyProductChange(toggled: ProductUiModel) {
        val currentList = _items.value.orEmpty()
        val updatedList = currentList.map {
            if (it.id == toggled.id) toggled else it
        }
        _items.postValue(updatedList)
    }

    companion object {
        fun provideFactory(price: Int, count: Int): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    RecommendViewModel(
                        productsRepository = RepositoryProvider.productsRepository,
                        cartItemRepository = RepositoryProvider.cartItemRepository,
                        price = price,
                        count = count
                    )
                }
            }
        }
    }
}

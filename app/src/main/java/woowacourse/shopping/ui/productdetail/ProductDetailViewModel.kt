package woowacourse.shopping.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.EMPTY_PRODUCT
import woowacourse.shopping.domain.usecase.AddSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.GetProductDetailUseCase
import woowacourse.shopping.domain.usecase.GetRecentSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.UpdateCartProductUseCase
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class ProductDetailViewModel(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val getRecentSearchHistoryUseCase: GetRecentSearchHistoryUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val updateCartProductUseCase: UpdateCartProductUseCase,
) : ViewModel() {
    private val _product: MutableLiveData<Product> =
        MutableLiveData(EMPTY_PRODUCT)
    val product: LiveData<Product> get() = _product

    private val _lastHistoryProduct: MutableLiveData<HistoryProduct?> = MutableLiveData(null)
    val lastHistoryProduct: LiveData<HistoryProduct?> get() = _lastHistoryProduct

    private val _onCartProductAddSuccess: MutableSingleLiveData<Boolean?> =
        MutableSingleLiveData(null)
    val onCartProductAddSuccess: SingleLiveData<Boolean?> get() = _onCartProductAddSuccess

    fun loadProductDetail(id: Long) {
        getProductDetailUseCase(id) { catalogProduct ->
            _product.postValue(catalogProduct)
        }
    }

    fun loadLastHistoryProduct() {
        getRecentSearchHistoryUseCase { historyProduct ->
            _lastHistoryProduct.postValue(historyProduct)
        }
    }

    fun addHistoryProduct(id: Long) {
        addSearchHistoryUseCase(id)
    }

    fun decreaseCartProductQuantity() {
        _product.value = product.value?.decreaseQuantity()
    }

    fun increaseCartProductQuantity() {
        _product.value = product.value?.increaseQuantity()
    }

    fun updateCartProduct() {
        val product: Product = product.value ?: return
        runCatching {
            updateCartProductUseCase(
                productId = product.productDetail.id,
                cartId = product.cartId,
                quantity = product.quantity,
            )
        }.onSuccess {
            _onCartProductAddSuccess.postValue(true)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY]) as ShoppingApp

                    return ProductDetailViewModel(
                        getProductDetailUseCase = application.getProductDetailUseCase,
                        getRecentSearchHistoryUseCase = application.getRecentSearchHistoryUseCase,
                        addSearchHistoryUseCase = application.addSearchHistoryUseCase,
                        updateCartProductUseCase = application.updateCartProductUseCase,
                    ) as T
                }
            }
    }
}

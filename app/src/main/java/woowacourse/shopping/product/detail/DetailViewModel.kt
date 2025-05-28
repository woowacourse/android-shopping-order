package woowacourse.shopping.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.product.catalog.ProductUiModel

class DetailViewModel(
    product: ProductUiModel,
    private val cartProductRepository: CartProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val catalogProductRepository: CatalogProductRepository,
) : ViewModel() {
    private val _product = MutableLiveData<ProductUiModel>(product)
    val product: LiveData<ProductUiModel> = _product

    private val _quantity = MutableLiveData<Int>(1)
    val quantity: LiveData<Int> = _quantity

    private val _price = MutableLiveData<Int>(0)
    val price: LiveData<Int> = _price

    private val _latestViewedProduct = MutableLiveData<ProductUiModel>()
    val latestViewedProduct: LiveData<ProductUiModel> = _latestViewedProduct

    fun increaseQuantity() {
        _quantity.value = _quantity.value?.plus(1)
        setPriceSum()
    }

    fun decreaseQuantity() {
        if ((_quantity.value ?: 0) <= 0) return
        _quantity.value = _quantity.value?.minus(1)
        setPriceSum()
    }

    fun setQuantity() {
//        _quantity.value = productData.quantity
    }

    fun setPriceSum() {
        _price.value = (product.value?.price ?: 0) * (quantity.value ?: 0)
    }

    fun addToCart() {
        val addedProduct = product.value?.copy(quantity = quantity.value ?: 0)

        if (addedProduct?.cartItemId != null) {
            cartProductRepository.updateProduct(addedProduct, addedProduct.quantity) {}
        } else {
            addedProduct?.let {
                cartProductRepository.insertCartProduct(it) {}
            }
        }
    }

    fun addToRecentlyViewedProduct() {
        val uid = product.value?.id ?: return
        recentlyViewedProductRepository.insertRecentlyViewedProductUid(uid)
    }

    fun setLatestViewedProduct() {
        recentlyViewedProductRepository.getLatestViewedProduct { product ->
            _latestViewedProduct.postValue(product)
        }
    }
}

package woowacourse.shopping.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.feature.main.MainContract.View.MainScreenEvent
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.RecentProductUiModel

class MainPresenter(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : MainContract.Presenter {
    private val _products: MutableLiveData<List<CartProductUiModel>> = MutableLiveData()
    override val products: LiveData<List<CartProductUiModel>>
        get() = _products

    private val _recentProducts: MutableLiveData<List<RecentProductUiModel>> = MutableLiveData()
    override val recentProducts: LiveData<List<RecentProductUiModel>>
        get() = _recentProducts

    private val _badgeCount: MutableLiveData<Int> = MutableLiveData()
    override val badgeCount: LiveData<Int>
        get() = _badgeCount

    private val _mainScreenEvent: MutableLiveData<MainScreenEvent> = MutableLiveData()
    override val mainScreenEvent: LiveData<MainScreenEvent>
        get() = _mainScreenEvent

    override fun resetProducts() {
        productRepository.resetCache()
    }

    private fun createCartProductUiModels(
        products: List<Product>,
        cartInfo: List<CartProductUiModel>
    ): List<CartProductUiModel> {
        val cartItems = cartInfo.associateBy { it.productUiModel.id }
        return products.map { product ->
            val cartItem = cartItems[product.id]
            cartItem ?: CartProductUiModel(-1, product.toPresentation(), false)
        }
    }
}

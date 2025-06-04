package woowacourse.shopping.view.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class ProductDetailViewModelFactory(
    private val product: Product,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(
                product,
                cartProductRepository,
                recentProductRepository,
            ) as T
        }
        throw IllegalArgumentException(INVALID_VIEWMODEL_CLASS)
    }

    companion object {
        private const val INVALID_VIEWMODEL_CLASS: String = "생성할 수 없는 ViewModel 클래스입니다"
    }
}

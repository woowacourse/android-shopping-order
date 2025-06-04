package woowacourse.shopping.view.cart.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class CartProductRecommendationViewModelFactory(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductRecommendationViewModel::class.java)) {
            return CartProductRecommendationViewModel(
                productRepository,
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

package woowacourse.shopping.cart.recoomendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.CartRecommendationRepository
import woowacourse.shopping.data.repository.CartRecommendationRepositoryImpl
import woowacourse.shopping.data.source.CartProductRemoteDataSource
import woowacourse.shopping.data.source.CatalogProductDataSourceRemoteDataSource
import woowacourse.shopping.data.source.RecentlyViewedProductDataSourceRemoteDataSource

@Suppress("UNCHECKED_CAST")
class RecommendationFragmentViewModelFactory(
    private val application: ShoppingApplication,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendationFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val cartRecommendationRepository: CartRecommendationRepository =
                CartRecommendationRepositoryImpl.initialize(
                    cartProductDataSource = CartProductRemoteDataSource(),
                    catalogProductDataSource = CatalogProductDataSourceRemoteDataSource(),
                    recentlyViewProductDataSource =
                        RecentlyViewedProductDataSourceRemoteDataSource.initialize(
                            application.database.recentlyViewedProductDao(),
                        ),
                )
            return RecommendationFragmentViewModel(
                cartRecommendationRepository = cartRecommendationRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

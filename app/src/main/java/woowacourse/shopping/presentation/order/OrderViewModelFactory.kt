package woowacourse.shopping.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule

class OrderViewModelFactory : ViewModelProvider.Factory {
    private val cartRepository = RepositoryModule.provideCartRepository()
    private val couponRepository = RepositoryModule.provideCouponRepository()

    override fun <T : ViewModel> create(modelClass: Class<T>): T = OrderViewModel(cartRepository, couponRepository) as T
}

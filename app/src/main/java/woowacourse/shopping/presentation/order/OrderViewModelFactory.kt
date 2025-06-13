package woowacourse.shopping.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule
import woowacourse.shopping.domain.usecase.GetAvailableCouponsUseCase

class OrderViewModelFactory : ViewModelProvider.Factory {
    private val cartRepository = RepositoryModule.provideCartRepository()
    private val couponRepository = RepositoryModule.provideCouponRepository()
    private val getAvailableCouponsUseCase = GetAvailableCouponsUseCase(couponRepository)
    private val orderRepository = RepositoryModule.provideOrderRepository()

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        OrderViewModel(cartRepository, orderRepository, getAvailableCouponsUseCase) as T
}

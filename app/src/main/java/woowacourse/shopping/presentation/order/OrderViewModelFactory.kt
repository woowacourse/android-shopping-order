package woowacourse.shopping.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.di.RepositoryModule
import woowacourse.shopping.di.UseCaseModule

class OrderViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        val savedStateHandle = extras.createSavedStateHandle()
        val getAvailableCouponUseCase = UseCaseModule.getAvailableCouponUseCase
        val orderRepository = RepositoryModule.orderRepository
        return OrderViewModel(savedStateHandle, getAvailableCouponUseCase, orderRepository) as T
    }
}

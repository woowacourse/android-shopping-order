package woowacourse.shopping.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.UseCaseModule

class OrderViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val getAvailableCouponUseCase = UseCaseModule.getAvailableCouponUseCase
        return OrderViewModel(getAvailableCouponUseCase) as T
    }
}

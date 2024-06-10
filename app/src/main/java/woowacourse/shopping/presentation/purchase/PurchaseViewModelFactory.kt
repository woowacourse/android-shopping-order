package woowacourse.shopping.presentation.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.data.repository.DefaultCartRepository
import com.example.domain.repository.CouponRepository
import com.example.domain.repository.OrderRepository
import java.lang.IllegalArgumentException

class PurchaseViewModelFactory(
    private val cartRepository: DefaultCartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        if (modelClass.isAssignableFrom(PurchaseViewModel::class.java)) {
            return PurchaseViewModel(
                extras.createSavedStateHandle(),
                cartRepository,
                orderRepository,
                couponRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}

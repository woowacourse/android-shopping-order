package woowacourse.shopping.presentation.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository

class OrderViewModelFactory(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderDatabase: OrderDatabase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(
                cartRepository = cartRepository,
                couponRepository = couponRepository,
                orderDatabase = orderDatabase,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory

class OrderRecommendViewModel(
    private val productHistoryRepository: ProductHistoryRepository,
    private val orderRepository: OrderRepository,
) :
    BaseViewModel() {
    override fun retry() {}

    companion object {
        fun factory(
            productHistoryRepository: ProductHistoryRepository,
            orderRepository: OrderRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                OrderRecommendViewModel(
                    productHistoryRepository,
                    orderRepository,
                )
            }
        }
    }
}

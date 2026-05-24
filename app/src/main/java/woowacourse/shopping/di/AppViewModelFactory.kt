package woowacourse.shopping.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ui.order.OrderViewModel
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.detail.DetailProductViewModel
import woowacourse.shopping.ui.payment.PaymentViewModel
import woowacourse.shopping.ui.productlist.ProductListViewModel
import woowacourse.shopping.ui.recommend.ShoppingCartRecommendViewModel

class AppViewModelFactory(
    private val appContainer: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ProductListViewModel::class.java) ->
                ProductListViewModel(
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                    visitStore = appContainer.visitStore,
                    productRepository = appContainer.productRepository,
                ) as T

            modelClass.isAssignableFrom(DetailProductViewModel::class.java) ->
                DetailProductViewModel(
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                    visitStore = appContainer.visitStore,
                    productRepository = appContainer.productRepository,
                ) as T

            modelClass.isAssignableFrom(ShoppingCartRecommendViewModel::class.java) ->
                ShoppingCartRecommendViewModel(
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                    visitStore = appContainer.visitStore,
                ) as T

            modelClass.isAssignableFrom(ShoppingCartViewModel::class.java) ->
                ShoppingCartViewModel(
                    shoppingCartRepository = appContainer.shoppingCartRepository,
                ) as T

            modelClass.isAssignableFrom(OrderViewModel::class.java) ->
                OrderViewModel(orderRepository = appContainer.orderRepository) as T

            modelClass.isAssignableFrom(PaymentViewModel::class.java) ->
                PaymentViewModel(
                    shoppingCartRepository = appContainer.shoppingCartRepository,
                    couponRepository = appContainer.couponRepository,
                    paymentReminderSettingsRepository = appContainer.paymentReminderSettingsRepository,
                ) as T

            else -> throw IllegalArgumentException("지원하지 않는 ViewModel: ${modelClass.name}")
        }
}

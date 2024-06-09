package woowacourse.shopping.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.remote.injector.CartItemRepositoryInjector
import woowacourse.shopping.data.remote.injector.CouponRepositoryInjector
import woowacourse.shopping.data.remote.injector.OrderRepositoryInjector
import woowacourse.shopping.data.remote.injector.ProductRepositoryInjector
import woowacourse.shopping.data.remote.injector.RecentProductRepositoryInjector
import woowacourse.shopping.domain.usecase.CurationUseCase
import woowacourse.shopping.presentation.ui.cart.CartViewModel
import woowacourse.shopping.presentation.ui.curation.CurationViewModel
import woowacourse.shopping.presentation.ui.detail.ProductDetailViewModel
import woowacourse.shopping.presentation.ui.payment.PaymentActionViewModel
import woowacourse.shopping.presentation.ui.shopping.ShoppingViewModel

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> {
                ProductDetailViewModel(
                    CartItemRepositoryInjector.instance,
                    RecentProductRepositoryInjector.instance,
                ) as T
            }

            modelClass.isAssignableFrom(ShoppingViewModel::class.java) -> {
                ShoppingViewModel(
                    ProductRepositoryInjector.instance,
                    CartItemRepositoryInjector.instance,
                    RecentProductRepositoryInjector.instance,
                ) as T
            }

            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(
                    CartItemRepositoryInjector.instance,
                ) as T
            }

            modelClass.isAssignableFrom(CurationViewModel::class.java) -> {
                CurationViewModel(
                    CartItemRepositoryInjector.instance,
                    CurationUseCase(
                        RecentProductRepositoryInjector.instance,
                        ProductRepositoryInjector.instance,
                        CartItemRepositoryInjector.instance,
                    ),
                ) as T
            }

            modelClass.isAssignableFrom(PaymentActionViewModel::class.java) -> {
                PaymentActionViewModel(
                    OrderRepositoryInjector.instance,
                    CouponRepositoryInjector.instance,
                    RecentProductRepositoryInjector.instance,
                ) as T
            }

            else -> {
                throw IllegalArgumentException(INVALID_VIEWMODEL)
            }
        }
    }

    companion object {
        const val INVALID_VIEWMODEL = "뷰모델이 적절하지 않은 케이스입니다."
    }
}

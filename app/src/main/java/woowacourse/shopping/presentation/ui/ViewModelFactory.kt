package woowacourse.shopping.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.RepositoryInjector
import woowacourse.shopping.presentation.ui.cart.CartViewModel
import woowacourse.shopping.presentation.ui.curation.CurationViewModel
import woowacourse.shopping.presentation.ui.detail.ProductDetailViewModel
import woowacourse.shopping.presentation.ui.payment.PaymentViewModel
import woowacourse.shopping.presentation.ui.shopping.ShoppingViewModel

class ViewModelFactory(private val ids: List<Long> = emptyList()) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> {
                ProductDetailViewModel(
                    RepositoryInjector.repository,
                ) as T
            }

            modelClass.isAssignableFrom(ShoppingViewModel::class.java) -> {
                ShoppingViewModel(
                    RepositoryInjector.repository,
                ) as T
            }

            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(
                    RepositoryInjector.repository,
                ) as T
            }

            modelClass.isAssignableFrom(CurationViewModel::class.java) -> {
                CurationViewModel(
                    RepositoryInjector.repository,
                    ids,
                ) as T
            }

            modelClass.isAssignableFrom(PaymentViewModel::class.java) -> {
                PaymentViewModel(
                    RepositoryInjector.repository,
                    ids,
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

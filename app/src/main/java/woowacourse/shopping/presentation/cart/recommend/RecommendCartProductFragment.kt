package woowacourse.shopping.presentation.cart.recommend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryInjector
import woowacourse.shopping.data.shopping.ShoppingRepositoryInjector
import woowacourse.shopping.databinding.FragmentRecommendCartProductBinding
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ShoppingRepository
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.BindingFragment

class RecommendCartProductFragment :
    BindingFragment<FragmentRecommendCartProductBinding>(R.layout.fragment_recommend_cart_product) {

    private val viewModel by viewModels<RecommendCartProductViewModel> {
        val orderIds = arguments?.getLongArray(ORDERED_PRODUCTS_ID) ?: longArrayOf()
        RecommendCartProductViewModel.factory(
            orderIds.toList(),
            CartRepositoryInjector.cartRepository(),
            ShoppingRepositoryInjector.shoppingRepository(requireContext().applicationContext)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        val TAG: String? = RecommendCartProductFragment::class.java.canonicalName

        private const val ORDERED_PRODUCTS_ID = "PRODUCT_ID"
        fun args(orderIds: List<Long>): Bundle =
            Bundle().apply {
                putLongArray(ORDERED_PRODUCTS_ID, orderIds.toLongArray())
            }
    }
}

class RecommendCartProductViewModel(
    private val orderIds: List<Long>,
    private val cartRepository: CartRepository,
    private val shoppingRepository: ShoppingRepository,
) : ViewModel() {


    companion object {
        fun factory(
            orderIds: List<Long>,
            cartRepository: CartRepository,
            shoppingRepository: ShoppingRepository
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                RecommendCartProductViewModel(orderIds, cartRepository, shoppingRepository)
            }
        }
    }
}
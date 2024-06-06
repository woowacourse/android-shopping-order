package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import android.os.Build
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentOrderRecommendBinding
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.model.CartsWrapper
import woowacourse.shopping.presentation.ui.payment.PaymentActivity
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.adapter.RecommendAdapter

class OrderRecommendFragment : BaseFragment<FragmentOrderRecommendBinding>() {
    override val layoutResourceId: Int get() = R.layout.fragment_order_recommend

    private val viewModel: OrderRecommendViewModel by viewModels {
        OrderRecommendViewModel.factory(
            ProductHistoryRepository.getInstance(),
            ShoppingCartRepository.getInstance(),
        )
    }

    private val adapter: RecommendAdapter by lazy { RecommendAdapter(viewModel) }

    override fun initViewCreated() {
        initDataBinding()
        initAdapter()
        initObserve()
        val cartsWrapper: CartsWrapper? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(PUT_EXTRA_CART_IDS_KEY, CartsWrapper::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getSerializable(PUT_EXTRA_CART_IDS_KEY)
                    as? CartsWrapper
            }

        cartsWrapper?.let {
            viewModel.load(it.carts)
        }
    }

    private fun initDataBinding() {
        binding.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun initAdapter() {
        binding.rvRecommendOrder.adapter = adapter
    }

    private fun initObserve() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.recommendProducts)
        }

        viewModel.navigateAction.observeEvent(viewLifecycleOwner) { navigateAction ->
            when (navigateAction) {
                is OrderRecommendNavigateAction.NavigateToPayment -> {
                    val intent =
                        PaymentActivity.getIntent(
                            this.requireContext(),
                            navigateAction.cartsWrapper,
                        )
                    requireContext().startActivity(intent)
                }
            }
        }
    }

    companion object {
        const val TAG = "OrderRecommendFragment"

        const val PUT_EXTRA_CART_IDS_KEY = "PUT_EXTRA_CART_IDS_KEY"
    }
}

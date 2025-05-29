package woowacourse.shopping.view.cart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.App
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentOrderCompleteBinding
import woowacourse.shopping.view.cart.adapter.RecommendAdapter
import woowacourse.shopping.view.cart.vm.CartViewModel
import woowacourse.shopping.view.cart.vm.CartViewModelFactory
import kotlin.getValue

class OrderCompleteFragment :
    Fragment(
        R.layout.fragment_order_complete,
    ),
    RecommendAdapter.Handler {
    private val viewModel: CartViewModel by activityViewModels {
        val container = (requireActivity().application as App).container
        CartViewModelFactory(
            container.cartRepository,
            container.historyLoader,
        )
    }

    private val adapter: RecommendAdapter by lazy {
        RecommendAdapter(emptyList(), this@OrderCompleteFragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOrderCompleteBinding.bind(view)
        setUpBinding(binding)
    }

    private fun setUpBinding(binding: FragmentOrderCompleteBinding)  {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onSelectProduct(productId: Long) {
        TODO("Not yet implemented")
    }

    override fun showQuantity(productId: Long) {
        TODO("Not yet implemented")
    }
}

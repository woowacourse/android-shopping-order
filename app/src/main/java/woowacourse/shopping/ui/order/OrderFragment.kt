package woowacourse.shopping.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import woowacourse.shopping.databinding.FragmentOrderBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.order.event.OrderEvent

class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException()

    private val viewModel: OrderViewModel by viewModels {
        OrderViewModel.factory()
    }

    private val recommendedProductsAdapter: RecommendedProductsAdapter by lazy {
        RecommendedProductsAdapter(
            viewModel,
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.rvOrderRecommendedProducts.adapter = recommendedProductsAdapter

        observeRecommendedProducts()
        observeEvent()

        return binding.root
    }

    private fun observeRecommendedProducts() {
        viewModel.recommendedProducts.observe(viewLifecycleOwner) {
            recommendedProductsAdapter.submitList(it)
        }
    }

    private fun observeEvent() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                OrderEvent.CompleteOrder -> {
                    ((requireActivity()) as FragmentNavigator).navigateToPayment()
                }
            }
        }
    }

    companion object {
        const val TAG = "OrderFragment"
    }
}

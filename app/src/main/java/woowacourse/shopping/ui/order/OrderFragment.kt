package woowacourse.shopping.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        viewModel.recommendedProducts.observe(viewLifecycleOwner) {
            recommendedProductsAdapter.submitList(it)
        }

        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                OrderEvent.CompleteOrder -> {
                    Toast.makeText(requireContext(), "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    (requireActivity() as FragmentNavigator).navigateToProductList()
                }
            }

        }

        return binding.root
    }

    companion object {
        const val TAG = "OrderFragment"
    }
}

package woowacourse.shopping.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentOrderBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.order.event.OrderError
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
        observeError()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeRecommendedProducts()
        observeEvent()
        observeError()
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

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            when (error) {
                OrderError.LoadRecommendedProducts -> showToast(R.string.error_load_recommended_products)
                OrderError.CalculateOrderItemsQuantity -> showToast(R.string.error_calculate_order_items_quantity)
                OrderError.CalculateOrderItemsTotalPrice -> showToast(R.string.error_calculate_order_items_total_price)
                OrderError.UpdateOrderItem -> showToast(R.string.error_update_order_item)
            }
        }
    }

    private fun showToast(@StringRes stringId: Int) {
        Toast.makeText(
            requireContext(),
            stringId,
            Toast.LENGTH_SHORT,
        ).show()
    }

    companion object {
        const val TAG = "OrderFragment"
    }
}

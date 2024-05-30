package woowacourse.shopping.presentation.ui.cart.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.cart.CartViewModel
import woowacourse.shopping.presentation.ui.cart.OrderState
import woowacourse.shopping.presentation.util.EventObserver

class RecommendFragment : Fragment() {
    lateinit var binding: FragmentRecommendBinding
    private val viewModel: CartViewModel by activityViewModels()
    private lateinit var adapter: RecommendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRecommendBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initCartAdapter()
        observeErrorEventUpdates()
        observeCartUpdates()
        observeRecommendedUpdates()
        viewModel.buildRecommendProducts()
    }

    private fun initCartAdapter() {
        adapter = RecommendAdapter(quantityHandler = viewModel)
        binding.rvRecommendProducts.adapter = adapter
    }

    private fun observeRecommendedUpdates() {
        viewModel.recommendedProduct.observe(viewLifecycleOwner) {
            adapter.updateItems(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setOrderState(OrderState.Recommend)
    }

    private fun observeErrorEventUpdates() {
        viewModel.error.observe(
            viewLifecycleOwner,
            EventObserver {
            },
        )
    }

    private fun observeCartUpdates() {
        viewModel.shoppingProducts.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
//                    cartAdapter.updateList(it.data)
                }
            }
        }
    }

    companion object {
        fun newInstance() = CartListFragment()
    }
}

package woowacourse.shopping.view.cart.recommend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.App
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.view.cart.vm.CartViewModel
import woowacourse.shopping.view.cart.vm.CartViewModelFactory

class RecommendFragment : Fragment(R.layout.fragment_recommend) {
    private val viewModel: CartViewModel by activityViewModels {
        val container = (requireActivity().application as App).container
        CartViewModelFactory(
            container.cartRepository,
            container.productRepository,
        )
    }

    private val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter(
            viewModel.recommendEventHandler,
            viewModel.quantityEventHandler,
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRecommendBinding.bind(view)
        setUpBinding(binding)
        setUpRecyclerView(binding)
        viewModel.loadRecommendProduct()
        viewModel.recommendUiState.observe(viewLifecycleOwner) { value ->
            recommendAdapter.submitList(value.item)
        }
    }

    private fun setUpBinding(binding: FragmentRecommendBinding) {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            adapter = recommendAdapter
        }
    }

    private fun setUpRecyclerView(binding: FragmentRecommendBinding) {
        binding.recyclerViewRecommend.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
    }
}

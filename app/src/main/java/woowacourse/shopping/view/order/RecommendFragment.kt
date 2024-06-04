package woowacourse.shopping.view.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.view.detail.DetailActivity
import woowacourse.shopping.view.home.adapter.product.HomeViewItem
import woowacourse.shopping.view.order.adapter.recommend.RecommendAdapter
import woowacourse.shopping.view.order.viewmodel.OrderViewModel
import woowacourse.shopping.view.state.UiState

class RecommendFragment : Fragment() {
    private lateinit var binding: FragmentRecommendBinding
    private lateinit var adapter: RecommendAdapter
    private val viewModel by activityViewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setUpDataBinding()
        observeViewModel()
    }

    private fun setUpAdapter() {
        adapter = RecommendAdapter(viewModel)
        binding.rvRecommend.adapter = adapter
        viewModel.generateRecommendProductViewItems()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {
        viewModel.recommendUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> showData(state.data)
                is UiState.Loading -> showData(emptyList())
                is UiState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) { navigateToDetail ->
            navigateToDetail.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }
    }

    private fun showData(productViewItems: List<HomeViewItem.ProductViewItem>) {
        adapter.submitList(productViewItems)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(DetailActivity.createIntent(requireContext(), productId))
    }

    companion object {
        fun newInstance(): Fragment {
            return RecommendFragment()
        }
    }
}

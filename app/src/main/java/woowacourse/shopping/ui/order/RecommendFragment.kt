package woowacourse.shopping.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.ui.detail.DetailActivity
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem
import woowacourse.shopping.ui.order.adapter.recommend.RecommendAdapter
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.state.UiState

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding: FragmentRecommendBinding
        get() = _binding!!

    private lateinit var adapter: RecommendAdapter
    private val viewModel by activityViewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

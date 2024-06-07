package woowacourse.shopping.ui.order.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication.Companion.cartDataSourceImpl
import woowacourse.shopping.app.ShoppingApplication.Companion.productDataSourceImpl
import woowacourse.shopping.app.ShoppingApplication.Companion.recentProductDatabase
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.ui.detail.DetailActivity
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem
import woowacourse.shopping.ui.order.cart.viewmodel.RecommendViewModelFactory
import woowacourse.shopping.ui.order.recommend.adapter.RecommendAdapter
import woowacourse.shopping.ui.order.recommend.viewmodel.RecommendViewModel
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.state.UiState

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding: FragmentRecommendBinding
        get() = _binding!!

    private lateinit var adapter: RecommendAdapter
    private val orderViewModel by activityViewModels<OrderViewModel>()
    private val recommendViewModel by viewModels<RecommendViewModel> {
        RecommendViewModelFactory(
            recentProductRepository = RecentProductRepositoryImpl(recentProductDatabase),
            productRepository = ProductRepositoryImpl(productDataSourceImpl),
            cartRepository = CartRepositoryImpl(cartDataSourceImpl),
            orderViewModel = orderViewModel,
        )
    }

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
        adapter = RecommendAdapter(recommendViewModel)
        binding.rvRecommend.adapter = adapter
        recommendViewModel.generateRecommendProductViewItems()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun observeViewModel() {
        recommendViewModel.recommendUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> showData(state.data)
                is UiState.Loading -> showData(emptyList())
                is UiState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
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

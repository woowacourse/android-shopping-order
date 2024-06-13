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
import woowacourse.shopping.app.ShoppingApplication.Companion.localRecentDataSource
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteProductDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.ui.detail.DetailActivity
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.ui.order.recommend.action.RecommendNavigationActions.NavigateToDetail
import woowacourse.shopping.ui.order.recommend.action.RecommendNotifyingActions
import woowacourse.shopping.ui.order.recommend.action.RecommendShareActions.MinusCartViewItemQuantity
import woowacourse.shopping.ui.order.recommend.action.RecommendShareActions.PlusCartViewItemQuantity
import woowacourse.shopping.ui.order.recommend.action.RecommendShareActions.UpdateNewCartViewItems
import woowacourse.shopping.ui.order.recommend.adapter.RecommendAdapter
import woowacourse.shopping.ui.order.recommend.viewmodel.RecommendViewModel
import woowacourse.shopping.ui.order.recommend.viewmodel.RecommendViewModelFactory
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
            recentProductRepository = RecentProductRepositoryImpl(localRecentDataSource),
            productRepository = ProductRepositoryImpl(remoteProductDataSource),
            cartRepository = CartRepositoryImpl(remoteCartDataSource),
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
        orderViewModel.updateCurrentFragmentName(this::class.java.simpleName)
        setUpAdapter()
        setUpDataBinding()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        recommendViewModel.updateRecommendProductViewItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpAdapter() {
        adapter = RecommendAdapter(recommendViewModel)
        binding.rvRecommend.adapter = adapter
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

        recommendViewModel.recommendShareActions.observe(viewLifecycleOwner) { recommendShareActions ->
            recommendShareActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is UpdateNewCartViewItems -> orderViewModel.updateCartViewItems(action.newCartViewItems)
                    is PlusCartViewItemQuantity -> orderViewModel.onQuantityPlusButtonClick(action.productId)
                    is MinusCartViewItemQuantity -> orderViewModel.onQuantityMinusButtonClick(action.productId)
                }
            }
        }

        recommendViewModel.recommendNavigationActions.observe(viewLifecycleOwner) { recommendNavigationActions ->
            recommendNavigationActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is NavigateToDetail -> navigateToDetail(action.productId)
                }
            }
        }

        recommendViewModel.recommendNotifyingActions.observe(viewLifecycleOwner) { recommendNotifyingActions ->
            recommendNotifyingActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is RecommendNotifyingActions.NotifyError -> showError(getString(R.string.unknown_error))
                }
            }
        }

        orderViewModel.cartViewItems.observe(viewLifecycleOwner) { cartViewItems ->
            recommendViewModel.updateSharedCartViewItems(cartViewItems)
        }
    }

    private fun showData(productViewItems: List<ProductViewItem>) {
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

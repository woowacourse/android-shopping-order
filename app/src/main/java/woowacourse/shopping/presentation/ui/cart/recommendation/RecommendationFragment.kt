package woowacourse.shopping.presentation.ui.cart.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.data.repository.RemoteShoppingRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.order.OrderActivity

class RecommendationFragment : Fragment(), RecommendationClickListener {
    private lateinit var binding: FragmentRecommendBinding
    private lateinit var recommendAdapter: RecommendAdapter
    private val viewModel: RecommendViewModel by lazy {
        val viewModelFactory =
            RecommendViewModelFactory(
                cartRepository = RemoteCartRepositoryImpl(),
                shoppingRepository = RemoteShoppingRepositoryImpl(),
                recentProductRepository = RecentProductRepositoryImpl(requireContext()),
                orderDatabase = OrderDatabase,
            )
        viewModelFactory.create(RecommendViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRecommendBinding.inflate(inflater, container, false)
        setUpRecyclerViewAdapter()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.clickListener = this

        observeViewModel()
    }

    private fun setUpRecyclerViewAdapter() {
        recommendAdapter = RecommendAdapter(viewModel)
        binding.recyclerviewRecommendationList.adapter = recommendAdapter
    }

    private fun showSkeletonUI(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerRecommendationList.startShimmer()
        } else {
            binding.shimmerRecommendationList.stopShimmer()
        }
    }

    private fun observeViewModel() {
        viewModel.recommendItemsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Success -> showData(state.data)
                is UIState.Empty -> {}
                is UIState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }

        viewModel.shoppingProducts.observe(viewLifecycleOwner) {
            recommendAdapter.submitList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showSkeletonUI(it)
        }
    }

    private fun showData(data: List<ShoppingProduct>) {
        recommendAdapter.submitList(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onMakeOrderClick() {
        viewModel.postOrder()
        startActivity(OrderActivity.createIntent(requireContext()))
    }
}

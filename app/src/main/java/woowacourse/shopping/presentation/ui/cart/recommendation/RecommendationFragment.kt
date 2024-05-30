package woowacourse.shopping.presentation.ui.cart.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.ShoppingItemsRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.state.UIState

class RecommendationFragment : Fragment() {
    private lateinit var binding: FragmentRecommendBinding
    private val recommendViewModel: RecommendViewModel by lazy {
        val viewModelFactory =
            RecommendViewModelFactory(
                cartRepository = CartRepositoryImpl(requireContext()),
                shoppingRepository = ShoppingItemsRepositoryImpl(),
                recentProductRepository = RecentProductRepositoryImpl(requireContext()),
            )
        viewModelFactory.create(RecommendViewModel::class.java)
    }

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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = recommendViewModel
        loadRecommendItems()
    }

    private fun loadRecommendItems() {
        lifecycleScope.launch {
            showSkeletonUI(isLoading = true)
            delay(3000)
            showSkeletonUI(isLoading = false)
            setUpViews()
        }
    }

    private fun showSkeletonUI(isLoading: Boolean) {
//        if (isLoading) {
//            binding. .startShimmer()
//            binding.shimmerCartList.visibility = View.VISIBLE
//            binding.recyclerView.visibility = View.GONE
//        } else {
//            binding.shimmerCartList.stopShimmer()
//            binding.shimmerCartList.visibility = View.GONE
//            binding.recyclerView.visibility = View.VISIBLE
//        }
    }

    private fun setUpViews() {
        setUpUIState()
    }

    private fun setUpUIState() {
        val adapter = setUpRecyclerViewAdapter()
        recommendViewModel.recommendItemsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Success -> showData(state.data, adapter)
                is UIState.Empty -> {}
                is UIState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }
    }

    private fun setUpRecyclerViewAdapter(): RecommendAdapter {
        val adapter = RecommendAdapter(recommendViewModel)
        binding.recyclerviewRecommendationList.adapter = adapter
        return adapter
    }

    private fun showData(
        data: List<ShoppingProduct>,
        adapter: RecommendAdapter,
    ) {
        adapter.loadData(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}

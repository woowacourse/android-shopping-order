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
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.data.repository.RemoteShoppingRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.state.UIState

class RecommendationFragment : Fragment(), RecommendationClickListener {
    private lateinit var binding: FragmentRecommendBinding
    private lateinit var recommendAdapter: RecommendAdapter
    private val viewModel: RecommendViewModel by lazy {
        val viewModelFactory =
            RecommendViewModelFactory(
                cartRepository = RemoteCartRepositoryImpl(),
                shoppingRepository = RemoteShoppingRepositoryImpl(),
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
        binding.viewModel = viewModel
        binding.clickListener = this
        loadRecommendItems()
    }

    private fun loadRecommendItems() {
        lifecycleScope.launch {
            showSkeletonUI(isLoading = true)
            delay(1500)
            showSkeletonUI(isLoading = false)
            setUpViews()
        }
    }

    private fun showSkeletonUI(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerRecommendationList.startShimmer()
            viewModel.onLoading()
        } else {
            binding.shimmerRecommendationList.stopShimmer()
            viewModel.onLoaded()
        }
    }

    private fun setUpViews() {
        setUpUIState()
    }

    private fun setUpUIState() {
        setUpRecyclerViewAdapter()
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
    }

    private fun setUpRecyclerViewAdapter() {
        recommendAdapter = RecommendAdapter(viewModel)
        binding.recyclerviewRecommendationList.adapter = recommendAdapter
    }

    private fun showData(data: List<ShoppingProduct>) {
        recommendAdapter.loadData(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onMakeOrderClick() {
        viewModel.completeOrder()
        requireActivity().finish()
    }
}

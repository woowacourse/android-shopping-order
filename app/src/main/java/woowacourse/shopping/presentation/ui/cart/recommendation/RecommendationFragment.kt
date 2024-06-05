package woowacourse.shopping.presentation.ui.cart.recommendation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.data.repository.RemoteShoppingRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.cart.FragmentController

class RecommendationFragment : Fragment(), RecommendationClickListener {
    private lateinit var binding: FragmentRecommendBinding
    private lateinit var recommendAdapter: RecommendAdapter
    private lateinit var fragmentEventListener: FragmentController
    private val recommendViewModel: RecommendViewModel by lazy {
        val viewModelFactory =
            RecommendViewModelFactory(
                cartRepository = RemoteCartRepositoryImpl(),
                shoppingRepository = RemoteShoppingRepositoryImpl(),
                recentProductRepository = RecentProductRepositoryImpl(requireContext()),
            )
        viewModelFactory.create(RecommendViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentController) {
            fragmentEventListener = context
        } else {
            error("invalid activity(context), $context")
        }
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
        setUpRecyclerViewAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = recommendViewModel
        binding.clickListener = this
        observeViewModel()
    }

    private fun setUpRecyclerViewAdapter() {
        recommendAdapter = RecommendAdapter(recommendViewModel)
        binding.recyclerviewRecommendationList.adapter = recommendAdapter
    }

    private fun observeViewModel() {
        recommendViewModel.isLoading.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { isLoading ->
                showSkeletonUI(isLoading)
            }
        }

        recommendViewModel.recommendItemsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Success -> showData(state.data)
                is UIState.Empty -> {}
                is UIState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }

        recommendViewModel.changedIds.observe(viewLifecycleOwner) {
            recommendAdapter.updateItems(it)
        }
    }

    private fun showSkeletonUI(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerRecommendationList.startShimmer()
            binding.shimmerRecommendationList.visibility = View.VISIBLE
            binding.recyclerviewRecommendationList.visibility = View.GONE
        } else {
            binding.shimmerRecommendationList.stopShimmer()
            binding.shimmerRecommendationList.visibility = View.GONE
            binding.recyclerviewRecommendationList.visibility = View.VISIBLE
        }
    }

    private fun showData(data: List<ShoppingProduct>) {
        recommendAdapter.submitItems(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        recommendViewModel.setLoadingState(true)
        recommendViewModel.setRecommendation()
    }

    override fun onMakeOrderClick() {
        recommendViewModel.completeOrder()
        fragmentEventListener.onOrderButtonClicked()
    }
}

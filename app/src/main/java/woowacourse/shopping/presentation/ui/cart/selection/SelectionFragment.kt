package woowacourse.shopping.presentation.ui.cart.selection

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
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.databinding.FragmentSelectionBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.cart.recommendation.RecommendationFragment
import woowacourse.shopping.presentation.ui.detail.DetailActivity

class SelectionFragment : Fragment(), SelectionClickListener {
    private lateinit var binding: FragmentSelectionBinding
    private val viewModel: SelectionViewModel by lazy {
        val viewModelFactory =
            SelectionViewModelFactory(
                cartRepository = RemoteCartRepositoryImpl(),
            )
        viewModelFactory.create(SelectionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSelectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.clickListener = this

        observeViewModel()
        showSkeletonUI()
    }

    private fun setUpViews() {
        setUpUIState()
    }

    private fun setUpRecyclerViewAdapter(): SelectionAdapter {
        val adapter = SelectionAdapter(viewModel, viewModel)
        binding.recyclerView.adapter = adapter
        return adapter
    }

    private fun setUpUIState() {
        val adapter = setUpRecyclerViewAdapter()
        viewModel.cartItemsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Success -> showData(state.data, adapter)
                is UIState.Empty -> {} // emptyCart()
                is UIState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }
    }

    private fun showData(
        data: List<CartItem>,
        adapter: SelectionAdapter,
    ) {
        adapter.loadData(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun emptyCart() {
        viewModel.isCartEmpty()
        Toast.makeText(requireContext(), getString(R.string.empty_cart_message), Toast.LENGTH_LONG).show()
    }

    private fun observeViewModel() {
        viewModel.deleteCartItem.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { itemId ->
                viewModel.deleteItem(itemId)
            }
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }
    }

    private fun navigateToDetail(productId: Long) {
        startActivity(DetailActivity.createIntent(requireContext(), productId))
    }

    private fun showSkeletonUI() {
        lifecycleScope.launch {
            showCartData(isLoading = true)
            delay(1500)
            showCartData(isLoading = false)
            setUpViews()
        }
    }

    private fun showCartData(isLoading: Boolean) {
        if (isLoading) {
            viewModel.onLoading()
            binding.shimmerCartList.startShimmer()
        } else {
            viewModel.onLoaded()
            binding.shimmerCartList.stopShimmer()
        }
    }

    override fun onMakeOrderClick() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.cart_fragment, RecommendationFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    override fun onSelectAllClick() {
        OrderDatabase.postOrder(viewModel.order.value ?: Order())
        viewModel.selectAllByCondition()
    }
}

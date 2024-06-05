package woowacourse.shopping.presentation.ui.cart.selection

import android.content.Context
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
import woowacourse.shopping.databinding.FragmentSelectionBinding
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.cart.CartItemUiModel
import woowacourse.shopping.presentation.ui.cart.FragmentController
import woowacourse.shopping.presentation.ui.detail.DetailActivity

class SelectionFragment : Fragment(), SelectionClickListener {
    private lateinit var binding: FragmentSelectionBinding
    private lateinit var selectionAdapter: SelectionAdapter
    private lateinit var fragmentEventListener: FragmentController
    private val viewModel: SelectionViewModel by lazy {
        val viewModelFactory =
            SelectionViewModelFactory(
                shoppingRepository = RemoteShoppingRepositoryImpl(),
                recentProductRepository = RecentProductRepositoryImpl(requireContext()),
                cartRepository = RemoteCartRepositoryImpl(),
            )
        viewModelFactory.create(SelectionViewModel::class.java)
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
        binding = FragmentSelectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.clickListener = this

        observeViewModel()
    }

    private fun setUpRecyclerView() {
        selectionAdapter = SelectionAdapter(viewModel, viewModel)
        binding.recyclerView.adapter = selectionAdapter
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { isLoading ->
                showSkeletonUI(isLoading)
            }
        }

        viewModel.uiCartItemsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Success -> showData(state.data, selectionAdapter)
                is UIState.Empty -> {}
                is UIState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }

        viewModel.isCheckedChangedIds.observe(viewLifecycleOwner) {
            selectionAdapter.updateChecked(it)
        }

        viewModel.quantityChangedIds.observe(viewLifecycleOwner) {
            selectionAdapter.updateQuantity(it)
        }

        viewModel.deletedId.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { itemId ->
                selectionAdapter.deleteItem(itemId)
            }
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }
    }

    private fun showData(
        data: List<CartItemUiModel>,
        adapter: SelectionAdapter,
    ) {
        adapter.submitItems(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showSkeletonUI(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerCartList.startShimmer()
            binding.shimmerCartList.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.shimmerCartList.stopShimmer()
            binding.shimmerCartList.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun navigateToDetail(productId: Long) {
        startActivity(DetailActivity.createIntent(requireContext(), productId))
    }

    override fun onResume() {
        super.onResume()
        viewModel.setLoadingState(true)
        viewModel.setUpCartItems()
    }

    override fun onMakeOrderClick() {
        OrderDatabase.postOrder(viewModel.order.value ?: Order())
        fragmentEventListener.onOrderButtonClicked()
    }

    override fun onSelectAllClick() {
        viewModel.selectAllByCondition()
    }

    fun onShow() {
        viewModel.setUpCartItems()
    }
}

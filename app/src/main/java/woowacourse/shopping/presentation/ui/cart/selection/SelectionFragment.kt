package woowacourse.shopping.presentation.ui.cart.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.databinding.FragmentSelectionBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.cart.recommendation.RecommendationFragment
import woowacourse.shopping.presentation.ui.detail.DetailActivity

class SelectionFragment : Fragment(), SelectionClickListener {
    private lateinit var binding: FragmentSelectionBinding
    private lateinit var adapter: SelectionAdapter
    private val viewModel: SelectionViewModel by lazy {
        val viewModelFactory =
            SelectionViewModelFactory(
                cartRepository = RemoteCartRepositoryImpl(),
                orderDatabase = OrderDatabase,
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

        setUpRecyclerViewAdapter()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCartItems()
    }

    private fun setUpRecyclerViewAdapter() {
        adapter = SelectionAdapter(viewModel, viewModel)
        binding.recyclerView.adapter = adapter
    }

    private fun showData(data: List<CartItem>) {
        adapter.submitList(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun emptyCart() {
        viewModel.isCartEmpty()
        Toast.makeText(requireContext(), getString(R.string.empty_cart_message), Toast.LENGTH_LONG).show()
    }

    private fun observeViewModel() {
        viewModel.cartItemsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Success -> showData(state.data.items)
                is UIState.Empty -> emptyCart()
                is UIState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }

        viewModel.cartItems.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
            adapter.submitList(it.items)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoadingUI(it)
        }

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

    private fun showLoadingUI(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerCartList.startShimmer()
        } else {
            binding.shimmerCartList.stopShimmer()
        }
    }

    override fun onMakeOrderClick() {
        viewModel.postOrder()
        parentFragmentManager.beginTransaction()
            .replace(R.id.cart_fragment, RecommendationFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    override fun onSelectAllClick() {
        viewModel.selectAllByCondition()
    }
}

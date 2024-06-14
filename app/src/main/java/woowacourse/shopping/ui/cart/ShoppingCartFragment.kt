package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.databinding.FragmentCartListBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.cart.adapter.CartItemAdapter

class ShoppingCartFragment : Fragment() {
    private var _binding: FragmentCartListBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("FragmentCartListBinding is not initialized")

    private val factory: UniversalViewModelFactory = ShoppingCartViewModel.factory()

    private val viewModel: ShoppingCartViewModel by lazy {
        ViewModelProvider(this, factory)[ShoppingCartViewModel::class.java]
    }

    private val adapter: CartItemAdapter by lazy {
        CartItemAdapter(
            onCartItemDeleteListener = viewModel,
            onItemQuantityChangeListener = viewModel,
            onCartItemSelectedListener = viewModel,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartListBinding.inflate(inflater)
        initBinding()
        binding.cartList.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation()
        observeDeletedItem()
        observeItemsInCurrentPage()
        observeOrderNavigation()
        observeErrorMessage()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(1000)
            viewModel.loadAll()
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initNavigation() {
        binding.toolbarCartList.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeDeletedItem() {
        viewModel.deletedItemId.observe(viewLifecycleOwner) { productId ->
            viewModel.deleteItem(productId)
        }
    }

    private fun observeItemsInCurrentPage() {
        viewModel.cartItems.observe(viewLifecycleOwner) { products ->
            adapter.updateCartItems(products)
            binding.shimmerCartList.stopShimmer()
        }
    }

    private fun observeOrderNavigation() {
        viewModel.navigationOrderEvent.observe(viewLifecycleOwner) { orderInformation ->
            (requireActivity() as? FragmentNavigator)?.navigateToOrder(orderInformation)
        }
    }

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "ShoppingCartFragment"
    }
}

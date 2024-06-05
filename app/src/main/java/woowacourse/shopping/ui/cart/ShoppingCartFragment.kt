package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.databinding.FragmentCartListBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.cart.event.ShoppingCartEvent

class ShoppingCartFragment : Fragment() {
    private var _binding: FragmentCartListBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("FragmentCartListBinding is not initialized")

    private val viewModel: ShoppingCartViewModel by viewModels {
        ShoppingCartViewModel.factory()
    }

    private val adapter: CartAdapter by lazy {
        CartAdapter(
            onProductItemClickListener = viewModel,
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
        binding.cartList.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = this

        initNavigation()
        showSkeletonUi()
        observeDeletedItem()
        observeItemsInCurrentPage()
        observeOrderNavigation()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(1000)
            viewModel.loadAll()
        }
    }

    private fun showSkeletonUi() {
        binding.shimmerCartList.visibility = View.VISIBLE
        binding.cartList.visibility = View.INVISIBLE
    }

    private fun initNavigation() {
        binding.productDetailToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeDeletedItem() {
        viewModel.deletedItemId.observe(viewLifecycleOwner) { productId ->
            Log.d("fragment", "$productId")
            viewModel.deleteItem(productId)
        }
    }

    private fun observeItemsInCurrentPage() {
        viewModel.cartItems.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            binding.cartList.visibility = View.VISIBLE
            binding.shimmerCartList.stopShimmer()
            binding.shimmerCartList.visibility = View.GONE
        }
    }

    private fun observeOrderNavigation() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ShoppingCartEvent.NavigationOrder -> (requireActivity() as? FragmentNavigator)?.navigateToOrder()

                is ShoppingCartEvent.PopBackStack -> (requireActivity() as? FragmentNavigator)?.popBackStack()
            }
        }


    }

    companion object {
        const val TAG = "ShoppingCartFragment"
    }
}

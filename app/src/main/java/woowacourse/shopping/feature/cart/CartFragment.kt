package woowacourse.shopping.feature.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.cart.adapter.CartAdapter
import woowacourse.shopping.feature.cart.adapter.CartSkeletonAdapter
import woowacourse.shopping.feature.cart.adapter.CartViewHolder

class CartFragment :
    Fragment(),
    CartViewHolder.CartClickListener {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by activityViewModels {
        (requireActivity() as CartActivity).sharedViewModelFactory
    }

    private val cartAdapter: CartAdapter by lazy {
        CartAdapter(
            cartClickListener = this,
            quantityChangeListener =
                object : QuantityChangeListener {
                    override fun onIncrease(cartItem: CartItem) {
                        viewModel.increaseQuantity(cartItem)
                    }

                    override fun onDecrease(cartItem: CartItem) {
                        viewModel.removeCartItemOrDecreaseQuantity(cartItem)
                    }
                },
            onItemCheckedChange = { item, isChecked ->
                viewModel.setItemSelection(item, isChecked)
            },
            isItemChecked = { item -> viewModel.isItemSelected(item) },
        )
    }
    private val cartSkeletonAdapter: CartSkeletonAdapter by lazy {
        CartSkeletonAdapter()
    }

    private val concatAdapter: ConcatAdapter by lazy {
        ConcatAdapter(cartSkeletonAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupBinding()
        setupRecyclerView()
        observeViewModel()
        setupBottomBar()
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupRecyclerView() {
        binding.rvCartItems.adapter = concatAdapter
    }

    private fun setupBottomBar() {
        binding.bottomBar.checkboxAll.setOnCheckedChangeListener(null)
        binding.bottomBar.checkboxAll.setOnCheckedChangeListener { _, isChecked ->
            viewModel.selectAllItems(isChecked)
            cartAdapter.notifyDataSetChanged()
        }

        viewModel.isAllSelected.observe(viewLifecycleOwner) { isAll ->
            binding.bottomBar.checkboxAll.setOnCheckedChangeListener(null)
            binding.bottomBar.checkboxAll.isChecked = isAll
            binding.bottomBar.checkboxAll.setOnCheckedChangeListener { _, checked ->
                viewModel.selectAllItems(checked)
                cartAdapter.notifyDataSetChanged()
            }
            cartAdapter.notifyDataSetChanged()
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

        }
    }

    private fun observeViewModel() {

        viewModel.removeItemEvent.observe(viewLifecycleOwner) { cartItem ->
            onCartItemDelete(cartItem)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                if (concatAdapter.adapters.contains(cartSkeletonAdapter)) {
                    concatAdapter.removeAdapter(cartSkeletonAdapter)
                }
                if (!concatAdapter.adapters.contains(cartAdapter)) {
                    concatAdapter.addAdapter(0, cartAdapter)
                }
            } else {
                if (concatAdapter.adapters.contains(cartAdapter)) {
                    concatAdapter.removeAdapter(cartAdapter)
                }
                if (!concatAdapter.adapters.contains(cartSkeletonAdapter)) {
                    concatAdapter.addAdapter(0, cartSkeletonAdapter)
                }
            }
        }
        binding.bottomBar.orderButton.setOnClickListener {
            (requireActivity() as CartActivity).navigateToRecommend()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCartQuantity()
    }

    override fun onCartItemDelete(cartItem: CartItem) {
        val deletedIndex: Int? = viewModel.getPosition(cartItem)
        deletedIndex?.let { cartAdapter.removeItem(it) }
        viewModel.delete(cartItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package woowacourse.shopping.feature.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
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

    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(CartRepositoryImpl(CartRemoteDataSourceImpl()))
    }

    private val cartAdapter: CartAdapter by lazy {
        CartAdapter(
            this,
            quantityChangeListener =
                object : QuantityChangeListener {
                    override fun onIncrease(cartItem: CartItem) {
                        viewModel.increaseQuantity(cartItem)
                    }

                    override fun onDecrease(cartItem: CartItem) {
                        viewModel.removeCartItemOrDecreaseQuantity(cartItem)
                    }
                },
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
        binding.bottomBar.orderButton.setOnClickListener {
            (requireActivity() as CartActivity).navigateToRecommend()
        }

        binding.bottomBar.checkboxAll.setOnCheckedChangeListener { _, isChecked ->
            viewModel.selectAllItems(isChecked)
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            val formattedPrice =
                java.text.NumberFormat
                    .getNumberInstance(java.util.Locale.KOREA)
                    .format(totalPrice)
            binding.bottomBar.totalPrice.text = "총 ${formattedPrice}원"
        }

        viewModel.selectedItemCount.observe(viewLifecycleOwner) { count ->
            binding.bottomBar.orderButton.text = "주문하기($count)"
        }

        viewModel.isAllSelected.observe(viewLifecycleOwner) { isAllSelected ->
            binding.bottomBar.checkboxAll.isChecked = isAllSelected
        }
    }

    private fun observeViewModel() {
        viewModel.loginErrorEvent.observe(viewLifecycleOwner) { result ->
            when (result) {
                CartFetchError.Network -> Toast.makeText(requireContext(), "네트워크 에러 발생", Toast.LENGTH_SHORT).show()
                is CartFetchError.Server -> Toast.makeText(requireContext(), "로그인 실패", Toast.LENGTH_SHORT).show()
            }
            requireActivity().finish()
        }

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

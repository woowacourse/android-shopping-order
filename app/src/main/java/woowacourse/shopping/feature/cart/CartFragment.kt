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
        // 전체선택 체크박스 누를 때
        binding.bottomBar.checkboxAll.setOnCheckedChangeListener(null)
        binding.bottomBar.checkboxAll.setOnCheckedChangeListener { _, isChecked ->
            viewModel.selectAllItems(isChecked)
            cartAdapter.notifyDataSetChanged() // ① 전체선택 후 즉시 개별 체크박스 갱신
        }

        // 뷰모델이 isAllSelected 변경될 때
        viewModel.isAllSelected.observe(viewLifecycleOwner) { isAll ->
            binding.bottomBar.checkboxAll.setOnCheckedChangeListener(null)
            binding.bottomBar.checkboxAll.isChecked = isAll
            binding.bottomBar.checkboxAll.setOnCheckedChangeListener { _, checked ->
                viewModel.selectAllItems(checked)
                cartAdapter.notifyDataSetChanged() // ② “전체선택” 에서 해제 시에도 반영
            }
            cartAdapter.notifyDataSetChanged() // ③ 개별 해제 시 전체선택 해제되고 UI 갱신
        }
    }

    private fun observeViewModel() {
        viewModel.loginErrorEvent.observe(viewLifecycleOwner) { result ->
            when (result) {
                CartFetchError.Network ->
                    Toast
                        .makeText(
                            requireContext(),
                            "네트워크 에러 발생",
                            Toast.LENGTH_SHORT,
                        ).show()

                is CartFetchError.Server ->
                    Toast
                        .makeText(
                            requireContext(),
                            "로그인 실패",
                            Toast.LENGTH_SHORT,
                        ).show()
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

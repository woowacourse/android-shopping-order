package woowacourse.shopping.feature.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.cart.adapter.CartAdapter
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
        ).apply {
            showSkeleton()
        }
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
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupRecyclerView() {
        binding.rvCartItems.adapter = cartAdapter
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
        binding.bottomBar.orderButton.setOnClickListener {
            (requireActivity() as CartActivity).navigateToRecommend()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCartItemDelete(cartItem: CartItem) {
        viewModel.delete(cartItem) {
            cartAdapter.removeItem(cartItem)
        }
    }

    override fun onCartItemChecked(
        cartItem: CartItem,
        changeCheckValue: Boolean,
    ) {
        viewModel.updateCartItemCheck(cartItem, changeCheckValue)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

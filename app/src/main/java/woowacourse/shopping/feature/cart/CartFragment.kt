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

class CartFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by activityViewModels {
        (requireActivity() as CartActivity).sharedViewModelFactory
    }

    private val cartAdapter: CartAdapter by lazy {
        CartAdapter(
            viewModel = viewModel,
            quantityChangeListener =
                object : QuantityChangeListener {
                    override fun onIncrease(cartItem: CartItem) {
                        viewModel.increaseCartItemQuantity(cartItem)
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
                is CartFetchError.Network -> extracted("네트워크 에러 발생")
                is CartFetchError.Server -> extracted("로그인 실패")
                is CartFetchError.Local -> extracted("로컬 저장소 에러 발생")
            }
            requireActivity().finish()
        }

        binding.bottomBar.orderButton.setOnClickListener {
            (requireActivity() as CartActivity).navigateToRecommend()
        }
    }

    private fun extracted(message: String) {
        Toast
            .makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT,
            ).show()
    }

    override fun onResume() {
        super.onResume()

        binding.rvCartItems.post {
            if (binding.rvCartItems.adapter != null) {
                binding.rvCartItems.scrollToPosition(0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

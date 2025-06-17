package woowacourse.shopping.feature.cart.cartdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.cart.CartViewModel
import woowacourse.shopping.feature.cart.cartdetail.adapter.CartAdapter

class CartFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by activityViewModels {
        (requireActivity() as CartActivity).sharedViewModelFactory
    }

    private val cartAdapter: CartAdapter by lazy {
        val cartHandler = CartItemHandler(viewModel)
        CartAdapter(
            cartItemClickHandler = cartHandler,
            quantityChangeListener = cartHandler,
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

        viewModel.updateAppBarTitle(getString(R.string.cart_action_bar_name))
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
            showErrorMessage("네트워크 에러 발생")
            requireActivity().finish()
        }

        binding.bottomBar.orderButton.setOnClickListener {
            (requireActivity() as CartActivity).navigateToRecommend()
        }
    }

    private fun showErrorMessage(message: String) {
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

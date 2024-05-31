package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.ui.cart.adapter.CartAdapter

class CartSelectionFragment(val viewModel: CartViewModel) : Fragment() {
    private lateinit var binding: FragmentCartSelectionBinding
    private val adapter by lazy { CartAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCartSelectionBinding.inflate(inflater, container, false)
        binding.rvCart.itemAnimator = null
        binding.rvCart.adapter = adapter

        viewModel.cartUiState.observe(viewLifecycleOwner) {
            val cartUiState = it.getContentIfNotHandled() ?: return@observe
            when (cartUiState) {
                CartUiState.Failure -> {}

                CartUiState.Loading -> {
                    binding.layoutCartSkeleton.visibility = View.VISIBLE
                    binding.rvCart.visibility = View.GONE
                    binding.tvEmptyCart.visibility = View.GONE
                }

                CartUiState.Empty -> {
                    binding.layoutCartSkeleton.visibility = View.GONE
                    binding.rvCart.visibility = View.GONE
                    binding.tvEmptyCart.visibility = View.VISIBLE
                }

                is CartUiState.Success -> {
                    binding.layoutCartSkeleton.visibility = View.GONE
                    binding.rvCart.visibility = View.VISIBLE
                    binding.tvEmptyCart.visibility = View.GONE
                    adapter.submitList(cartUiState.cartUiModels)
                }
            }
        }
        return binding.root
    }
}

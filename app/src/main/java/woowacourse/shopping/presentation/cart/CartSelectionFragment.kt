package woowacourse.shopping.presentation.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.presentation.cart.adapter.CartAdapter

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

        viewModel.cartUiState.observe(viewLifecycleOwner) { cartUiState ->
            when {
                cartUiState.isFailure -> {
                }

                cartUiState.isLoading -> {
                    binding.layoutCartSkeleton.visibility = View.VISIBLE
                    binding.rvCart.visibility = View.GONE
                }

                cartUiState.isSuccess -> {
                    binding.layoutCartSkeleton.visibility = View.GONE
                    binding.rvCart.visibility = View.VISIBLE
                    adapter.submitList(cartUiState.cartUiModels)
                }
            }
        }
        return binding.root
    }
}

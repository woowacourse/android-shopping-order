package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.ui.cart.adapter.CartAdapter

class CartSelectionFragment(val viewModel: CartViewModel) : Fragment() {
    private var _binding: FragmentCartSelectionBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { CartAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartSelectionBinding.inflate(inflater, container, false)
        binding.rvCart.itemAnimator = null
        binding.rvCart.adapter = adapter

        viewModel.cartUiModels.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.layoutCartSkeleton.visibility = View.GONE
                binding.rvCart.visibility = View.GONE
                binding.tvEmptyCart.visibility = View.VISIBLE
                return@observe
            }

            binding.layoutCartSkeleton.visibility = View.GONE
            binding.rvCart.visibility = View.VISIBLE
            binding.tvEmptyCart.visibility = View.GONE
            adapter.submitList(it)
        }

        viewModel.cartLoadingEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled() ?: return@observe
            binding.layoutCartSkeleton.visibility = View.VISIBLE
            binding.rvCart.visibility = View.GONE
            binding.tvEmptyCart.visibility = View.GONE
        }

        viewModel.cartErrorEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled() ?: return@observe
            showToastCartFailure()
        }
        return binding.root
    }

    private fun showToastCartFailure() {
        Toast.makeText(requireContext(), R.string.common_error_retry, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

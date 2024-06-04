package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.ui.cart.adapter.CartAdapter

class CartSelectionFragment : Fragment() {
    private var _binding: FragmentCartSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by activityViewModels()

    private val adapter by lazy { CartAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartSelectionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.loadAllCartItems()
        viewModel.navigateCartSelection()
        initializeView()

        return binding.root
    }

    private fun initializeView() {
        initializeCartList()
        observeData()
    }

    private fun initializeCartList() {
        binding.rvCart.itemAnimator = null
        binding.rvCart.adapter = adapter
    }

    private fun observeData() {
        viewModel.cartUiModels.observe(viewLifecycleOwner) {
            adapter.submitList(it.uiModels)
        }

        viewModel.cartErrorEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled() ?: return@observe
            showToastCartFailure()
        }
    }

    private fun showToastCartFailure() {
        Toast.makeText(requireContext(), R.string.common_error_retry, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package woowacourse.shopping.view.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.view.home.adapter.product.ProductAdapter
import woowacourse.shopping.view.state.UiState

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding: FragmentRecommendBinding
        get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductAdapter(viewModel, viewModel)
        binding.rvRecommend.adapter = adapter

        viewModel.recommendedProducts.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> adapter.submitProductItems(state.data, false)
                is UiState.Loading -> return@observe
                is UiState.Error -> showError(state.exception.message ?: getString(R.string.unknown_error))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}
package woowacourse.shopping.presentation.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentCartRecommendBinding
import woowacourse.shopping.presentation.detail.ProductDetailActivity

class CartRecommendFragment(val viewModel: CartViewModel) : Fragment() {
    private var _binding: FragmentCartRecommendBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        // ProductsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCartRecommendBinding.inflate(inflater, container, false)

        // viewModel.loadRecommendProductUiModels()
        initializeView()
        return binding.root
    }

    private fun initializeView() {
        binding.rvRecommendProduct.itemAnimator = null
        // binding.rvRecommendProduct.adapter = adapter
        viewModel.recommendProductUiModels.observe(viewLifecycleOwner) {
            // adapter.updateProducts(it)
        }
    }

    private fun navigateToProductDetailView(productId: Int) {
        val intent = ProductDetailActivity.newIntent(requireContext(), productId)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentCartRecommendBinding
import woowacourse.shopping.ui.cart.adapter.RecommendProductsAdapter

class CartRecommendFragment(val viewModel: CartViewModel) : Fragment() {
    private var _binding: FragmentCartRecommendBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        RecommendProductsAdapter(
            onClickProductItem = { },
            onIncreaseProductQuantity = { viewModel.increaseQuantity(it) },
            onDecreaseProductQuantity = { viewModel.decreaseQuantity(it) },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartRecommendBinding.inflate(inflater, container, false)
        viewModel.loadRecommendProductUiModels()
        initializeView()
        return binding.root
    }

    private fun initializeView() {
        binding.rvRecommendProduct.itemAnimator = null
        binding.rvRecommendProduct.adapter = adapter
        viewModel.recommendProductUiModels.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.databinding.FragmentCartRecommendBinding
import woowacourse.shopping.ui.cart.adapter.RecommendProductsAdapter

class CartRecommendFragment : Fragment() {
    private var _binding: FragmentCartRecommendBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by activityViewModels()

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

        viewModel.loadRecommendProducts()
        viewModel.navigateCartRecommend()
        initializeView()

        return binding.root
    }

    private fun initializeView() {
        initializeRecommendProductList()
        observeData()
    }

    private fun initializeRecommendProductList() {
        binding.rvRecommendProduct.itemAnimator = null
        binding.rvRecommendProduct.adapter = adapter
    }

    private fun observeData() {
        viewModel.recommendProductUiModels.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

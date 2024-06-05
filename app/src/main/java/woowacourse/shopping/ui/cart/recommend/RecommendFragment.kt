package woowacourse.shopping.ui.cart.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendProductBinding
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.cart.viewmodel.CartViewModelFactory
import woowacourse.shopping.ui.products.toUiModel

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendProductBinding? = null
    val binding: FragmentRecommendProductBinding
        get() = requireNotNull(_binding) { "${this::class.java.simpleName}에서 에러가 발생했습니다." }

    private lateinit var recommendProductAdapter: RecommendProductAdapter

    private val viewModel: CartViewModel by activityViewModels {
        CartViewModelFactory(
            ProductRepositoryImpl(),
            CartRepositoryImpl(),
            RecentProductRepositoryImpl.get(RecentProductDatabase.database().recentProductDao()),
            OrderRepositoryImpl(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRecommendProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setRecommendProductAdapter()
        initSetting()
        setRecommendProduct()
    }

    private fun initSetting() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.isRecommendPage.value = true
    }

    private fun setRecommendProduct() {
        viewModel.loadRecommendProducts()
        viewModel.products.observe(viewLifecycleOwner) {
            recommendProductAdapter.submitList(it.map { it.toUiModel() })
        }
    }

    private fun setRecommendProductAdapter() {
        binding.rvRecommendProduct.itemAnimator = null
        recommendProductAdapter = RecommendProductAdapter(viewModel)
        binding.rvRecommendProduct.adapter = recommendProductAdapter
    }
}

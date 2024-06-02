package woowacourse.shopping.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.UniversalViewModelFactory
import woowacourse.shopping.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException()

    private lateinit var factory: UniversalViewModelFactory
    private lateinit var viewModel: OrderViewModel

    private val recommendProductsAdapter: RecommendProductAdapter by lazy { RecommendProductAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            factory =
                OrderViewModel.factory(
                    (it.getSerializable(ORDER_ITEM_ID) as LongArray).toList(),
                )
        }
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRecommendedProducts()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initRecommendProductsAdapter()
        return binding.root
    }

    private fun initRecommendProductsAdapter() {
        binding.rvOrderRecommendProducts.adapter = recommendProductsAdapter
        viewModel.recommendedProducts.observe(viewLifecycleOwner) { recommendProducts ->
            recommendProductsAdapter.updateRecommendProducts(recommendProducts)
        }
    }

    companion object {
        const val ORDER_ITEM_ID = "OrderItemId"
        const val TAG = "OrderFragment"
    }
}

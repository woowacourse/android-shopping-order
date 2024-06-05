package woowacourse.shopping.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ui.util.UniversalViewModelFactory
import woowacourse.shopping.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException()

    private lateinit var factory: UniversalViewModelFactory
    private lateinit var viewModel: OrderViewModel

    private val recommendedProductsAdapter: RecommendedProductsAdapter by lazy {
        RecommendedProductsAdapter(
            viewModel,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            factory = OrderViewModel.factory()
        }
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.rvOrderRecommendedProducts.adapter = recommendedProductsAdapter

        viewModel.recommendedProducts.observe(viewLifecycleOwner) {
            Log.d(TAG, "submitList in adapter $it")
            recommendedProductsAdapter.submitList(it)
        }

        return binding.root
    }

    companion object {
        const val ORDER_ITEMS_ID = "OrderItemId"
        const val TAG = "OrderFragment"
    }
}

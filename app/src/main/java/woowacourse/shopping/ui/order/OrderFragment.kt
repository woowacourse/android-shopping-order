package woowacourse.shopping.ui.order

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import woowacourse.shopping.R
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.databinding.FragmentOrderBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.model.OrderInformation
import woowacourse.shopping.ui.order.adapter.RecommendProductAdapter
import java.io.Serializable

class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException()

    private lateinit var factory: UniversalViewModelFactory
    private lateinit var viewModel: OrderViewModel

    private val recommendProductsAdapter: RecommendProductAdapter by lazy { RecommendProductAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater)
        initBinding()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRecommendProductsAdapter()
        observeNavigationPaymentEvent()
        observeErrorMessage()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRecommendedProducts()
    }

    private fun initViewModel() {
        fetchOrderInformation()
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
    }

    private fun fetchOrderInformation() {
        arguments?.let { bundle ->
            val orderInformation =
                bundle.bundleSerializable(ORDER_INFORMATION, OrderInformation::class.java)
                    ?: throw NoSuchElementException()
            factory = OrderViewModel.factory(orderInformation)
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initToolbar() {
        binding.toolbarOrder.setNavigationOnClickListener {
            (requireActivity() as FragmentNavigator).popBackStack()
        }
    }

    private fun initRecommendProductsAdapter() {
        binding.rvOrderRecommendProducts.adapter = recommendProductsAdapter
        viewModel.recommendProducts.observe(viewLifecycleOwner) { recommendProducts ->
            recommendProductsAdapter.updateRecommendProducts(recommendProducts)
        }
    }

    private fun observeNavigationPaymentEvent() {
        viewModel.navigationPaymentEvent.observe(viewLifecycleOwner) { orderInformation ->
            (requireActivity() as FragmentNavigator).navigateToPayment(orderInformation)
        }
    }

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun <T : Serializable> Bundle.bundleSerializable(
        key: String,
        clazz: Class<T>,
    ): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, clazz)
        } else {
            getSerializable(key) as? T
        }
    }

    companion object {
        const val ORDER_INFORMATION = "OrderInformation"
        const val TAG = "OrderFragment"
    }
}

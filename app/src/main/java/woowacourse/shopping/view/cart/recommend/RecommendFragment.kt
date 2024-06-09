package woowacourse.shopping.view.cart.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.view.cart.CartViewModel

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding: FragmentRecommendBinding
        get() = _binding!!

    private val viewModel by activityViewModels<CartViewModel>()
    private val adapter: RecommendProductAdapter by lazy {
        RecommendProductAdapter(viewModel, viewModel)
    }

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
        initializeBindingVariables()
        observeState()
    }

    private fun initializeBindingVariables() {
        binding.lifecycleOwner = this
        binding.adapter = adapter
    }

    private fun observeState() {
        viewModel.recommendedListUiState.observe(viewLifecycleOwner) { state ->
            adapter.loadData(state.recommendedProducts)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

package woowacourse.shopping.feature.cart.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.cart.CartViewModel
import woowacourse.shopping.feature.cart.adapter.RecommendAdapter

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by activityViewModels {
        (requireActivity() as CartActivity).sharedViewModelFactory
    }

    val recommendAdapter by lazy {
        RecommendAdapter(
            lifecycleOwner = viewLifecycleOwner,
            object : QuantityChangeListener {
                override fun onIncrease(cartItem: CartItem) {
                    viewModel.addCartItemOrIncreaseQuantityFromRecommend(cartItem)
                }

                override fun onDecrease(cartItem: CartItem) {
                    viewModel.removeCartItemOrDecreaseQuantityFromRecommend(cartItem)
                }
            },
        ).apply { showSkeleton() }
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

        binding.rvRecommendItems.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendAdapter
        }

        viewModel.recommendedGoods.observe(viewLifecycleOwner) { goodsList ->
            recommendAdapter.setRecommendItem(goodsList)
        }

        viewModel.loadRecommendedGoods()
        setupBinding()
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.bottomBar.checkboxAll.visibility = View.GONE
        binding.bottomBar.tvAll.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

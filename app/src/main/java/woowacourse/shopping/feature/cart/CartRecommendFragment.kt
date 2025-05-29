package woowacourse.shopping.feature.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartRecommendBinding
import woowacourse.shopping.feature.cart.adapter.RecommendAdapter
import kotlin.getValue

class CartRecommendFragment : Fragment() {
    private lateinit var binding: FragmentCartRecommendBinding
    private val viewModel: CartViewModel by activityViewModels<CartViewModel>()
    private val adapter: RecommendAdapter = RecommendAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_cart_recommend, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.rvRecommend.adapter = adapter
        binding.rvRecommend.layoutManager =
            LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false,
            )
        return binding.root
    }
}

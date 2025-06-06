package woowacourse.shopping.feature.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.databinding.FragmentCartRecommendBinding
import woowacourse.shopping.feature.cart.adapter.RecommendAdapter
import woowacourse.shopping.feature.payment.PaymentActivity
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
        binding = FragmentCartRecommendBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.rvRecommend.adapter = adapter
        binding.rvRecommend.layoutManager =
            LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false,
            )

        binding.tvOrderButton.setOnClickListener {
            Log.d("CartRecommendFragment", "order button clicked")
            navigate()
        }
        return binding.root
    }

    private fun navigate() {
        val intent = PaymentActivity.newIntent(this.requireActivity())
        startActivity(intent)
    }
}

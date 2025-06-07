package woowacourse.shopping.feature.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.databinding.FragmentCartRecommendBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.feature.cart.adapter.RecommendAdapter
import woowacourse.shopping.feature.cart.adapter.RecommendClickListener
import woowacourse.shopping.feature.payment.PaymentActivity
import kotlin.getValue

class CartRecommendFragment : Fragment() {
    private lateinit var binding: FragmentCartRecommendBinding
    private val viewModel: CartViewModel by activityViewModels<CartViewModel>()
    private lateinit var adapter: RecommendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCartRecommendBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupAdapter()
        binding.rvRecommend.adapter = adapter
        binding.rvRecommend.layoutManager =
            LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false,
            )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.orderItems.observe(this) { orderIds ->
            navigate(orderIds.toLongArray())
        }
    }

    private fun setupAdapter() {
        adapter =
            RecommendAdapter(
                object : RecommendClickListener {
                    override fun insertToCart(cart: CartProduct) {
                        viewModel.addToRecommendCart(cart)
                    }

                    override fun removeFromCart(cart: CartProduct) {
                        viewModel.removeFromRecommendCart(cart)
                    }
                },
            )
    }

    private fun navigate(orderIds: LongArray) {
        val intent = PaymentActivity.newIntent(this.requireActivity(), orderIds)
        startActivity(intent)
    }
}

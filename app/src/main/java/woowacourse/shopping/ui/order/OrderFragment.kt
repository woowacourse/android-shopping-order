package woowacourse.shopping.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.UniversalViewModelFactory

class OrderFragment : Fragment() {

    private lateinit var factory: UniversalViewModelFactory
    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            factory = OrderViewModel.factory(
                (it.getSerializable(ORDER_ITEM_ID) as LongArray).toList(),
                ShoppingApp.orderSource
            ) // arguemtn 가져오기
        }
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    companion object {
        const val ORDER_ITEM_ID = "OrderItemId"
        const val TAG = "OrderFragment"
    }
}

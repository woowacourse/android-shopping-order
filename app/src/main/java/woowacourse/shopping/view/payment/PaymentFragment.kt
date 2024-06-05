package woowacourse.shopping.view.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.RecentlyProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.RemoteCouponRepositoryImpl
import woowacourse.shopping.data.repository.remote.RemoteProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.databinding.FragmentProductListBinding
import woowacourse.shopping.view.MainActivityListener
import woowacourse.shopping.view.ViewModelFactory
import woowacourse.shopping.view.products.ProductListViewModel

class PaymentFragment : Fragment(), OnclickNavigatePayment {
    private var mainActivityListener: MainActivityListener? = null
    private var _binding: FragmentPaymentBinding? = null
    val binding: FragmentPaymentBinding get() = _binding!!
    private val paymentViewModel: PaymentViewModel by lazy {
        val viewModelFactory =
            ViewModelFactory {
                PaymentViewModel(
                    couponRepository = RemoteCouponRepositoryImpl(),
                )
            }
        viewModelFactory.create(PaymentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun clickBack() {
        TODO("Not yet implemented")
    }
}

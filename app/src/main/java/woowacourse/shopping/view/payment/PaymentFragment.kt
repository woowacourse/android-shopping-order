package woowacourse.shopping.view.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.view.MainActivityListener

class PaymentFragment : Fragment(), OnclickNavigatePayment {
    private var mainActivityListener: MainActivityListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.common.utils.Toaster
import woowacourse.shopping.data.member.MemberRemoteDataSourceRetrofit
import woowacourse.shopping.data.member.MemberRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.ui.model.OrderModel
import woowacourse.shopping.ui.order.OrderProductAdapter

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var presenter: OrderDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initPresenter()

        setupView()
    }

    private fun initBinding() {
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
    }

    private fun initPresenter() {
        val memberRepository = MemberRepositoryImpl(MemberRemoteDataSourceRetrofit())
        presenter = OrderDetailPresenter(this, memberRepository)
    }

    private fun setupView() {
        val id = intent.getIntExtra(EXTRA_KEY_ID, 0)
        presenter.loadDetail(id)
    }

    override fun showDetail(order: OrderModel) {
        binding.rvOrderProduct.adapter = OrderProductAdapter(order.products)
        binding.order = order
    }

    override fun notifyLoadFailed() {
        runOnUiThread {
            Toaster.showToast(this, "주문을 불러오는데 실패했습니다!")
        }
    }

    companion object {

        private const val EXTRA_KEY_ID = "id"
        fun createIntent(context: Context, id: Int) : Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(EXTRA_KEY_ID, id)
            return intent
        }
    }
}
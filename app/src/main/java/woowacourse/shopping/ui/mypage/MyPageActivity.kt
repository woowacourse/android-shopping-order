package woowacourse.shopping.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityMyPageBinding
import woowacourse.shopping.utils.UserData

class MyPageActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMyPageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initUserInfo()
    }

    private fun initUserInfo() {
        binding.tvUserId.text = UserData.id
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MyPageActivity::class.java)
            context.startActivity(intent)
        }
    }
}

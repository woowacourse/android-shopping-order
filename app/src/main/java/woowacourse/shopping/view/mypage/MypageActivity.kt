package woowacourse.shopping.view.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class MypageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MypageActivity::class.java)
        }
    }
}

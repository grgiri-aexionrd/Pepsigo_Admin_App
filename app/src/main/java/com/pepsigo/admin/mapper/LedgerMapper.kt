package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.TransactionDetail
import com.pepsigo.admin.repository.TransactionDetailUi
import com.pepsigo.admin.utils.safeAmount
import com.pepsigo.admin.utils.safeDate
import com.pepsigo.admin.utils.safeText

@RequiresApi(Build.VERSION_CODES.O)
fun TransactionDetail.toUi(): TransactionDetailUi {
    return TransactionDetailUi(
        type = type,
        date = date.safeDate(),
        ref = ref.safeText(),
        amount = amount.safeAmount()
    )

}
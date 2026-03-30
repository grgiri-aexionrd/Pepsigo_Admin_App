package com.pepsigo.admin.utils

import android.content.Context
import android.content.Intent



fun shareInvoicePrint(context: Context, invoiceText: String) {
    val sendIntent : Intent = Intent().apply{
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, invoiceText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent,  "Invoice")
    context.startActivity(shareIntent)
}



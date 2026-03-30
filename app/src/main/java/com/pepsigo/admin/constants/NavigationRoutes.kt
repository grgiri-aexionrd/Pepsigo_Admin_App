package com.pepsigo.admin.constants

object Routes {
    const val Reports = "reports"
    const val SalesRegister = "sales_register"
    const val PurchaseRegister = "purchase_register"
    const val OutstandingDues = "outstanding_dues"
    const val StockSummary = "stock_summary"
    const val BatchStock = "batch_stock"
    const val Ledger = "ledger"

    // Added routes for missing reports
    const val ItemWiseDelivery = "item_wise_delivery"
    const val ItemWiseSales = "item_wise_sales"
    const val PaymentSummary = "payment_summary"

    // Payment detail screen
    const val PaymentDetail = "payment_detail/{paymentId}"
    fun paymentDetailRoute(paymentId: Int) = "payment_detail/$paymentId"

    // Make payment screen
//    const val MakePayment = "make_payment"

    fun makePaymentRoute(
        saleId: Int? = null,
        purchaseId: Int? = null,
        customerId: Int?=null,
        amount: Double? =null
    ): String {
        return "make_payment?" +
                "saleId=${saleId ?: ""}" +
                "&purchaseId=${purchaseId ?: ""}" +
                "&customerId=${customerId ?: ""}" +
                "&amount=${amount ?: ""}"
    }
    const val MAKE_PAYMENT_ROUTE =
        "make_payment?" +
                "saleId={saleId}" +
                "&purchaseId={purchaseId}" +
                "&customerId={customerId}" +
                "&amount={amount}"

    const val MAKE_SALE = "sale"
    const val ADD_PRODUCT = "add_product"
    const val BATCH_SELECTION = "batch_selection"
    const val SALE_SUMMARY = "sale_summary"
    const val SALE_SUCCESS = "sale_success"

    const val PRINT_INVOICE = "print_invoice/{saleId}"
    fun printInvoiceRoute(saleId: Int) = "print_invoice/$saleId"
}



package com.pepsigo.admin.utils

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class TextPrintAdapter(
    private val context: Context,
    private val text: String
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder("invoice.txt")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()

        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback
    ) {
        try {
            if (cancellationSignal.isCanceled) {
                callback.onWriteCancelled()
                return
            }

            FileOutputStream(destination.fileDescriptor).use { output ->
                OutputStreamWriter(output, Charsets.UTF_8).use { writer ->
                    writer.write(text)
                    writer.flush()
                }
            }

            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

        } catch (e: Exception) {
            e.printStackTrace()
            callback.onWriteFailed(e.localizedMessage)
        }
    }
}

fun printInvoice(context: Context, invoiceText: String) {

    val cleanText = invoiceText
        .replace("\uFEFF", "")          // remove BOM
        .replace("\r", "")              // windows CR
        .replace(Regex("[^\\x09\\x0A\\x0D\\x20-\\x7E]"), "") // remove control chars

    val printManager =
        context.getSystemService(Context.PRINT_SERVICE) as android.print.PrintManager

    val attributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)   // ✅ A4
        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        .build()

    val jobName = "Invoice Print"

    printManager.print(
        jobName,
        TextPrintAdapter(context, cleanText),
        attributes
    )
}
package com.pepsigo.admin.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class PdfPrintAdapter(
    private val context: Context,
    private val uri: Uri
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        val info = PrintDocumentInfo.Builder("invoice.pdf")
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
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destination.fileDescriptor).use { output ->
                    input.copyTo(output)
                }
            }

            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

        } catch (e: Exception) {
            callback.onWriteFailed(e.message)
        }
    }
}

fun createInvoicePdf(context: Context, text: String): File {

    val pdf = PdfDocument()

    val pageInfo = PdfDocument.PageInfo.Builder(
        595,  // A4 width (points)
        842,  // A4 height
        1
    ).create()

    val page = pdf.startPage(pageInfo)

    val canvas = page.canvas

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 10f
        typeface = Typeface.MONOSPACE
    }

    val startX = 40f
    var startY = 50f
    val lineHeight = 14f

    text.split("\n").forEach { line ->
        canvas.drawText(line, startX, startY, paint)
        startY += lineHeight
    }

    pdf.finishPage(page)

    val file = File(context.cacheDir, "invoice.pdf")
    pdf.writeTo(FileOutputStream(file))
    pdf.close()

    return file
}

fun printInvoicePdf(context: Context, pdfFile: File) {

    val printManager =
        context.getSystemService(Context.PRINT_SERVICE) as PrintManager

    val uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        pdfFile
    )

    val adapter = PdfPrintAdapter(context, uri)

    printManager.print(
        "Invoice",
        adapter,
        PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .build()
    )
}
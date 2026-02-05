package com.gromber05.peco.utils

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.gromber05.peco.model.data.Animal
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generatePdf(context: Context, animals: List<Animal>): File {
    val pdf = PdfDocument()

    val pageWidth = 595
    val pageHeight = 842
    val marginX = 40f
    val marginTop = 50f
    val marginBottom = 50f

    val titlePaint = Paint().apply {
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val subtitlePaint = Paint().apply {
        textSize = 12f
    }

    val headerPaint = Paint().apply {
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val rowPaint = Paint().apply {
        textSize = 12f
    }

    val linePaint = Paint().apply {
        strokeWidth = 1f
    }

    val rowHeight = 20f
    val headerHeight = 24f

    var pageNumber = 1
    var y = 0f

    fun startNewPage(): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        y = marginTop
        canvas.drawText("Informe de animales", marginX, y, titlePaint)

        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        y += 18f
        canvas.drawText("Generado: $dateStr", marginX, y, subtitlePaint)

        y += 28f
        val colNameX = marginX
        val colDobX = marginX + 260f
        val colStateX = marginX + 400f

        canvas.drawText("Nombre", colNameX, y, headerPaint)
        canvas.drawText("Nacimiento/Edad", colDobX, y, headerPaint)
        canvas.drawText("Estado", colStateX, y, headerPaint)

        val lineY = y + 6f
        canvas.drawLine(marginX, lineY, pageWidth - marginX, lineY, linePaint)

        y += headerHeight

        return page
    }

    fun finishPage(page: PdfDocument.Page) {
        val canvas = page.canvas
        val footerText = "Página $pageNumber"
        canvas.drawText(footerText, marginX, pageHeight - marginBottom + 20f, subtitlePaint)
        pdf.finishPage(page)
        pageNumber++
    }

    if (animals.isEmpty()) {
        val page = startNewPage()
        val canvas = page.canvas
        canvas.drawText("No hay animales para mostrar.", marginX, y + 10f, rowPaint)
        finishPage(page)

        val file = File(context.cacheDir, "animales.pdf")
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        return file
    }

    var page = startNewPage()
    val canvas = page.canvas

    val colNameX = marginX
    val colDobX = marginX + 260f
    val colStateX = marginX + 400f

    for (animal in animals) {
        if (y + rowHeight > pageHeight - marginBottom) {
            finishPage(page)
            page = startNewPage()
        }

        val c = page.canvas
        c.drawText(safe(animal.name), colNameX, y, rowPaint)
        c.drawText(safe(animal.dob.toString()), colDobX, y, rowPaint)
        c.drawText(safe(animal.adoptionState.toString()), colStateX, y, rowPaint)

        y += rowHeight
    }

    if (y + 40f > pageHeight - marginBottom) {
        finishPage(page)
        page = startNewPage()
    }
    val c2 = page.canvas
    c2.drawLine(marginX, y + 8f, pageWidth - marginX, y + 8f, linePaint)
    y += 28f
    c2.drawText("Total animales: ${animals.size}", marginX, y, headerPaint)

    finishPage(page)

    val file = File(context.cacheDir, "animales.pdf")
    FileOutputStream(file).use { pdf.writeTo(it) }
    pdf.close()

    return file
}

private fun safe(s: String?): String = s?.take(40) ?: "-"

fun openPdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Abrir PDF"))
}

fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
}
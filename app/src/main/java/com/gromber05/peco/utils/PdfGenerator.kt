package com.gromber05.peco.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.gromber05.peco.model.data.Animal
import java.io.File
import java.io.FileOutputStream

class AnimalPdfGenerator {
    fun generate(context: Context, animal: Animal): File {
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdf.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()

        paint.textSize = 20f
        canvas.drawText("Informe del animal", 40f, 50f, paint)

        paint.textSize = 14f
        canvas.drawText("Nombre: ${animal.name}", 40f, 100f, paint)
        canvas.drawText("Edad: ${animal.dob}", 40f, 130f, paint)
        canvas.drawText("Estado: ${animal.adoptionState}", 40f, 160f, paint)

        pdf.finishPage(page)

        val file = File(context.cacheDir, "animal_${animal.uid}.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        return file
    }
}

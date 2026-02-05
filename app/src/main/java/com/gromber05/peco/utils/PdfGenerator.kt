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

/**
 * Genera un PDF con un informe tabular de animales y lo guarda en la caché de la app.
 *
 * El documento se crea con [PdfDocument] (API nativa Android) y contiene:
 * - Cabecera con título y fecha de generación.
 * - Tabla con columnas: Nombre, Nacimiento/Edad, Estado.
 * - Paginación automática cuando se supera el alto disponible.
 * - Pie de página con el número de página.
 * - Resumen final con el total de animales.
 *
 * Si la lista está vacía, se genera igualmente un PDF con un mensaje indicando
 * que no hay datos para mostrar.
 *
 * El archivo se guarda en `context.cacheDir` con el nombre `animales.pdf`.
 *
 * @param context Contexto Android necesario para acceder a `cacheDir`.
 * @param animals Lista de [Animal] a incluir en el informe.
 * @return Archivo PDF generado en la caché (`animales.pdf`).
 */
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

    /**
     * Crea una nueva página del PDF y dibuja:
     * - Título del informe.
     * - Fecha/hora de generación.
     * - Cabecera de la tabla (Nombre, Nacimiento/Edad, Estado) y una línea separadora.
     *
     * También reinicia la coordenada vertical `y` al margen superior para comenzar a dibujar filas.
     *
     * @return Página iniciada mediante [PdfDocument.startPage].
     */
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

    /**
     * Finaliza una página del PDF añadiendo el pie de página y llamando a [PdfDocument.finishPage].
     *
     * Tras finalizar, incrementa el contador de página para la siguiente.
     *
     * @param page Página actual a finalizar.
     */
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

/**
 * Normaliza valores de texto para mostrarlos en el PDF de forma segura.
 *
 * - Si el texto es `null`, devuelve `"-"`.
 * - Si el texto es demasiado largo, lo recorta a un máximo de 40 caracteres.
 *
 * @param s Texto de entrada (posiblemente `null`).
 * @return Texto seguro para imprimir en el PDF.
 */
private fun safe(s: String?): String = s?.take(40) ?: "-"

/**
 * Abre un archivo PDF usando una app externa.
 *
 * Utiliza [FileProvider] para obtener una URI segura y concede permisos
 * de lectura temporales al visor seleccionado.
 *
 * Requiere que exista un `provider` configurado en el `AndroidManifest`
 * con la autoridad `${context.packageName}.provider` y sus `paths` correspondientes.
 *
 * @param context Contexto Android usado para resolver el [FileProvider] y lanzar el intent.
 * @param file Archivo PDF a abrir.
 */
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

/**
 * Comparte un archivo PDF mediante un intent de compartir.
 *
 * Utiliza [FileProvider] para generar una URI segura y adjunta el PDF mediante
 * [Intent.EXTRA_STREAM], otorgando permisos de lectura temporales.
 *
 * @param context Contexto Android usado para resolver el [FileProvider] y lanzar el intent.
 * @param file Archivo PDF a compartir.
 */
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

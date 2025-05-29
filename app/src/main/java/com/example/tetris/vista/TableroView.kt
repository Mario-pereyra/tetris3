package com.example.tetris.vista

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.tetris.juego.MotorJuegoTetris
import com.example.tetris.viewmodel.TetrisViewModel
import androidx.core.graphics.toColorInt


class TableroView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null,
    estiloDefectoAttr: Int = 0
) : View(contexto, atributos, estiloDefectoAttr) {


    private var viewModel: TetrisViewModel? = null
    private var anchoTablero: Int = 10 // Valores por defecto
    private var altoTablero: Int = 16
    private var celdasTablero: Array<Array<Int?>> = Array(altoTablero) { Array(anchoTablero) { null } }
    private var piezaActual: com.example.tetris.modelo.TetrominoModelo? = null
    private var juegoTerminado: Boolean = false


    private val gestureDetector: GestureDetector


    private val pinturaFondoTablero = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.FILL
    }


    private val pinturaLineasCuadricula = Paint().apply {
        color = "#555555".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }


    private val pinturaBloque = Paint().apply {
        style = Paint.Style.FILL
    }


    private val pinturaBordeBloque = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }


    private val pinturaTextoGameOver = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 60f
        setShadowLayer(5f, 2f, 2f, Color.BLACK)
    }
    private val pinturaSubtextoGameOver = Paint().apply {
        color = Color.LTGRAY
        textAlign = Paint.Align.CENTER
        textSize = 40f
    }


    private val pinturaLineaDivisoria = Paint().apply {
        color = "#44FFFFFF".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private var tamanoCelda: Float = 0f
    private var margenIzquierdoTablero: Float = 0f
    private var margenSuperiorTablero: Float = 0f

    init {

        gestureDetector = GestureDetector(contexto, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {

                if (juegoTerminado) return false


                val mitadPantalla = width / 2f
                return when {
                    e.x < mitadPantalla -> {

                        viewModel?.moverPiezaIzquierda()
                        true
                    }
                    else -> {

                        viewModel?.moverPiezaDerecha()
                        true
                    }
                }
            }
        })

        isClickable = true
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }


    fun establecerViewModel(vm: TetrisViewModel) {
        this.viewModel = vm


        findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            vm.estadoTablero.observe(lifecycleOwner) { tablero ->
                celdasTablero = tablero
                invalidate()
            }
            vm.piezaActual.observe(lifecycleOwner) { pieza ->
                piezaActual = pieza
                invalidate()
            }

            vm.juegoTerminado.observe(lifecycleOwner) { terminado ->
                juegoTerminado = terminado
                invalidate()
            }
        }


        this.anchoTablero = 10
        this.altoTablero = 16


        requestLayout()
        invalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)


        val anchoDisponibleParaTablero = w * 0.95f
        tamanoCelda = anchoDisponibleParaTablero / anchoTablero


        val altoTotalTablero = tamanoCelda * altoTablero


        margenIzquierdoTablero = (w - (tamanoCelda * anchoTablero)) / 2f
        margenSuperiorTablero = (h - altoTotalTablero) / 2f

        if (margenSuperiorTablero < 0) {
            margenSuperiorTablero = 20f
        }


        pinturaTextoGameOver.textSize = tamanoCelda * 2.2f
        pinturaSubtextoGameOver.textSize = tamanoCelda * 1.2f
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        val rectFondoTablero = RectF(
            margenIzquierdoTablero,
            margenSuperiorTablero,
            margenIzquierdoTablero + anchoTablero * tamanoCelda,
            margenSuperiorTablero + altoTablero * tamanoCelda
        )
        canvas.drawRect(rectFondoTablero, pinturaFondoTablero)


        for (fila in 0 until altoTablero) {
            for (columna in 0 until anchoTablero) {
                celdasTablero[fila][columna]?.let { colorBloque ->
                    dibujarBloque(canvas, columna, fila, colorBloque)
                }
            }
        }
        // 3. Dibuja la pieza actual que el jugador está controlando.
        piezaActual?.let { pieza ->
            // Itera sobre los bloques de la pieza actual.
            pieza.obtenerCoordenadasAbsolutasDeBloques().forEach { puntoBloque ->
                // Solo dibujar bloques que estén dentro de la parte visible vertical del tablero.
                if (puntoBloque.y >= 0) {
                    dibujarBloque(canvas, puntoBloque.x, puntoBloque.y, pieza.color)
                }
            }
        }

        // 4. Dibuja las líneas de la cuadrícula sobre el tablero
        // Dibuja líneas verticales
        for (i in 0..anchoTablero) {
            val xLinea = margenIzquierdoTablero + i * tamanoCelda
            canvas.drawLine(xLinea, margenSuperiorTablero, xLinea, margenSuperiorTablero + altoTablero * tamanoCelda, pinturaLineasCuadricula)
        }
        // Dibuja líneas horizontales
        for (i in 0..altoTablero) {
            val yLinea = margenSuperiorTablero + i * tamanoCelda
            canvas.drawLine(margenIzquierdoTablero, yLinea, margenIzquierdoTablero + anchoTablero * tamanoCelda, yLinea, pinturaLineasCuadricula)
        }

        val mitadPantalla = width / 2f
        canvas.drawLine(
            mitadPantalla,
            margenSuperiorTablero,
            mitadPantalla,
            margenSuperiorTablero + altoTablero * tamanoCelda,
            pinturaLineaDivisoria
        )

        if (juegoTerminado) {
            dibujarMensajeGameOver(canvas)
        }
    }


    private fun dibujarBloque(canvas: Canvas, columna: Int, fila: Int, color: Int) {

        val rectIzquierdo = margenIzquierdoTablero + columna * tamanoCelda
        val rectSuperior = margenSuperiorTablero + fila * tamanoCelda
        val rectDerecho = rectIzquierdo + tamanoCelda
        val rectInferior = rectSuperior + tamanoCelda


        pinturaBloque.color = color
        canvas.drawRect(rectIzquierdo, rectSuperior, rectDerecho, rectInferior, pinturaBloque)
        canvas.drawRect(rectIzquierdo, rectSuperior, rectDerecho, rectInferior, pinturaBordeBloque)
    }


    private fun dibujarMensajeGameOver(canvas: Canvas) {
        val centroX = width / 2f
        val centroY = height / 2f


        val pinturaFondoMsg = Paint().apply {
            color = Color.argb(180, 0, 0, 0)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, centroY - (tamanoCelda*2), width.toFloat(), centroY + (tamanoCelda*2), pinturaFondoMsg)

        canvas.drawText("GAME OVER", centroX, centroY, pinturaTextoGameOver)
    }
}

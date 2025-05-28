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

/**
 * Vista personalizada que dibuja el tablero y las piezas del juego Tetris.
 * Implementa el patrón MVVM observando al ViewModel para actualizar la interfaz.
 */
class TableroView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null,
    estiloDefectoAttr: Int = 0
) : View(contexto, atributos, estiloDefectoAttr) {

    private var motorJuego: MotorJuegoTetris? = null
    private var viewModel: TetrisViewModel? = null
    private var anchoTablero: Int = 10 // Valores por defecto
    private var altoTablero: Int = 16
    private var celdasTablero: Array<Array<Int?>> = Array(altoTablero) { Array(anchoTablero) { null } }
    private var piezaActual: com.example.tetris.modelo.TetrominoModelo? = null
    private var juegoTerminado: Boolean = false

    // Detector de gestos para controlar los movimientos con toques
    private val gestureDetector: GestureDetector

    // --- Objetos Paint para dibujar ---
    // Paint para el fondo del tablero
    private val pinturaFondoTablero = Paint().apply {
        color = Color.DKGRAY // Un gris oscuro para el fondo del área de juego
        style = Paint.Style.FILL
    }

    // Paint para las líneas de la cuadrícula (opcional)
    private val pinturaLineasCuadricula = Paint().apply {
        color = "#555555".toColorInt() // Un gris más claro para las líneas
        style = Paint.Style.STROKE
        strokeWidth = 1f // Grosor fino para las líneas
    }

    // Paint para los bloques de las piezas
    private val pinturaBloque = Paint().apply {
        style = Paint.Style.FILL // Los bloques se rellenan con su color
    }

    // Paint para el borde de los bloques (da un efecto 3D o de separación)
    private val pinturaBordeBloque = Paint().apply {
        color = Color.BLACK // Borde negro para los bloques
        style = Paint.Style.STROKE
        strokeWidth = 2f // Grosor del borde
    }

    // Paint para el texto de "Game Over"
    private val pinturaTextoGameOver = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 60f // Se ajustará en onSizeChanged
        setShadowLayer(5f, 2f, 2f, Color.BLACK) // Sombra para el texto
    }
    private val pinturaSubtextoGameOver = Paint().apply {
        color = Color.LTGRAY
        textAlign = Paint.Align.CENTER
        textSize = 40f // Se ajustará en onSizeChanged
    }

    // Paint para las líneas divisorias de control (opcional)
    private val pinturaLineaDivisoria = Paint().apply {
        color = "#44FFFFFF".toColorInt() // Blanco semitransparente
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    // --- Variables de dimensionamiento ---
    private var tamanoCelda: Float = 0f      // Ancho y alto de cada celda del tablero en píxeles
    private var margenIzquierdoTablero: Float = 0f // Margen para centrar el tablero horizontalmente
    private var margenSuperiorTablero: Float = 0f  // Margen para posicionar el tablero verticalmente

    init {
        // Inicializar el detector de gestos para dividir la pantalla en dos mitades:
        // - Mitad izquierda: Mover pieza a la izquierda
        // - Mitad derecha: Mover pieza a la derecha
        gestureDetector = GestureDetector(contexto, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                return true // Necesario para que se procesen otros eventos
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                // No procesamos toques si el juego ha terminado
                if (juegoTerminado) return false

                // Determinar en qué mitad de la pantalla se tocó
                val mitadPantalla = width / 2f
                return when {
                    e.x < mitadPantalla -> {
                        // Tocar en la mitad izquierda mueve la pieza a la izquierda
                        viewModel?.moverPiezaIzquierda()
                        true
                    }
                    else -> {
                        // Tocar en la mitad derecha mueve la pieza a la derecha
                        viewModel?.moverPiezaDerecha()
                        true
                    }
                }
            }
        })

        // Activar detección de toques
        isClickable = true
    }

    /**
     * Procesa los eventos táctiles y los delega al detector de gestos
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    /**
     * Establece la instancia del motor del juego para esta vista.
     * Método mantenido por compatibilidad.
     */
    fun establecerMotorJuego(motor: MotorJuegoTetris) {
        this.motorJuego = motor
        this.anchoTablero = motor.anchoTablero
        this.altoTablero = motor.altoTablero

        // Configuramos el listener para actualizar la vista cuando cambia el motor
        motor.alActualizarVistaJuego = {
            celdasTablero = motor.celdasTablero
            piezaActual = motor.piezaActualParaVista
            invalidate()
        }

        // Solicita un redibujado inicial.
        requestLayout()
        invalidate()
    }

    /**
     * Establece el ViewModel para implementar el patrón MVVM
     */
    fun establecerViewModel(vm: TetrisViewModel) {
        this.viewModel = vm

        // Configuramos los observers para actualizar la vista cuando cambian los datos
        findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            // Observar el estado del tablero
            vm.estadoTablero.observe(lifecycleOwner) { tablero ->
                celdasTablero = tablero
                invalidate()
            }

            // Observar la pieza actual
            vm.piezaActual.observe(lifecycleOwner) { pieza ->
                piezaActual = pieza
                invalidate()
            }

            // Observar si el juego ha terminado
            vm.juegoTerminado.observe(lifecycleOwner) { terminado ->
                juegoTerminado = terminado
                invalidate()
            }
        }

        // Valores iniciales basados en el motor interno del ViewModel
        this.anchoTablero = 10 // Por defecto
        this.altoTablero = 16  // Por defecto

        // Solicita un redibujado inicial.
        requestLayout()
        invalidate()
    }

    /**
     * Se llama cuando el tamaño de esta vista ha cambiado.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Calcula el tamaño de celda basado en el ancho disponible y el número de columnas.
        val anchoDisponibleParaTablero = w * 0.95f // Usar 95% del ancho para el tablero
        tamanoCelda = anchoDisponibleParaTablero / anchoTablero

        // Calcula el alto total que ocupará el tablero.
        val altoTotalTablero = tamanoCelda * altoTablero

        // Calcula los márgenes para centrar el tablero.
        margenIzquierdoTablero = (w - (tamanoCelda * anchoTablero)) / 2f
        margenSuperiorTablero = (h - altoTotalTablero) / 2f

        // Asegurarse de que el tablero no se dibuje fuera de la pantalla si es muy alto.
        if (margenSuperiorTablero < 0) {
            margenSuperiorTablero = 20f // Un pequeño margen superior si no cabe centrado.
        }

        // Ajustar tamaño del texto de Game Over relativo al tamaño de celda
        pinturaTextoGameOver.textSize = tamanoCelda * 2.2f
        pinturaSubtextoGameOver.textSize = tamanoCelda * 1.2f
    }

    /**
     * El corazón del dibujado. Se llama cada vez que la vista necesita redibujarse.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Dibuja el fondo del tablero de juego.
        val rectFondoTablero = RectF(
            margenIzquierdoTablero,
            margenSuperiorTablero,
            margenIzquierdoTablero + anchoTablero * tamanoCelda,
            margenSuperiorTablero + altoTablero * tamanoCelda
        )
        canvas.drawRect(rectFondoTablero, pinturaFondoTablero)

        // 2. Dibuja las piezas ya asentadas en el tablero.
        for (fila in 0 until altoTablero) {
            for (columna in 0 until anchoTablero) {
                celdasTablero[fila][columna]?.let { colorBloque ->
                    // Si la celda no es null, contiene el color de un bloque asentado.
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

        // 5. Dibuja una línea vertical en el centro para indicar la división de controles
        val mitadPantalla = width / 2f
        canvas.drawLine(
            mitadPantalla,
            margenSuperiorTablero,
            mitadPantalla,
            margenSuperiorTablero + altoTablero * tamanoCelda,
            pinturaLineaDivisoria
        )

        // 6. Si el juego ha terminado, dibuja el mensaje de "Game Over".
        if (juegoTerminado) {
            dibujarMensajeGameOver(canvas)
        }
    }

    /**
     * Función auxiliar para dibujar un único bloque del Tetris en el canvas.
     * @param canvas El Canvas sobre el cual dibujar.
     * @param columna La columna (índice X) del tablero donde se dibujará el bloque.
     * @param fila La fila (índice Y) del tablero donde se dibujará el bloque.
     * @param color El color del bloque.
     */
    private fun dibujarBloque(canvas: Canvas, columna: Int, fila: Int, color: Int) {
        // Calcula las coordenadas en píxeles del rectángulo del bloque.
        val rectIzquierdo = margenIzquierdoTablero + columna * tamanoCelda
        val rectSuperior = margenSuperiorTablero + fila * tamanoCelda
        val rectDerecho = rectIzquierdo + tamanoCelda
        val rectInferior = rectSuperior + tamanoCelda

        // Establece el color para el relleno del bloque.
        pinturaBloque.color = color
        // Dibuja el relleno del bloque.
        canvas.drawRect(rectIzquierdo, rectSuperior, rectDerecho, rectInferior, pinturaBloque)
        // Dibuja el borde del bloque.
        canvas.drawRect(rectIzquierdo, rectSuperior, rectDerecho, rectInferior, pinturaBordeBloque)
    }

    /**
     * Dibuja el mensaje de "Game Over" centrado en la pantalla.
     */
    private fun dibujarMensajeGameOver(canvas: Canvas) {
        val centroX = width / 2f
        val centroY = height / 2f

        // Dibuja un rectángulo semitransparente de fondo para el mensaje
        val pinturaFondoMsg = Paint().apply {
            color = Color.argb(180, 0, 0, 0) // Negro semitransparente
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, centroY - (tamanoCelda*2), width.toFloat(), centroY + (tamanoCelda*2), pinturaFondoMsg)

        // Solo mostramos el mensaje de Game Over
        canvas.drawText("GAME OVER", centroX, centroY, pinturaTextoGameOver)
    }
}

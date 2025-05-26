package com.example.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.tetris.databinding.ActivityMainBinding
import com.example.tetris.juego.MotorJuegoTetris
import com.example.tetris.db.PuntuacionEntity
import com.example.tetris.db.TetrisDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var motorJuego: MotorJuegoTetris
    private lateinit var db: TetrisDatabase
    private lateinit var botonTop10: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa la base de datos
        db = TetrisDatabase.getInstance(this)

        // Inicializa el motor con valores por defecto
        motorJuego = MotorJuegoTetris(anchoTablero = 10, altoTablero = 16)

        // Conecta primero el motor del juego con la vista
        binding.vistaTableroTetris.establecerMotorJuego(motorJuego)

        // Ahora que el motor está conectado con la vista, configura los listeners y UI
        configurarListenersDeControles()
        configurarListenersDelMotorJuego()

        // Estado inicial de los controles
        actualizarVisibilidadControles(!motorJuego.juegoTerminado)

        // Hacer que el botón de reiniciar siempre esté visible
        binding.botonReiniciar.visibility = View.VISIBLE

        // Agrega el botón Top 10 con diseño consistente
        botonTop10 = Button(this).apply {
            text = "Top 10"
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { mostrarDialogoTop10() }
        }
        binding.panelInformacion.addView(botonTop10)
    }

    private fun configurarListenersDeControles() {
        with(binding) {
            botonIzquierda.setOnClickListener {
                if (!motorJuego.juegoTerminado) {
                    motorJuego.moverPiezaIzquierda()
                }
            }

            botonDerecha.setOnClickListener {
                if (!motorJuego.juegoTerminado) {
                    motorJuego.moverPiezaDerecha()
                }
            }

            botonRotar.setOnClickListener {
                if (!motorJuego.juegoTerminado) {
                    motorJuego.rotarPieza()
                }
            }

            botonCaidaRapida.setOnClickListener {
                if (!motorJuego.juegoTerminado) {
                    motorJuego.dejarCaerPiezaRapido()
                }
            }

            botonReiniciar.setOnClickListener {
                motorJuego.reiniciarJuego()
                actualizarVisibilidadControles(true)
                // No necesitamos cambiar la visibilidad del botón de reiniciar
            }
        }
    }

    private fun configurarListenersDelMotorJuego() {
        motorJuego.alActualizarPuntuacionNivel = { puntuacion, nivel ->
            runOnUiThread {
                binding.textoPuntuacion.text = puntuacion.toString()
                binding.textoNivel.text = nivel.toString()
            }
        }

        motorJuego.alTerminarJuego = { puntuacionFinal ->
            runOnUiThread {
                actualizarVisibilidadControles(false)
                // Ya no necesitamos cambiar la visibilidad del botón de reiniciar
                mostrarDialogoGameOver(puntuacionFinal)
            }
        }
    }

    private fun actualizarVisibilidadControles(habilitado: Boolean) {
        with(binding) {
            botonIzquierda.isEnabled = habilitado
            botonDerecha.isEnabled = habilitado
            botonRotar.isEnabled = habilitado
            botonCaidaRapida.isEnabled = habilitado
        }
    }

    private fun mostrarDialogoGameOver(puntuacionFinal: Int) {
        val input = EditText(this)
        input.hint = "Tu nombre"
        AlertDialog.Builder(this)
            .setTitle("¡Juego Terminado!")
            .setMessage("Tu puntuación final es: $puntuacionFinal\n\nIngresa tu nombre para guardar la puntuación:")
            .setView(input)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nombre = input.text.toString().ifBlank { "Jugador" }
                guardarPuntuacion(nombre, puntuacionFinal)
                motorJuego.reiniciarJuego()
                actualizarVisibilidadControles(true)
                dialog.dismiss()
            }
            .setNegativeButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun guardarPuntuacion(nombre: String, puntuacion: Int) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    db.puntuacionDao().insertarPuntuacion(PuntuacionEntity(nombre = nombre, puntuacion = puntuacion))
                }
            } catch (e: Exception) {
                Log.e("Tetris", "Error al guardar puntuación", e)
                Toast.makeText(this@MainActivity, "Error al guardar puntuación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoTop10() {
        lifecycleScope.launch {
            try {
                val top10 = withContext(Dispatchers.IO) {
                    db.puntuacionDao().obtenerTop10()
                }
                val mensaje = if (top10.isEmpty())
                    "No hay puntuaciones aún."
                else
                    top10.withIndex().joinToString("\n") { "${it.index + 1}. ${it.value.nombre}: ${it.value.puntuacion}" }

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Top 10 Puntuaciones")
                    .setMessage(mensaje)
                    .setPositiveButton("Cerrar", null)
                    .show()
            } catch (e: Exception) {
                Log.e("Tetris", "Error al obtener puntuaciones", e)
                Toast.makeText(this@MainActivity, "Error al cargar puntuaciones", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Pausar el juego si está en curso
        if (!motorJuego.juegoTerminado) {
            // Aquí podrías implementar una pausa si lo deseas
        }
    }
}

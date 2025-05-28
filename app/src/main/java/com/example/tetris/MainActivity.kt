package com.example.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.tetris.databinding.ActivityMainBinding
import com.example.tetris.db.TetrisDatabase
import com.example.tetris.viewmodel.TetrisViewModel
import com.example.tetris.viewmodel.TetrisViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TetrisViewModel
    private lateinit var db: TetrisDatabase
    private lateinit var botonTop10: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa la base de datos
        db = TetrisDatabase.getInstance(this)

        // Inicializa el ViewModel con su Factory
        val viewModelFactory = TetrisViewModelFactory(db.puntuacionDao())
        viewModel = ViewModelProvider(this, viewModelFactory)[TetrisViewModel::class.java]

        // Conecta la vista con el ViewModel
        binding.vistaTableroTetris.establecerViewModel(viewModel)

        // Configura los listeners y observadores
        configurarListenersDeControles()
        configurarObservadores()

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
                viewModel.moverPiezaIzquierda()
            }

            botonDerecha.setOnClickListener {
                viewModel.moverPiezaDerecha()
            }

            botonRotar.setOnClickListener {
                viewModel.rotarPieza()
            }

            botonCaidaRapida.setOnClickListener {
                viewModel.dejarCaerPiezaRapido()
            }

            botonReiniciar.setOnClickListener {
                viewModel.reiniciarJuego()
            }
        }
    }

    private fun configurarObservadores() {
        // Observar cambios en la puntuación
        viewModel.puntuacion.observe(this) { puntuacion ->
            binding.textoPuntuacion.text = puntuacion.toString()
        }

        // Observar cambios en el nivel
        viewModel.nivel.observe(this) { nivel ->
            binding.textoNivel.text = nivel.toString()
        }

        // Observar fin de juego
        viewModel.juegoTerminado.observe(this) { juegoTerminado ->
            actualizarVisibilidadControles(!juegoTerminado)
            if (juegoTerminado) {
                mostrarDialogoGameOver(viewModel.puntuacion.value ?: 0)
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
                viewModel.guardarPuntuacion(nombre)
                viewModel.reiniciarJuego()
                dialog.dismiss()
            }
            .setNegativeButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun mostrarDialogoTop10() {
        viewModel.cargarTop10Puntuaciones()
        viewModel.top10Puntuaciones.observe(this) { top10 ->
            val mensaje = if (top10.isEmpty())
                "No hay puntuaciones aún."
            else
                top10.withIndex().joinToString("\n") { "${it.index + 1}. ${it.value.nombre}: ${it.value.puntuacion}" }

            AlertDialog.Builder(this)
                .setTitle("Top 10 Puntuaciones")
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }

    override fun onPause() {
        super.onPause()
        // En un futuro se podría implementar lógica de pausa
    }
}

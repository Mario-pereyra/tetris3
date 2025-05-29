package com.example.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TetrisDatabase.getInstance(this)

        val viewModelFactory = TetrisViewModelFactory(db.puntuacionDao())
        viewModel = ViewModelProvider(this, viewModelFactory)[TetrisViewModel::class.java]

        binding.vistaTableroTetris.establecerViewModel(viewModel)

        configurarListenersDeControles()
        configurarObservadores()

        binding.botonReiniciar.visibility = View.VISIBLE
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

            botonTop10.setOnClickListener {
                mostrarDialogoTop10()
            }
        }
    }

    private fun configurarObservadores() {
        viewModel.puntuacion.observe(this) { puntuacion ->
            binding.textoPuntuacion.text = puntuacion.toString()
        }

        viewModel.nivel.observe(this) { nivel ->
            binding.textoNivel.text = nivel.toString()
        }

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
                top10.withIndex()
                    .joinToString("\n") { "${it.index + 1}. ${it.value.nombre}: ${it.value.puntuacion}" }

            AlertDialog.Builder(this)
                .setTitle("Top 10 Puntuaciones")
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }
}

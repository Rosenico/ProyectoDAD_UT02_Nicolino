package com.example.voz_a_texto_kotlin

import Personaje
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.voz_a_texto_kotlin.databinding.ActivityPersonajeDetailBinding
import java.util.Locale

class PersonajeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonajeDetailBinding
    private lateinit var listaPersonajes: ArrayList<Personaje>
    private var posicionActual: Int = 0

    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val voiceInput = result?.get(0)?.lowercase(Locale.getDefault()) ?: ""
            procesarComandoVoz(voiceInput)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonajeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibir la lista y la posición desde MainActivity
        listaPersonajes = intent.getParcelableArrayListExtra("listaPersonajes") ?: arrayListOf()
        posicionActual = intent.getIntExtra("posicion", 0)

        mostrarPersonaje()

        // Configurar el botón de micrófono
        binding.ibtnMicrofono.setOnClickListener {
            activarMicrofono()
        }
    }

    private fun mostrarPersonaje() {
        if (posicionActual in listaPersonajes.indices) {
            val personaje = listaPersonajes[posicionActual]
            binding.tvNombre.text = personaje.nombre
            binding.tvInformacion.text = personaje.informacion
            binding.ivImagen.setImageResource(personaje.imagenId)
        }
    }

    private fun activarMicrofono() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult.launch(intent)
        } else {
            Toast.makeText(this, "El dispositivo no soporta entrada por voz", Toast.LENGTH_SHORT).show()
        }
    }

    private fun procesarComandoVoz(comando: String) {
        when (comando) {
            "siguiente" -> {
                if (posicionActual < listaPersonajes.size - 1) {
                    posicionActual++
                    mostrarPersonaje()
                } else {
                    Toast.makeText(this, "No hay más personajes", Toast.LENGTH_SHORT).show()
                }
            }
            "atrás", "anterior" -> {
                if (posicionActual > 0) {
                    posicionActual--
                    mostrarPersonaje()
                } else {
                    Toast.makeText(this, "Estás en el primer personaje", Toast.LENGTH_SHORT).show()
                }
            }
            else -> Toast.makeText(this, "Comando no reconocido", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.example.voz_a_texto_kotlin

import Personaje
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voz_a_texto_kotlin.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adaptador: AdaptadorPersonajes

    private val listaPersonajes = ArrayList(PersonajeData.personajes)

    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val voiceInput = result?.get(0) ?: ""
            binding.etNombre.setText(voiceInput) // Muestra el texto reconocido
            filtrar(voiceInput) // Filtra con el texto de voz
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del RecyclerView
        binding.rvNombres.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorPersonajes(listaPersonajes) { personaje ->
            onPersonajeClicked(personaje)
        }
        binding.rvNombres.adapter = adaptador

        // Configuración del botón de micrófono
        binding.ibtnMicrofono.setOnClickListener {
            escucharVoz()
        }

        // Filtrado por texto
        binding.etNombre.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filtrar(s.toString())
            }
        })
    }

    private fun onPersonajeClicked(personaje: Personaje) {
        val intent = Intent(this, PersonajeDetailActivity::class.java)
        intent.putParcelableArrayListExtra("listaPersonajes", listaPersonajes)
        intent.putExtra("posicion", listaPersonajes.indexOf(personaje))
        startActivity(intent)
    }


    private fun escucharVoz() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult.launch(intent)
        } else {
            Log.e("ERROR", "Su dispositivo no admite entrada por voz")
            Toast.makeText(this, "Su dispositivo no admite entrada por voz", Toast.LENGTH_LONG).show()
        }
    }

    private fun filtrar(texto: String) {
        val listaFiltrada = listaPersonajes.filter { personaje ->
            personaje.nombre.contains(texto, ignoreCase = true)
        }

        if (listaFiltrada.isEmpty()) {
            Toast.makeText(this, "No se encontraron personajes", Toast.LENGTH_SHORT).show()
        }

        adaptador.filtrar(ArrayList(listaFiltrada))
    }
}

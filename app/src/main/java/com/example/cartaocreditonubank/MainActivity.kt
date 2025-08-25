package com.example.cartaocreditonubank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import android.widget.Toast
import androidx.core.widget.addTextChangedListener

class MainActivity : AppCompatActivity() {

    private lateinit var editTextNome: EditText
    private lateinit var cardContainer: FrameLayout
    private lateinit var cardFront: CardView
    private lateinit var cardBack: CardView


    private val NOME_USUARIO_KEY = "nome_usuario"
    private val PREFS_NAME = "meus_dados"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // EditText
        editTextNome = findViewById(R.id.name)

        // CardView (flip)
        cardContainer = findViewById(R.id.cardContainer)
        cardFront = findViewById(R.id.cardFront)
        cardBack = findViewById(R.id.cardBack)

        // Inicialmente, verso invisível
        cardBack.visibility = View.GONE

        // Listener para flip do cartão
        cardContainer.setOnClickListener {
            if (cardFront.isVisible) {
                // Flip para verso
                cardFront.animate().rotationY(90f).setDuration(300).withEndAction {
                    cardFront.visibility = View.GONE
                    cardBack.visibility = View.VISIBLE
                    cardBack.rotationY = -90f
                    cardBack.animate().rotationY(0f).setDuration(300).start()
                }.start()
            } else {
                // Flip para frente
                cardBack.animate().rotationY(90f).setDuration(300).withEndAction {
                    cardBack.visibility = View.GONE
                    cardFront.visibility = View.VISIBLE
                    cardFront.rotationY = -90f
                    cardFront.animate().rotationY(0f).setDuration(300).start()
                }.start()
            }
        }

        // Listener para salvar automaticamente se tiver 3 ou mais caracteres
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        editTextNome.doAfterTextChanged { text ->
            val titular = text.toString().trim()
            if (titular.length < 3) {
                editTextNome.error = "O nome do titular deve conter pelo menos 3 caracteres."
            } else {
                editTextNome.error = null // remove erro
                prefs.edit().putString(NOME_USUARIO_KEY, titular).apply()
            }
        }

        val editTextNumero = findViewById<EditText>(R.id.numero)
        editTextNumero.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().replace (" ", " ")

                val limitado = if (texto.length > 19) texto.substring(0, 16) else texto

                if (texto.length > 19) {
                    editTextNumero.error = "O cartão deve conter 16 dígitos."
                } else {
                    editTextNumero.error = null
                }

                val formatado = limitado.chunked(4).joinToString {" "}

                if (formatado != s.toString()) {
                    editTextNumero.setText(formatado)
                    editTextNumero.setSelection(formatado.length)
                }
            }

        })

        val editTextValidade = findViewById<EditText>(R.id.validade)

        editTextValidade.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().replace("/", "")

                // Limita a 4 dígitos (MM + AA)
                val limitado = if (texto.length > 4) texto.substring(0, 4) else texto

                if (texto.length > 19) {
                    editTextValidade.error = "A validade deve conter mês e ano: MM/AA."
                } else {
                    editTextValidade.error = null
                }

                // Insere "/" depois dos 2 primeiros
                val formatado = when {
                    limitado.length >= 3 -> limitado.substring(0, 2) + "/" + limitado.substring(2)
                    else -> limitado
                }

                if (formatado != s.toString()) {
                    editTextValidade.setText(formatado)
                    editTextValidade.setSelection(formatado.length)
                }
            }
        })

        val editTextCVV = findViewById<EditText>(R.id.cvv)

        editTextCVV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString()

                // Limita a 3 dígitos
                val limitado = if (texto.length > 3) texto.substring(0, 3) else texto

                if (texto.length > 19) {
                    editTextCVV.error = "O cartão deve conter 3 dígitos."
                } else {
                    editTextCVV.error = null
                }

                if (limitado != s.toString()) {
                    editTextCVV.setText(limitado)
                    editTextCVV.setSelection(limitado.length)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val nomeSalvo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString(NOME_USUARIO_KEY, "")
        if (!nomeSalvo.isNullOrEmpty()) {
            editTextNome.setText(nomeSalvo)
        }
    }
}

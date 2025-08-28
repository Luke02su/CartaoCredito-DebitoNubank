package com.example.cartaocreditonubank

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged

class MainActivity : AppCompatActivity() {

    private lateinit var editTextNome: EditText
    private lateinit var editTextNumero: EditText
    private lateinit var editTextValidade: EditText
    private lateinit var editTextCVV: EditText

    private lateinit var cardContainer: FrameLayout
    private lateinit var cardFront: CardView
    private lateinit var cardBack: CardView

    // Chaves para SharedPreferences
    private val PREFS_NAME = "meus_dados"
    private val NOME_USUARIO_KEY = "nome_usuario"
    private val NUMERO_CARTAO_KEY = "numero_cartao"
    private val VALIDADE_CARTAO_KEY = "validade_cartao"
    private val CVV_CARTAO_KEY = "cvv_cartao"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // EditTexts
        editTextNome = findViewById(R.id.name)
        editTextNumero = findViewById(R.id.numero)
        editTextValidade = findViewById(R.id.validade)
        editTextCVV = findViewById(R.id.cvv)

        // CardView (flip)
        cardContainer = findViewById(R.id.cardContainer)
        cardFront = findViewById(R.id.cardFront)
        cardBack = findViewById(R.id.cardBack)

        // Verso invisível inicialmente
        cardBack.visibility = View.GONE

        // Flip do cartão ao clicar
        cardContainer.setOnClickListener {
            if (cardFront.isVisible) {
                // Frente → Verso
                cardFront.animate().rotationY(90f).setDuration(300).withEndAction {
                    cardFront.visibility = View.GONE
                    cardBack.visibility = View.VISIBLE
                    cardBack.rotationY = -90f
                    cardBack.animate().rotationY(0f).setDuration(300).start()
                }.start()
            } else {
                // Verso → Frente
                cardBack.animate().rotationY(90f).setDuration(300).withEndAction {
                    cardBack.visibility = View.GONE
                    cardFront.visibility = View.VISIBLE
                    cardFront.rotationY = -90f
                    cardFront.animate().rotationY(0f).setDuration(300).start()
                }.start()
            }
        }

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Nome do titular
        editTextNome.doAfterTextChanged { text ->
            val titular = text.toString().trim()
            if (titular.length < 3) {
                editTextNome.error = "O nome do titular deve conter pelo menos 3 caracteres."
            } else {
                editTextNome.error = null
                prefs.edit().putString(NOME_USUARIO_KEY, titular).apply()
            }
        }

        // Número do cartão
        editTextNumero.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().replace(" ", "")
                val limitado = if (texto.length > 16) texto.substring(0, 16) else texto

                if (texto.length < 16) {
                    editTextNumero.error = "O cartão deve conter 16 dígitos."
                }

                val formatado = limitado.chunked(4).joinToString(" ")

                if (formatado != s.toString()) {
                    editTextNumero.setText(formatado)
                    editTextNumero.setSelection(formatado.length)
                }

                prefs.edit().putString(NUMERO_CARTAO_KEY, formatado).apply()
            }
        })

        // Validade do cartão
        editTextValidade.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().replace("/", "")
                val limitado = if (texto.length > 4) texto.substring(0, 4) else texto

                if (texto.length >= 2) {
                    val mes = texto.substring(0, 2).toIntOrNull()
                    if (mes == null || mes !in 1..12) {
                        editTextValidade.error = "A validade deve estar no formato MM/AA."
                    } else if (texto.length < 4) {
                        editTextValidade.error = "A validade deve estar no formato MM/AA."
                    }
                } else {
                    editTextValidade.error = "A validade deve estar no formato MM/AA."
                }

                val formatado = when {
                    limitado.length >= 3 -> limitado.substring(0, 2) + "/" + limitado.substring(2)
                    else -> limitado
                }

                if (formatado != s.toString()) {
                    editTextValidade.setText(formatado)
                    editTextValidade.setSelection(formatado.length)
                }

                prefs.edit().putString(VALIDADE_CARTAO_KEY, formatado).apply()
            }
        })

        // CVV
        editTextCVV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString()
                val limitado = if (texto.length > 3) texto.substring(0, 3) else texto

                if (texto.length < 3) {
                    editTextCVV.error = "O CVV deve conter 3 dígitos."
                }

                if (limitado != s.toString()) {
                    editTextCVV.setText(limitado)
                    editTextCVV.setSelection(limitado.length)
                }

                prefs.edit().putString(CVV_CARTAO_KEY, limitado).apply()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        editTextNome.setText(prefs.getString(NOME_USUARIO_KEY, ""))
        editTextNumero.setText(prefs.getString(NUMERO_CARTAO_KEY, ""))
        editTextValidade.setText(prefs.getString(VALIDADE_CARTAO_KEY, ""))
        editTextCVV.setText(prefs.getString(CVV_CARTAO_KEY, ""))
    }
}

package com.example.guessthephrase

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    lateinit var myRV: RecyclerView
    lateinit var phraseTextView: TextView
    lateinit var letterTextView: TextView
    lateinit var guessedET:EditText
    var guessList = arrayListOf<String>()
    var guessLetters = arrayListOf<String>()
    var phrases = arrayListOf<String>()
    var counter = 10
    var answer = ""
    var isPhrase = true
    var guessCorrectLetter = ""
    // extension function to set edit text maximum length
    fun EditText.setMaxLength(maxLength: Int){
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }
    fun customAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(title)
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_info)

        //performing positive action
        builder.setPositiveButton("Ok") { dialogInterface, which ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun replayGame() {
        guessList.clear()
        counter = 10
        isPhrase = true
        answer = phrases[Random.nextInt(0, phrases.size)]
        phraseTextView.setText("Phrase"+encodePhrase(answer))
        letterTextView.setText("")
        guessedET.hint = "Guess the letters of the phrase"
        myRV.adapter!!.notifyDataSetChanged()
    }

    fun checkPhrase(guessText: String) {
        if (answer == guessText.capitalize()) {
            customAlert("Congratulations","You guessed it correctly!")
            replayGame()
        } else {
            counter--
            guessList.add("$guessText is a wrong guess.")
            if(counter!=0){
            guessList.add("$counter guesses remaining!")}
            else{
                guessedET.hint = "Guess the letters of the phrase"
                guessedET.setMaxLength(1)
                counter = 10
                isPhrase = false
                guessList.clear()

            }

        }
        myRV.adapter!!.notifyDataSetChanged()

    }

    fun encodePhrase(phrase: String): String {
        var enPhrase = ""
        for (i in phrase) {
            if (i == ' ') {
                enPhrase += " "
            } else {
                enPhrase += "*"
            }

        }
        return enPhrase
    }

    fun checkIfEmpty(str: String): Boolean {
        if (str.isEmpty()) {
            var myLayout = findViewById<ConstraintLayout>(R.id.clID)

            Snackbar.make(myLayout, "Enter a phrase/letter", Snackbar.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    fun checkLetter(guessLetter: Char) {

        letterTextView.setVisibility(View.VISIBLE)
        guessedET.setMaxLength(1)

        if (answer.contains(guessLetter)) {//the letter exist in the phrase
            guessLetters.add(guessLetter + "")
            counter--
            guessCorrectLetter=""
            var countLetters = 0
            for (i in answer) {
                if (guessLetters.contains(i+"")) {
                    if(guessLetter==i)
                        countLetters++
                    guessCorrectLetter += i
                } else if (i == ' ') {
                    guessCorrectLetter += " "
                } else if (i != guessLetter && i != ' ' && i != '*') {
                    guessCorrectLetter += "*"
                } else {
                    guessCorrectLetter += i
                }
            }

            letterTextView.text = "Guessed letters: " + guessLetters.toString()
            phraseTextView.text = "Phrase:" + guessCorrectLetter

            guessList.add("Found $countLetters $guessLetter(s)")
            guessList.add("$counter guesses remaining")
            myRV.adapter!!.notifyDataSetChanged()
        } else { //if the user entered a wrong letter
            counter--
            guessList.add("$guessLetter is a wrong guess")
            guessList.add("$counter guesses remaining")
            myRV.adapter!!.notifyDataSetChanged()
        }
        if (answer == guessCorrectLetter) {
            customAlert("Congratulations","You guessed it correctly!")

            myRV.adapter!!.notifyDataSetChanged()
            replayGame()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // RecyclerView and Adapter
        myRV = findViewById(R.id.rvMain)
        myRV.adapter = RecyclerViewAdapter(guessList)
        myRV.layoutManager = LinearLayoutManager(this)
        //add the phrases
        phrases.add("Break a leg")
        phrases.add("Better late than never")
        phrases.add("No pain no gain")
        phrases.add("So far so good")
        phrases.add("Break the ice")
        phrases.add("Live and learn")
        phrases.add("Spill the beans")
        phrases.add("Saving for a rainy day")
        //Generate random phrase
        answer = phrases[Random.nextInt(0, phrases.size)]
        phraseTextView = findViewById(R.id.phraseTextView)
        letterTextView = findViewById(R.id.letterTextView)

        //encode the phrase with * ignoring the spaces
        phraseTextView.text = "Phrase: " + encodePhrase(answer)

        val GuessBT = findViewById<Button>(R.id.GuessBT)
         guessedET = findViewById<EditText>(R.id.editTextGuess)


      guessedET.setMaxLength(answer.length)

        GuessBT.setOnClickListener {
            var guessText = guessedET.text.toString()
            if (isPhrase) {
                if (counter > 0) {
                    if (!checkIfEmpty(guessText)) {
                        checkPhrase(guessText)
                    }
                }
            } else {
                if (counter > 0) {
                    if (!checkIfEmpty(guessText)) {
                        guessedET.hint = "Guess the letters of the phrase"
                        checkLetter(guessText[0])
                    }
                }else{ //the user lost his 10 chances in guessing the letters
                    customAlert("Oops!","The phrase was $answer")
                    replayGame()
                }
            }
            guessedET.text.clear()
        }

    }

}

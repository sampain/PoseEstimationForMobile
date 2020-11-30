package com.epmus.mobile

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.epmus.mobile.MongoDbService.MongoTransactions
import com.epmus.mobile.ui.login.*
import io.realm.mongodb.Credentials
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import kotlinx.android.synthetic.main.activity_create_account.*
import org.bson.Document

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_Create)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val username = findViewById<EditText>(R.id.create_username)
        val password = findViewById<EditText>(R.id.create_password)
        val createAccoutButton = findViewById<Button>(R.id.createAccountButton)
        val createAccoutButtonDisabled = findViewById<Button>(R.id.createAccountButtonDisabled)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            createAccoutButton.isEnabled = loginState.isDataValid
            if (loginState.isDataValid) {
                createAccoutButtonDisabled.visibility = View.GONE
                createAccoutButton.visibility = View.VISIBLE
            } else {
                createAccoutButtonDisabled.visibility = View.VISIBLE
                createAccoutButton.visibility = View.GONE
            }

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer

            loadingCreate.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            setResult(Activity.RESULT_OK)
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        createUser(username.text.toString(), password.text.toString())
                }
                false
            }

            createAccoutButton.setOnClickListener {
                loadingCreate.visibility = View.VISIBLE
                createUser(username.text.toString(), password.text.toString())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun createUser(username: String, password: String) {
        realmApp.emailPassword.registerUserAsync(username, password) { registerResult ->
            if (registerResult.isSuccess) {
                loginUser(username, password)
            } else {
                loadingCreate.visibility = View.GONE
                Toast.makeText(
                    baseContext,
                    registerResult.error.errorMessage
                        ?: "Compte déjà existant / La connexion n'a pas réussi",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        realmApp.loginAsync(Credentials.emailPassword(username, password)) { loginResult ->
            if (loginResult.isSuccess) {
                findAndUpdateCustomData(username, password)
            } else {
                loadingCreate.visibility = View.GONE
                Toast.makeText(
                    baseContext,
                    loginResult.error.errorMessage ?: "La connexion n'a pas réussi",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun findAndUpdateCustomData(username: String, password: String) {
        val user = realmApp.currentUser()
        val mongoClient: MongoClient =
            user?.getMongoClient("mongodb-atlas")!!
        val mongoDatabase: MongoDatabase =
            mongoClient.getDatabase("iphysioBD-dev")!!
        val mongoCollection: MongoCollection<Document> =
            mongoDatabase.getCollection("patients")!!

        mongoCollection.findOne(
            Document("email", username)
        ).getAsync { findResult ->
            if (findResult.isSuccess) {
                val document = findResult.get()
                if (document == null) {
                    user.logOutAsync {
                        loadingCreate.visibility = View.GONE
                        Toast.makeText(
                            baseContext,
                            "Il n'y a pas de compte pour faire le lien",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    document["patientId"] = user.id.toString()

                    mongoCollection.updateOne(Document("email", username), document)
                        .getAsync { updateResult ->
                            if (updateResult.isSuccess) {
                                logoutLogin(username, password)
                            } else {
                                user.logOutAsync {
                                    Toast.makeText(
                                        baseContext,
                                        updateResult.error.errorMessage
                                            ?: "Impossible de faire lien",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                }
            } else {
                user.logOutAsync {
                    loadingCreate.visibility = View.GONE
                    Toast.makeText(
                        baseContext,
                        findResult.error.errorMessage
                            ?: "Il n'y a pas de compte pour faire le lien",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun logoutLogin(username: String, password: String) {
        realmApp.currentUser()?.logOutAsync { logoutUser ->
            if (logoutUser.isSuccess) {
                realmApp.loginAsync(Credentials.emailPassword(username, password)) { loginUser ->
                    if (loginUser.isSuccess) {
                        val user = realmApp.currentUser()

                        val welcome = getString(R.string.welcome)

                        val intent = Intent(this, MainMenuActivity::class.java)
                        startActivity(intent)
                        finish()

                        if (MongoTransactions.user?.customData?.get("_id") == null) {
                            Toast.makeText(
                                applicationContext,
                                "Il y a eu une erreur lors de la création du compte",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "$welcome ${user?.customData?.get("name").toString()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
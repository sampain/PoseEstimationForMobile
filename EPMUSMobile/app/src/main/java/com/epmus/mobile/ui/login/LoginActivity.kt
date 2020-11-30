package com.epmus.mobile.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.epmus.mobile.CreateAccountActivity
import com.epmus.mobile.MainMenuActivity
import com.epmus.mobile.MongoDbService.MongoTransactions
import com.epmus.mobile.R
import io.realm.mongodb.Credentials
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_login.*
import org.bson.Document

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (realmApp.currentUser() != null) {
            if (realmApp.currentUser()!!.customData == null) {
                realmApp.currentUser()!!.logOutAsync {
                    Toast.makeText(
                        baseContext,
                        "Veuillez vous reconnecter",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {

                val welcome = getString(R.string.welcome)
                val displayName = realmApp.currentUser()!!.customData["name"]

                val intent = Intent(this, MainMenuActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(
                    applicationContext,
                    "$welcome $displayName",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loginDisabled = findViewById<Button>(R.id.loginDisabled)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val createAccount = findViewById<TextView>(R.id.createAccount)

        createAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid
            if (loginState.isDataValid) {
                loginDisabled.visibility = View.GONE
                login.visibility = View.VISIBLE
            } else {
                loginDisabled.visibility = View.VISIBLE
                login.visibility = View.GONE
            }

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
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
                        validateCredentials(username.text.toString(), password.text.toString())
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                validateCredentials(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish()

        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun validateCredentials(username: String, password: String) {
        val creds = Credentials.emailPassword(username, password)
        realmApp.loginAsync(creds) {
            if (!it.isSuccess) {
                loginViewModel.login(null)
                Toast.makeText(
                    baseContext,
                    it.error.errorMessage ?: "Mot de passe et/ou nom d'utilisateur invalide",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val user = realmApp.currentUser()
                if (MongoTransactions.user?.customData?.get("_id") == null) {
                    findAndUpdateCustomData(username, password)
                } else {
                    loginViewModel.login(user)
                    finish()
                }
            }
        }
    }

    private fun findAndUpdateCustomData(username: String, password: String) {
        val user = realmApp.currentUser() ?: return

        val mongoClient: MongoClient =
            user.getMongoClient("mongodb-atlas")!!
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
                        loading.visibility = View.GONE
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
                                Toast.makeText(
                                    baseContext,
                                    updateResult.error.errorMessage
                                        ?: "Impossible de faire lien",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            } else {
                user.logOutAsync {
                    loading.visibility = View.GONE
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
                validateCredentials(username, password)
            }
        }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
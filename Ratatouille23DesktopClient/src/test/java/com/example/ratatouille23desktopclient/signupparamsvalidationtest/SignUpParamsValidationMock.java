package com.example.ratatouille23desktopclient.signupparamsvalidationtest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpParamsValidationMock {


    /**
     * Il metodo si occupa di testare la validità di tutti i parametri passati in input per la registrazione di un nuovo
     * amministratore sulla piattaforma Ratatouille23.
     * Parametri obbligatori per la registrazione sono: nome, cognome, email (utilizzata anche come username unico) e password.
     * Policy della password:
     *      - Lunghezza minima di 8 caratteri, massima di 20 caratteri
     *      - Contiene almeno 1 numero
     *      - Contiene almeno 1 carattere minuscolo
     *      - Contiene almeno 1 carattere maiuscolo
     *      - Contiene almeno 1 carattere speciale tra: ^ $ * . [ ] { } ( ) ? - " ! @ # % & / \ , > < ' : ; | _ ~ ` + =
     * @param email
     * @param password
     * @param name
     * @param surname
     * @return
     */
    public boolean validSignUpParams(String email, String password, String name, String surname) {
        if (email == null || password == null || name == null || surname == null)
            return false;
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty())
            return false;
        //check password policy
        Pattern passwordRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~\\$^+=<>.\"|_~`]).{8,20}$");
        Matcher matcher = passwordRegex.matcher(password);
        if (!matcher.matches())
            return false;
        //check email regex
        Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        matcher = emailRegex.matcher(email);
        if (!matcher.matches())
            return false;
        return true;
    }
}

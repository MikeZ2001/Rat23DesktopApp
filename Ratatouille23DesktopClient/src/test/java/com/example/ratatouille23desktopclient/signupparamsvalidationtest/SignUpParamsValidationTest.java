package com.example.ratatouille23desktopclient.signupparamsvalidationtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Strategia adottata: Black Box - WECT
 * Possibili casi:
 *      Email:
 *          - email null
 *          - email valida
 *          - email vuota
 *          - email senza @
 *          - email senza dominio
 *          - email con caratteri speciali aggiuntivi
 *      Nome/Cognome:
 *          - nome/cognome null
 *          - nome/cognome valido
 *          - nome/cognome vuoto
 *      Password:
 *          - password null
 *          - password valida
 *          - password con meno di 8 caratteri
 *          - password con più di 20 caratteri
 *          - password senza nemmeno un carattere speciale
 *          - password senza nemmeno un carattere minuscolo
 *          - password senza nemmeno un carattere maiuscolo
 *          - password senza nemmeno una cifra tra 0 e 9
 */
public class SignUpParamsValidationTest {
    private SignUpParamsValidationMock mock;
    private String validEmail, validPassword, validName, validSurname;

    @Before
    public void setUp(){
        mock = new SignUpParamsValidationMock();

        validEmail = "rat23admin@gmail.com";
        validPassword = "LemaoUbuntu95!!";
        validName =  "Gennaro";
        validSurname = "Bullo";
    }

    @Test
    public void allParamsValid(){
        Assert.assertTrue(mock.validSignUpParams(validEmail, validPassword, validName, validSurname));
    }

    @Test
    public void emailNull(){
        Assert.assertFalse(mock.validSignUpParams(null, validPassword, validName, validSurname));
    }

    @Test
    public void passwordNull(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, null, validName, validSurname));
    }

    @Test
    public void nameNull(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, validPassword, null, validSurname));
    }

    @Test
    public void surnameNull(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, validPassword, validName, null));
    }

    @Test
    public void emailEmpty(){
        Assert.assertFalse(mock.validSignUpParams("",validPassword, validName, validSurname));
    }

    @Test
    public void nameEmpty(){
        Assert.assertFalse(mock.validSignUpParams(validEmail,validPassword,"",validSurname));
    }

    @Test
    public void surnameEmpty(){
        Assert.assertFalse(mock.validSignUpParams(validEmail,validPassword,validName,""));
    }

    @Test
    public void emailNoAt(){
        Assert.assertFalse(mock.validSignUpParams("rat23admin.com",validPassword,validName,validSurname));
    }

    @Test
    public void emailNoDomain(){
        Assert.assertFalse(mock.validSignUpParams("rat23admin", validPassword, validName, validSurname));
    }

    @Test
    public void emailWithSpecialChars(){
        Assert.assertFalse(mock.validSignUpParams("rat23#admin@gmail.com", validPassword, validName, validSurname));
    }

    @Test
    public void passwordTooShort(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, "Le12%", validName, validSurname));
    }

    @Test
    public void passwordTooLong(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, "LemaoUbuntu95!!P33h9z%27c421799999", validName, validSurname));
    }

    @Test
    public void passwordNoSpecialChars(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, "LemaoUbuntu95", validName, validSurname));
    }

    @Test
    public void passwordNoUpperCase(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, "lemaoubuntu95!!", validName, validSurname));
    }

    @Test
    public void passwordNoLowerCase(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, "LEMAOUBUNTU95!!", validName, validSurname));
    }

    @Test
    public void passwordNoDigits(){
        Assert.assertFalse(mock.validSignUpParams(validEmail, "LemaoUbuntu!!", validName, validSurname));
    }
}

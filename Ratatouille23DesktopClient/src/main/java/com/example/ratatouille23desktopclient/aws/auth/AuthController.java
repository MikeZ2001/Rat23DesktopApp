package com.example.ratatouille23desktopclient.aws.auth;

import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.exceptions.ForcePasswordChangeException;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.enums.Role;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuthController {
    private final String userPoolID = "eu-central-1_xw97ibnEg";
    private final String clientID = "54kq6of0h1oc6f812cmm195504";

    private String session;

    /**
     * Crea un nuovo utente in stato "Forza nuova password".
     * L'utente al primo sign-in dovrà inserire quindi la sua nuova password dopo aver usato
     * quella fornita dall'amministratore.
     * @param employee
     * @throws InvalidPasswordException quando la password non rispetta la policy dello user pool.
     * @throws InvalidParameterException quando i parametri passati sono errati.
     * @throws UsernameExistsException quando esiste già un account con quella email.
     * @throws Exception
     */
    public void createEmployee(Employee employee, String password) throws InvalidPasswordException, InvalidParameterException, UsernameExistsException, Exception{
        String email = employee.getEmail();
        Role role = employee.getRole();
        String given_name = employee.getName();
        String family_name = employee.getSurname();

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        AttributeType roleAttr = AttributeType.builder()
                .name("custom:role")
                .value(role.name())
                .build();

        AttributeType nameAttr = AttributeType.builder()
                .name("given_name")
                .value(given_name)
                .build();

        AttributeType familyAttr = AttributeType.builder()
                .name("family_name")
                .value(family_name)
                .build();

        ArrayList<AttributeType> attributes = new ArrayList<>();
        attributes.add(roleAttr);
        attributes.add(nameAttr);
        attributes.add(familyAttr);

        AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolID)
                .username(email)
                .temporaryPassword(password)
                .userAttributes(attributes)
                .messageAction("SUPPRESS")
                .build() ;

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(userRequest);

        cognitoClient.close();
    }

    public void editEmployee(Employee employee) throws InvalidParameterException, InvalidPasswordException, Exception{
        String email = employee.getEmail();
        Role role = employee.getRole();
        String given_name = employee.getName();
        String family_name = employee.getSurname();

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        AttributeType roleAttr = AttributeType.builder()
                .name("custom:role")
                .value(role.name())
                .build();

        AttributeType nameAttr = AttributeType.builder()
                .name("given_name")
                .value(given_name)
                .build();

        AttributeType familyAttr = AttributeType.builder()
                .name("family_name")
                .value(family_name)
                .build();

        ArrayList<AttributeType> attributes = new ArrayList<>();
        attributes.add(roleAttr);
        attributes.add(nameAttr);
        attributes.add(familyAttr);

        AdminUpdateUserAttributesRequest req = AdminUpdateUserAttributesRequest.builder()
                .username(email)
                .userAttributes(attributes)
                .userPoolId(userPoolID)
                .build();

        AdminUpdateUserAttributesResponse res = cognitoClient.adminUpdateUserAttributes(req);

        cognitoClient.close();
    }

    /**
     * Esegue la registrazione dell'account amministratore.
     * @param email
     * @param given_name
     * @param family_name
     * @param password
     * @throws InvalidParameterException quando i parametri passati violano qualche policy o sono assenti.
     * @throws UsernameExistsException quando l'email passata è già usata per un altro account.
     * @throws InvalidPasswordException quando la password viola la policy della user pool.
     * @throws Exception
     */
    public void signUpAdmin(String email, String password,String given_name, String family_name)
            throws InvalidParameterException, UsernameExistsException, InvalidPasswordException, Exception{
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        AttributeType roleAttr = AttributeType.builder()
                .name("custom:role")
                .value(Role.ADMINISTRATOR.name())
                .build();

        AttributeType nameAttr = AttributeType.builder()
                .name("given_name")
                .value(given_name)
                .build();

        AttributeType familyAttr = AttributeType.builder()
                .name("family_name")
                .value(family_name)
                .build();

        ArrayList<AttributeType> attributes = new ArrayList<>();
        attributes.add(roleAttr);
        attributes.add(nameAttr);
        attributes.add(familyAttr);

        SignUpRequest adminSignUpRequest = SignUpRequest.builder()
                .username(email)
                .clientId(clientID)
                .userAttributes(attributes)
                .password(password)
                .build();

        SignUpResponse adminSignUpResponse = cognitoClient.signUp(adminSignUpRequest);

        cognitoClient.close();
    }

    /**
     * Invia il codice di conferma dell'account a Cognito.
     * @param verificationCode
     * @param email
     * @throws Exception
     */
    public void confirmCode(String email, String verificationCode) throws Exception{
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(clientID)
                .username(email)
                .confirmationCode(verificationCode)
                .build();

        ConfirmSignUpResponse confirmSignUpResponse = cognitoClient.confirmSignUp(confirmSignUpRequest);

        cognitoClient.close();
    }

    /**
     * Invia di nuovo il codice di conferma dell'account per email.
     * @param email
     * @throws Exception
     */
    public void resendCode(String email) throws Exception{
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        ResendConfirmationCodeRequest request = ResendConfirmationCodeRequest.builder().clientId(clientID).username(email).build();

        cognitoClient.resendConfirmationCode(request);

        cognitoClient.close();
    }

    /**
     * Effettua il login con le credenziali nella userpool Cognito.
     * @param email
     * @param password
     * @throws ForcePasswordChangeException in caso fosse necessario cambiare la password
     * @throws Exception
     */
    public void login(String email, String password) throws ForcePasswordChangeException, UserNotConfirmedException, Exception{
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", email);
        authParams.put("PASSWORD", password);


        InitiateAuthRequest loginReq = InitiateAuthRequest.builder()
                .clientId(clientID)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .build();

        InitiateAuthResponse res = cognitoClient.initiateAuth(loginReq);

        if (res.challengeName() != null && res.challengeName().equals(ChallengeNameType.NEW_PASSWORD_REQUIRED)){
            session = res.session();
            throw new ForcePasswordChangeException();
        }else{
            RAT23Cache<String, String> cache = RAT23Cache.getCacheInstance();
            cache.put("accessToken", res.authenticationResult().accessToken().toString());
            getCurrentUser(res.authenticationResult().accessToken());
        }
        cognitoClient.close();
    }

    /**
     * Risponde alla challange di Cognito che forza la creazione di una nuova password inviandola.
     * @param email
     * @param newPassword
     * @throws Exception
     */
    public void forceChangePassword(String email, String newPassword) throws Exception{
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        Map<String, String> responses = new HashMap<>();
        responses.put("USERNAME", email);
        responses.put("NEW_PASSWORD", newPassword);

        RespondToAuthChallengeRequest req = RespondToAuthChallengeRequest.builder()
                .clientId(clientID)
                .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .challengeResponses(responses)
                .session(this.session)
                .build();

        RespondToAuthChallengeResponse res = cognitoClient.respondToAuthChallenge(req);

        session = null;
        cognitoClient.close();
    }

    /**
     * Ottiene l'utente attualmente loggato tramite accessToken per salvare alcuni suoi attributi in cache.
     * @param accessToken
     * @throws Exception
     */
    private void getCurrentUser(String accessToken) throws Exception{
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        GetUserRequest request = GetUserRequest.builder()
                .accessToken(accessToken)
                .build();

        GetUserResponse response = cognitoClient.getUser(request);

        Employee currentUser = new Employee();
        for (AttributeType attribute : response.userAttributes()){
            if (attribute.name().equals("given_name"))
                currentUser.setName(attribute.value());
            if (attribute.name().equals("family_name"))
                currentUser.setSurname(attribute.value());
            if (attribute.name().equals("custom:role"))
                currentUser.setRole(Role.valueOf(attribute.value()));
            if (attribute.name().equals("email"))
                currentUser.setEmail(attribute.value());
        }

        RAT23Cache<String, String> cache = RAT23Cache.getCacheInstance();
        cache.put("currentUserEmail", currentUser.getEmail());
        cache.put("currentUserGivenName", currentUser.getName());
        cache.put("currentUserFamilyName", currentUser.getSurname());
        cache.put("currentUserRole", currentUser.getRole().name());

        cognitoClient.close();
    }

    /**
     * Cerca nella user pool il dipendente con l'email ricevuta in input.
     * @param email
     * @return employee = dipendente trovato nella user pool con tale email
     * @throws Exception
     */
    public Employee getUserByEmail(String email) throws Exception {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        AdminGetUserRequest request = AdminGetUserRequest.builder()
                .userPoolId(userPoolID)
                .username(email)
                .build();

        Employee employee = new Employee();
        employee.setEmail(email);
        AdminGetUserResponse response = cognitoClient.adminGetUser(request);
        for (AttributeType attribute: response.userAttributes()){
            if (attribute.name().equals("given_name"))
                employee.setName(attribute.value());
            if (attribute.name().equals("family_name"))
                employee.setSurname(attribute.value());
            if (attribute.name().equals("custom:role"))
                employee.setRole(Role.valueOf(attribute.value()));
        }

        cognitoClient.close();

        return employee;
    }

    /**
     * Invia un codice di conferma per il reset della password via email.
     * @param email
     * @throws InvalidParameterException nel caso l'email fosse sbagliata.
     * @throws Exception
     */
    public void resetPassword(String email) throws InvalidParameterException, Exception{
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .clientId(clientID)
                .username(email)
                .build();

        ForgotPasswordResponse res = cognitoClient.forgotPassword(request);

        cognitoClient.close();
    }

    /**
     * Invia il codice di conferma ricevuto per il reset della password e la nuova password per cambiarla.
     * @param email
     * @param verificationCode
     * @param newPassword
     * @throws InvalidPasswordException quando la password viola la policy della user pool.
     * @throws InvalidParameterException quando i parametri inviati sono errati.
     * @throws Exception
     */
    public void confirmResetPassword(String email, String verificationCode, String newPassword) throws InvalidParameterException, InvalidPasswordException, Exception{
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        ConfirmForgotPasswordRequest request = ConfirmForgotPasswordRequest.builder()
                .clientId(clientID)
                .username(email)
                .password(newPassword)
                .confirmationCode(verificationCode)
                .build();

        ConfirmForgotPasswordResponse res = cognitoClient.confirmForgotPassword(request);

        cognitoClient.close();
    }

    /**
     * Esegue il logout per l'utente attualmente autenticato.
     * @throws Exception
     */
    public void logout() throws Exception {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        RAT23Cache<String, String> cache = RAT23Cache.getCacheInstance();

        GlobalSignOutRequest req = GlobalSignOutRequest.builder()
                .accessToken(cache.get("accessToken"))
                .build();

        GlobalSignOutResponse res = cognitoClient.globalSignOut(req);

        cache.cacheCleanUp();

        cognitoClient.close();
    }

    /**
     * Elimina l'account di un utente individuato dall'email in input.
     * @param email
     * @throws UserNotFoundException quando l'utente non è stato trovato
     * @throws Exception
     */
    public void deleteUser(String email) throws InvalidParameterException, UserNotFoundException, Exception {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        AdminDeleteUserRequest request = AdminDeleteUserRequest.builder()
                .userPoolId(userPoolID)
                .username(email)
                .build();

        AdminDeleteUserResponse res = cognitoClient.adminDeleteUser(request);

        cognitoClient.close();
    }
}

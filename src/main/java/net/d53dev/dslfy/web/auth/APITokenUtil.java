package net.d53dev.dslfy.web.auth;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by davidsere on 16/11/15.
 */
public enum APITokenUtil {

    INSTANCE;

    private final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    private final String separator = "|%^%|";
    private final String separatorPattern = Pattern.quote(separator);
    private final String uuidPattern = "(?i)^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$";
    private final String simpleCheckToken = "dslfy.web";
    private final Period tokenValidity = Period.ofDays(30);

    // TODO: fix this crap
    private String secret = "very_secret_string";

    private void setupEncryptor() {


    }

    public String getToken(String username) {
        if (!encryptor.isInitialized()) {
            encryptor.setPassword(secret);
        }

        String token = UUID.randomUUID().toString() +
                separator + simpleCheckToken +
                separator + username +
                separator + Instant.now().plus(tokenValidity).toString(); //30 days

        return encryptor.encrypt(token);
    }

    public boolean validateToken(String token, String username) {
        if (token == null || username == null) {
            return false;
        }

        if (!encryptor.isInitialized()) {
            encryptor.setPassword(secret);
        }

        String decryptedMessage = encryptor.decrypt(token);
        String[] subTokens = decryptedMessage.split(separatorPattern);

        if (subTokens.length != 4) {
            return false;
        }

        if (!subTokens[0].matches(uuidPattern)) {
            return false;
        }
        if (!subTokens[1].equals(simpleCheckToken)) {
            return false;
        }
        if (!subTokens[2].equals(username)) {
            return false;
        }
        Instant expirationDate;
        try {
            expirationDate = Instant.parse(subTokens[3]);
        } catch (DateTimeParseException e) {
            return false;
        }
        if (Instant.now().isAfter(expirationDate)){
            return false;
        }

        return true;
    }
}


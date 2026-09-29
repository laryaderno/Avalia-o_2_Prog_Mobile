package com.example.avaliacao1;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/** Criptografia de senha: SHA-256 com salt aleatório por usuário. */
public final class PasswordUtils {

    private static final int TAMANHO_SALT_BYTES = 16;

    private PasswordUtils() { }

    public static String gerarSalt() {
        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hash(String senha, String saltBase64) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.decode(saltBase64, Base64.NO_WRAP));
            byte[] resultado = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(resultado, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 existe em todo Android
            throw new IllegalStateException(e);
        }
    }

    /** Compara o hash da senha digitada com o hash salvo (tempo constante). */
    public static boolean verificar(String senhaDigitada, String saltBase64, String hashSalvo) {
        byte[] calculado = hash(senhaDigitada, saltBase64).getBytes(StandardCharsets.UTF_8);
        byte[] esperado = hashSalvo.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(calculado, esperado);
    }
}

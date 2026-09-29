package com.example.avaliacao1;

/** Resultado simples de uma operação assíncrona (cadastro, login, edição). */
public class Resultado<T> {

    public final boolean sucesso;
    public final String mensagem;
    public final T dado;

    private Resultado(boolean sucesso, String mensagem, T dado) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.dado = dado;
    }

    public static <T> Resultado<T> ok(T dado) {
        return new Resultado<>(true, null, dado);
    }

    public static <T> Resultado<T> erro(String mensagem) {
        return new Resultado<>(false, mensagem, null);
    }

    public interface Callback<T> {
        /** Sempre chamado na thread principal. */
        void aoTerminar(Resultado<T> resultado);
    }
}

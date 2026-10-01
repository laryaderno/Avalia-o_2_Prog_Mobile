package com.example.avaliacao1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.color.MaterialColors;

import java.util.Locale;

public final class ToolbarHelper {

    private ToolbarHelper() {
    }

    public static void atualizar(Toolbar toolbar, Usuario usuario) {
        if (usuario == null) {
            toolbar.setSubtitle(null);
            toolbar.setLogo(null);
            toolbar.setLogoDescription(null);
            return;
        }

        toolbar.setSubtitle(usuario.nome);

        toolbar.setLogoDescription(
                toolbar.getContext().getString(
                        R.string.p2_avatar_usuario,
                        usuario.nome
                )
        );

        int tamanho = Math.max(
                1,
                Math.round(
                        40 * toolbar.getResources().getDisplayMetrics().density
                )
        );

        Bitmap avatar = Bitmap.createBitmap(
                tamanho,
                tamanho,
                Bitmap.Config.ARGB_8888
        );

        avatar.setDensity(
                toolbar.getResources().getDisplayMetrics().densityDpi
        );

        Canvas canvas = new Canvas(avatar);

        Paint pincel = new Paint(
                Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
        );

        Bitmap foto = null;

        if (usuario.foto != null && usuario.foto.length > 0) {
            foto = BitmapFactory.decodeByteArray(
                    usuario.foto,
                    0,
                    usuario.foto.length
            );
        }

        if (foto != null) {
            BitmapShader shader = new BitmapShader(
                    foto,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
            );

            float escala = Math.max(
                    (float) tamanho / foto.getWidth(),
                    (float) tamanho / foto.getHeight()
            );

            Matrix matriz = new Matrix();
            matriz.setScale(escala, escala);

            matriz.postTranslate(
                    (tamanho - foto.getWidth() * escala) / 2f,
                    (tamanho - foto.getHeight() * escala) / 2f
            );

            shader.setLocalMatrix(matriz);
            pincel.setShader(shader);

            canvas.drawCircle(
                    tamanho / 2f,
                    tamanho / 2f,
                    tamanho / 2f,
                    pincel
            );
        } else {
            pincel.setColor(
                    MaterialColors.getColor(
                            toolbar,
                            androidx.appcompat.R.attr.colorPrimary
                    )
            );

            canvas.drawCircle(
                    tamanho / 2f,
                    tamanho / 2f,
                    tamanho / 2f,
                    pincel
            );

            pincel.setColor(
                    MaterialColors.getColor(
                            toolbar,
                            com.google.android.material.R.attr.colorOnPrimary
                    )
            );

            pincel.setTextSize(tamanho * 0.45f);
            pincel.setTextAlign(Paint.Align.CENTER);

            pincel.setTypeface(
                    Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            );

            String nome = usuario.nome.trim();

            String inicial = nome.isEmpty()
                    ? "?"
                    : nome.substring(
                    0,
                    nome.offsetByCodePoints(0, 1)
            ).toUpperCase(Locale.getDefault());

            float centro = tamanho / 2f
                    - (pincel.ascent() + pincel.descent()) / 2f;

            canvas.drawText(
                    inicial,
                    tamanho / 2f,
                    centro,
                    pincel
            );
        }

        toolbar.setLogo(
                new BitmapDrawable(toolbar.getResources(), avatar)
        );
    }
}
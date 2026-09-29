package com.example.avaliacao1;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalheActivity extends AppCompatActivity {

    public static final String EXTRA_TITULO = "extra_titulo";
    public static final String EXTRA_DESCRICAO = "extra_descricao";
    public static final String EXTRA_IMAGEM = "extra_imagem";
    public static final String EXTRA_SOM = "extra_som";

    private MediaPlayer mediaPlayer;
    private Button btnPlay;
    private SeekBar seekBar;
    private TextView txtTempo;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        String titulo = getIntent().getStringExtra(EXTRA_TITULO);
        String descricao = getIntent().getStringExtra(EXTRA_DESCRICAO);
        int imagem = getIntent().getIntExtra(EXTRA_IMAGEM, 0);
        int som = getIntent().getIntExtra(EXTRA_SOM, 0);

        TextView txtTitulo = findViewById(R.id.txtDetalheTitulo);
        TextView txtDescricao = findViewById(R.id.txtDetalheDescricao);
        ImageView imgDetalhe = findViewById(R.id.imgDetalhe);

        btnPlay = findViewById(R.id.btnPlay);
        seekBar = findViewById(R.id.seekBar);
        txtTempo = findViewById(R.id.txtTempo);

        txtTitulo.setText(titulo);
        txtDescricao.setText(descricao);

        if (imagem != 0) {
            imgDetalhe.setImageResource(imagem);
        }

        if (som != 0) {

            mediaPlayer = MediaPlayer.create(this, som);

            if (mediaPlayer != null) {

                seekBar.setMax(mediaPlayer.getDuration());

                txtTempo.setText(
                        "0:00 / " + formatarTempo(mediaPlayer.getDuration())
                );

                btnPlay.setOnClickListener(v -> {

                    if (mediaPlayer.isPlaying()) {

                        mediaPlayer.pause();
                        btnPlay.setText("▶");

                    } else {

                        mediaPlayer.start();
                        btnPlay.setText("II");
                        atualizarProgresso();
                    }
                });

                seekBar.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onProgressChanged(
                                    SeekBar seekBar,
                                    int progress,
                                    boolean fromUser
                            ) {
                                if (fromUser && mediaPlayer != null) {
                                    mediaPlayer.seekTo(progress);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(
                                    SeekBar seekBar
                            ) {
                            }

                            @Override
                            public void onStopTrackingTouch(
                                    SeekBar seekBar
                            ) {
                            }
                        }
                );

                mediaPlayer.setOnCompletionListener(mp -> {

                    seekBar.setProgress(0);

                    txtTempo.setText(
                            "0:00 / " +
                                    formatarTempo(mediaPlayer.getDuration())
                    );

                    btnPlay.setText("▶ Reproduzir");
                });
            }

        } else {

            btnPlay.setEnabled(false);
            btnPlay.setText("Som indisponível");
        }
    }

    private void atualizarProgresso() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            int progresso = mediaPlayer.getCurrentPosition();

            seekBar.setProgress(progresso);

            txtTempo.setText(
                    formatarTempo(progresso)
                            + " / "
                            + formatarTempo(mediaPlayer.getDuration())
            );

            handler.postDelayed(
                    this::atualizarProgresso,
                    500
            );
        }
    }

    private String formatarTempo(int milissegundos) {

        int segundos = milissegundos / 1000;

        int minutos = segundos / 60;

        segundos = segundos % 60;

        return String.format(
                "%d:%02d",
                minutos,
                segundos
        );
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        handler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
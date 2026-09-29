package com.example.avaliacao1;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;

import androidx.activity.EdgeToEdge;

import com.example.avaliacao1.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private AppViewModel appViewModel;

    // Estado da sessão / navegação inferior
    private boolean sessaoCarregada = false;   // já recebemos a 1ª emissão do Room?
    private boolean logado = false;
    private boolean navInferiorPermitida = false;
    private boolean navInferiorEscondidaPorScroll = false;

    private static final String prefs_nome = "orbis_prefs";
    private static final String key_theme = "tema_escolhido";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applyChosenTheme();


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Destinos "de topo" (sem seta de voltar): Login + as 3 abas.
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.LoginFragment, R.id.FirstFragment,
                    R.id.SecondFragment, R.id.ThirdFragment
            ).build();
            NavigationUI.setupActionBarWithNavController(
                    this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNav, navController);

            // Navegação inferior só aparece fora da tela de login.
            navController.addOnDestinationChangedListener(
                    (controller, destino, args) -> atualizarNavegacaoInferior(destino));

            // Observer da sessão: login -> abre o app; logout -> volta ao login.
            appViewModel.getUsuarioAtivo().observe(this, usuario -> {
                sessaoCarregada = true;
                logado = usuario != null;
                sincronizarSessaoComNavegacao();
                atualizarToolbar(usuario);
                invalidateOptionsMenu();   // mostra/oculta Editar perfil e Sair
            });
        }
    }

    /** Leva o usuário para a tela certa conforme haja ou não sessão ativa. */
    private void sincronizarSessaoComNavegacao() {
        if (navController == null || !sessaoCarregada) return;
        NavDestination atual = navController.getCurrentDestination();
        if (atual == null) return;

        boolean naTelaDeLogin = atual.getId() == R.id.LoginFragment;
        if (logado && naTelaDeLogin) {
            navController.navigate(R.id.action_global_first);
        } else if (!logado && !naTelaDeLogin) {
            navController.navigate(R.id.action_global_login);
        }
        atualizarNavegacaoInferior(navController.getCurrentDestination());
    }

    private void atualizarNavegacaoInferior(NavDestination destino) {
        boolean emLogin = destino != null && destino.getId() == R.id.LoginFragment;
        navInferiorPermitida = logado && !emLogin;
        binding.bottomNav.setVisibility(navInferiorPermitida ? View.VISIBLE : View.GONE);
        binding.bottomNav.setTranslationY(0f);
        navInferiorEscondidaPorScroll = false;
    }

    /** Chamado pelos fragmentos ao rolar a lista para baixo. */
    public void esconderBottomNavigation() {
        if (!navInferiorPermitida || navInferiorEscondidaPorScroll) return;
        navInferiorEscondidaPorScroll = true;
        binding.bottomNav.animate()
                .translationY(binding.bottomNav.getHeight())
                .setDuration(200)
                .start();
    }

    /** Chamado pelos fragmentos ao rolar a lista para cima. */
    public void mostrarBottomNavigation() {
        if (!navInferiorPermitida || !navInferiorEscondidaPorScroll) return;
        navInferiorEscondidaPorScroll = false;
        binding.bottomNav.animate()
                .translationY(0f)
                .setDuration(200)
                .start();
    }

    /**
     * TODO (Pessoa 2): avatar (usuario.foto em byte[] -> Bitmap redondo) e nome na Toolbar.
     * Chamado a cada mudança de sessão/perfil; usuario == null quando deslogado.
     */
    private void atualizarToolbar(Usuario usuario) {
        // TODO (Pessoa 2)
    }

    private void abrirEdicaoDePerfil() {
        Intent i = new Intent(this, CadastroActivity.class);
        i.putExtra(CadastroActivity.EXTRA_MODO, CadastroActivity.MODO_EDICAO);
        startActivity(i);
    }

    /** Encerra a sessão no Room. O observer acima cuida de voltar ao login. */
    public void sair() {
        appViewModel.logout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem editar = menu.findItem(R.id.action_editar_perfil);
        MenuItem sair = menu.findItem(R.id.action_sair);
        if (editar != null) editar.setVisible(logado);
        if (sair != null) sair.setVisible(logado);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_editar_perfil) {
            abrirEdicaoDePerfil();
            return true;
        }
        if (id == R.id.action_sair) {
            sair();
            return true;
        }
        if (id == R.id.action_settings) {
            mostrarDialogoDeTema();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoDeTema() {
        String[] opcoes = {
                getString(R.string.tema_claro),
                getString(R.string.tema_escuro),
                getString(R.string.tema_sistema)
        };

        int modoAtual = getSharedPreferences(prefs_nome, MODE_PRIVATE)
                .getInt(key_theme, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        int indiceSelecionado;
        if (modoAtual == AppCompatDelegate.MODE_NIGHT_NO) {
            indiceSelecionado = 0;
        } else if (modoAtual == AppCompatDelegate.MODE_NIGHT_YES) {
            indiceSelecionado = 1;
        } else {
            indiceSelecionado = 2;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_dialogo_tema)
                .setSingleChoiceItems(opcoes, indiceSelecionado, (dialog, which) -> {
                    int novoModo;
                    if (which == 0) {
                        novoModo = AppCompatDelegate.MODE_NIGHT_NO;
                    } else if (which == 1) {
                        novoModo = AppCompatDelegate.MODE_NIGHT_YES;
                    } else {
                        novoModo = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    }

                    salvarTema(novoModo);
                    AppCompatDelegate.setDefaultNightMode(novoModo);

                    dialog.dismiss();
                })
                .show();
    }

    private void salvarTema(int modo) {
        SharedPreferences.Editor editor =
                getSharedPreferences(prefs_nome, MODE_PRIVATE).edit();
        editor.putInt(key_theme, modo);
        editor.apply();
    }

    private void applyChosenTheme() {
        int modoSalvo = getSharedPreferences(prefs_nome, MODE_PRIVATE)
                .getInt(key_theme, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(modoSalvo);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        boolean handled = false;
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            handled = NavigationUI.navigateUp(navController, appBarConfiguration);
        }
        return handled || super.onSupportNavigateUp();
    }

}

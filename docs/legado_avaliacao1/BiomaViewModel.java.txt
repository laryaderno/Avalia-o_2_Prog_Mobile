package com.example.avaliacao1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BiomaViewModel extends ViewModel {

    private final MutableLiveData<Bioma> biomaSelecionado = new MutableLiveData<>();

    public void selecionarBioma(Bioma bioma) {
        biomaSelecionado.setValue(bioma);
    }

    public LiveData<Bioma> getBiomaSelecionado() {
        return biomaSelecionado;
    }
}
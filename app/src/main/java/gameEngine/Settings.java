package gameEngine;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;

import game.danielesimone.briscola.R;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Settings {

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createSettingsMenu(Context c)
    {
        Dialog dialog = new Dialog(c);
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( LAYOUT_INFLATER_SERVICE );

        dialog.setContentView(R.layout.settings);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // PARENT VIEW;
        View settings = inflater.inflate(R.layout.settings, null);
        
        // SPINNER TIPO CARTE;
        Spinner tipoCarte = dialog.findViewById(R.id.settings_typeCardSpinner);
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, c.getResources().getStringArray(R.array.tipoCarte));
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoCarte.setAdapter(adapterTipo);
        String selectedTipo = SharedPref.getTipoCarte();
        List<String> opzioniTipo = Arrays.asList(c.getResources().getStringArray(R.array.tipoCarte));
        opzioniTipo = opzioniTipo.stream().map(String::toLowerCase).collect(Collectors.toList());
        int indexTipo = opzioniTipo.indexOf(selectedTipo);
        indexTipo = indexTipo == -1 ? 0 : indexTipo;
        tipoCarte.setSelection(indexTipo);

        // CHECKBOX;
        CheckBox carteScoperte = dialog.findViewById(R.id.settings_showCardCheckbox);
        carteScoperte.setChecked(SharedPref.getCarteScoperte());
        
        // SPINNER SKILL CPU;
        Spinner skillCpu = dialog.findViewById(R.id.settings_cpuSkillSpinner);
        ArrayAdapter<String> adapterSkill = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, c.getResources().getStringArray(R.array.difficulties));
        adapterSkill.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillCpu.setAdapter(adapterSkill);
        skillCpu.setSelection(SharedPref.getCPUSkill());

        // CONFIRM BUTTON;
        View confirmButton = dialog.findViewById(R.id.settings_accept);
        confirmButton.setOnClickListener(v -> {
            if(carteScoperte.isChecked()){
                SharedPref.setCarteScoperte(true);
                if(Game.CPU != null)
                    Game.CPU.scopriCarte();
            }else{
                SharedPref.setCarteScoperte(false);
                if(Game.CPU != null)
                    Game.CPU.copriCarte();
            }

            int skillValue = skillCpu.getSelectedItemPosition();
            SharedPref.setCPUSkill(skillValue);
            if(Game.CPU != null)
                Game.CPU.setSkill(skillValue);

            String tipoCarteValue = tipoCarte.getSelectedItem().toString();
            Engine.aggiornaTipoCarte(tipoCarteValue);

            dialog.dismiss();
        });
        
        View closeButton = dialog.findViewById(R.id.settings_close);
        View cancelButton = dialog.findViewById(R.id.settings_cancel);
        
        View.OnClickListener closeAction = v -> dialog.dismiss();

        closeButton.setOnClickListener(closeAction);
        cancelButton.setOnClickListener(closeAction);
        
        dialog.create();
        dialog.show();
    }
}

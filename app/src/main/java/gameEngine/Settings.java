package gameEngine;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( LAYOUT_INFLATER_SERVICE );

        // VIEW;
        View tipoCarteView = inflater.inflate(R.layout.settings, null);

        // SPINNER TIPO CARTE;
        Spinner tipoCarte = tipoCarteView.findViewById(R.id.typeCardSpinner);
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item,c.getResources().getStringArray(R.array.tipoCarte));
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoCarte.setAdapter(adapterTipo);
        String selectedTipo = SharedPref.getTipoCarte();
        List<String> opzioniTipo = Arrays.asList(c.getResources().getStringArray(R.array.tipoCarte));
        opzioniTipo = opzioniTipo.stream().map(String::toLowerCase).collect(Collectors.toList());
        int indexTipo = opzioniTipo.indexOf(selectedTipo);
        indexTipo = indexTipo == -1 ? 0 : indexTipo;
        tipoCarte.setSelection(indexTipo);

        // CHECKBOX;
        CheckBox carteScoperte = tipoCarteView.findViewById(R.id.showCardCheckbox);
        carteScoperte.setChecked(SharedPref.getCarteScoperte());


        // SPINNER SKILL CPU;
        Spinner skillCpu = tipoCarteView.findViewById(R.id.cpuSkillSpinner);
        ArrayAdapter<String> adapterSkill = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item, c.getResources().getStringArray(R.array.difficulties));
        adapterSkill.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillCpu.setAdapter(adapterSkill);
        skillCpu.setSelection(SharedPref.getCPUSkill());

        // CONFIRM;
        builder.setPositiveButton(c.getString(R.string.ok), (dialog, id) -> {
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
        });

        builder.setNegativeButton(c.getString(R.string.cancel), null);
        builder.setView(tipoCarteView);

        builder.create().show();
    }

}

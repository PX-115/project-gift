package com.example.project_gift.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.project_gift.R;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.enums.AulaState;
import com.example.project_gift.model.Student;
import com.example.project_gift.model.Teacher;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

public class StudentHomeFragment extends Fragment {

    private StudentHomeViewModel studentHomeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        studentHomeViewModel =
                new ViewModelProvider(this).get(StudentHomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_student_home, container, false);
        final TextView textView = root.findViewById(R.id.text_user);
        final TextView textCurso = root.findViewById(R.id.text_curso);
        final TextView textStatus = root.findViewById(R.id.text_status);
        final TextView textHora = root.findViewById(R.id.text_hora);
        final TextView textDisciplina = root.findViewById(R.id.text_disciplina);
        final ImageView imageView = root.findViewById(R.id.imageView);
        final MaterialButton buttonSave = root.findViewById(R.id.buttonSave);

        studentHomeViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        studentHomeViewModel.getCurso().observe(getViewLifecycleOwner(), s -> textCurso.setText(s));
        studentHomeViewModel.getLoginFormState().observe(getViewLifecycleOwner(), studentHomeFormState -> {
            if (studentHomeFormState == null) {
                return;
            }
            if(studentHomeFormState.getAulaState() != AulaState.NAO_ENCONTRADA.getValue()) {
                textHora.setVisibility(View.VISIBLE);
                textDisciplina.setVisibility(View.VISIBLE);
            } else {
                textHora.setVisibility(View.GONE);
                textDisciplina.setVisibility(View.GONE);
            }
        });
        studentHomeViewModel.getStatus().observe(getViewLifecycleOwner(), s -> textStatus.setText(s));
        studentHomeViewModel.getDisciplina().observe(getViewLifecycleOwner(), s -> textDisciplina.setText(s));
        studentHomeViewModel.getDate().observe(getViewLifecycleOwner(), s -> textHora.setText(s));

        return root;
    }


}
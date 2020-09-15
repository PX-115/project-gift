package unis.edu.project_gift.model;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class UsuarioViewModel {
    private void waitTaskCompleted(Task task) {


        while (true) {
            if(task.isComplete()) {
                break;
            }
        }
    }
}

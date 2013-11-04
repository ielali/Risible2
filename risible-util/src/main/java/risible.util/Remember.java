package risible.util;

import java.util.ArrayList;
import java.util.List;

public class Remember {
    private static final ThreadLocal<List<Runnable>> todos = new ThreadLocal() {
        protected List<Runnable> initialValue() {
            return new ArrayList();
        }
    };

    public void doTodos() {
        synchronized (todos) {
            List<Runnable> todos = getTodos();
            try {
                for (Runnable todo : todos) {
                    todo.run();
                }
            } finally {
                todos.clear();
            }
        }
    }

    private static List<Runnable> getTodos() {
        return todos.get();
    }

    public void todo(Runnable todo) {
        getTodos().add(todo);
    }
}

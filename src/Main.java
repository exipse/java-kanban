import Model.*;

public class Main {

    public static void main(String[] args) {
        Manager man = new Manager();

        ///  Проверка методов Тасок
        man.createTask(new Task("Таска - 1", "Описание 1...", Task.Status.NEW));
        man.createTask(new Task("Таска - 2", "Описание 2...", Task.Status.IN_PROGRESS));
        man.createEpic(new Epic("Эпик 1", "Эпик с 2 подзадачами"));
        man.createSubTask(new SubTask("СабТаска 1из2 в эпике 1 ", "Описание сабтаски 1", Task.Status.DONE, 3));
        man.createSubTask(new SubTask("СабТаска 2из2 в эпике 1 ", "Описание сабтаски 2", Task.Status.IN_PROGRESS, 3));
        man.createEpic(new Epic("Эпик 2", "Эпик с 1 подзадачей"));
        man.createSubTask(new SubTask("СабТаска 1 в эпике 2 ", "Описание сабтаски", Task.Status.IN_PROGRESS, 6));

        man.getTaskList();
        man.getEpicList();
        man.getSubTaskList();

        //Обновление таски
        Task task = man.tasks.get(1);
        if (task != null) {
            task.setName("Обновленная Таска - 1");
            task.setDescription("Новое Описание 1");
            task.setStatus(Task.Status.IN_PROGRESS);
            man.updateTask(task);
        } else {
            System.out.println("\n" + "Объекта под введенным идентификатором не найдено. Изменить объект нельзя.");
        }

        //Обновление эпика
        Epic epic = man.epics.get(3);
        if (epic != null) {
            epic.setName("Измененный Эпик 1");
            epic.setDescription("Обновленное Описание");
            man.updateEpic(epic);
        } else {
            System.out.println("\n" + "Объекта под введенным идентификатором не найдено. Изменить объект нельзя.");
        }

        //Обновление саптаски
        SubTask sub = man.subtasks.get(5);
        if (sub != null) {
            sub.setName("НОВАЯ- СабТаска 2из2 в эпике 1");
            sub.setDescription("НОВОЕ Описание 2");
            sub.setStatus(Task.Status.DONE);
            man.updateSubTask(sub);
        } else {
            System.out.println("\n" + "Объекта под введенным идентификатором не найдено. Изменить объект нельзя.");
        }

        man.getTaskList();
        man.getEpicList();
        man.getSubTaskList();

        man.deleteSubTaskById(7);
        man.deleteEpicById(3);
        man.getEpicList();
        man.getSubTaskList();

    }
}

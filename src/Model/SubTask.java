package Model;

public class SubTask extends Task {

    public int getEpicId() {
        return epicId;
    }

    int epicId;

    public SubTask(String name, String description, Status status, int epic) {
        super(name, description, status);
        this.epicId = epic;
    }


    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}

package Model;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int epic) {
        super(name, description, status);
        this.epicId = epic;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", epicId=" + epicId +
                '}' + "\n";
    }
}

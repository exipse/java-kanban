package Model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int epic) {
        super(name, description, status);
        this.epicId = epic;
        this.type = TypeTask.SUBTASK;
    }

    public SubTask(String name, String description, Status status, int epic, LocalDateTime startTime, long duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epic;
        this.type = TypeTask.SUBTASK;
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", finishTime=" + finishTime +
                '}' + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String fromObjectToString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                id, type, name, status, description, startTime, duration, finishTime, epicId);
    }
}

package Model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected static int count;

    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected TypeTask type;
    protected LocalDateTime startTime;
    protected long duration;
    protected LocalDateTime finishTime;

    //конструктор для создания эпика
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    //Конструктор создания без startTime и duration
    public Task(String name, String description, Status status) {
        this.id = count += 1;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TypeTask.TASK;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, long duration) {
        this.id = count += 1;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TypeTask.TASK;
        this.startTime = startTime;
        this.duration = duration;
        this.finishTime = startTime.plusMinutes(duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (count == 0) {
            this.id = count += 1 + id;
        } else {
            this.id = count += 1;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TypeTask getType() {
        return type;
    }

    public void setType(TypeTask type) {
        this.type = type;
    }

    public static void setCount(int count) {
        Task.count = count;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", finishTime=" + finishTime +
                '}';
    }

    public String fromObjectToString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                id, type, name, status, description, startTime, duration, finishTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startTime, duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        if (!(startTime == null)) {
            this.finishTime = startTime.plusMinutes(this.duration);
        }
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        if (!(startTime == null)) {
            this.finishTime = this.startTime.plusMinutes(duration);
        }
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }
}



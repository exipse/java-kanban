package Model;

import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> subTaskId = new ArrayList<>();

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(ArrayList<Integer> subTaskId) {
        this.subTaskId = subTaskId;
    }

    public Epic(String name, String description){
        super(name, description);
        this.id = count = count +1;
        this.status = Status.NEW;
 }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", subTaskId=" + subTaskId +
                '}';
    }
}

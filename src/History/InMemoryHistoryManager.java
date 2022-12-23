package History;

import Model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static CustomLinkedList<Task> linkList = new CustomLinkedList<>();
    private static Map<Integer, CustomLinkedList.Node> helpHashMap = new HashMap<>();

    //метод по получению актуальных данных в таблице истории
    @Override
    public List<Task> getHistory() {
        return linkList.getTasks();
    }

    @Override
    //метод добавления тасок/эпиков/сабтасок в историю
    public void add(Task task) {
        if (helpHashMap.containsKey(task.getId())) {
            linkList.removeNode(helpHashMap.get(task.getId()));
        }
        linkList.linkLast(task);
        helpHashMap.put(task.getId(), linkList.tail);
    }

    @Override
    public void remove(int id) {
        linkList.removeNode(helpHashMap.get(id));
        helpHashMap.remove(id);
    }

    public static class CustomLinkedList<Task> {
        private Node<Task> head;
        private Node<Task> tail;
        private int size = 0;

        class Node<T> {
            public Node<T> next;
            public Node<T> prev;
            public T data;

            public Node(Node<T> prev, T data, Node<T> next) {
                this.next = next;
                this.prev = prev;
                this.data = data;
            }
        }

        //метод по добавлению задач в конец связанного списка
        public void linkLast(Task task) {
            final Node<Task> oldTail = tail;
            final Node<Task> newTail = new Node<>(oldTail, task, null);
            tail = newTail;
            if (oldTail == null) {
                head = newTail;
            } else {
                oldTail.next = newTail;
            }
            size++;
        }

        //метод по "вырезанию" задачи
        public void removeNode(Node<Task> node) {
            Node<Task> beforeTask = node.prev;
            Node<Task> afterTask = node.next;
            //установка новых головы/хвоста, если это удалеямый элемент
            if ((head.equals(node) && tail.equals(node))) {
                head = null;
                tail = null;
            } else if (head.equals(node)) {
                head = afterTask;
            } else if (tail.equals(node)) {
                tail = beforeTask;
            }
            node.prev = null;
            node.next = null;
            if (beforeTask != null && afterTask != null) {
                beforeTask.next = afterTask;
                afterTask.prev = beforeTask;
            } else if (beforeTask == null) {
                afterTask.prev = null;
            } else {
                beforeTask.next = null;
            }
            size--;
        }

        public List<Model.Task> getTasks() {
            List<Model.Task> arrayList = new ArrayList<>();
            Node<Task> currentElement = (Node<Task>) linkList.head;
            while (currentElement != null) {
                arrayList.add((Model.Task) currentElement.data);
                currentElement = currentElement.next;
            }
            return arrayList;
        }
    }
}

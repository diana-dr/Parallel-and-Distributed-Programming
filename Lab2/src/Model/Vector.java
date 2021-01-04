package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Vector {

    private List<Integer> content;
    private Integer size;

    public Vector(int size) {
        this.size = size;
        this.content = new ArrayList<>(size);
    }

    public void setContent(List<Integer> content) {
        this.content = content;
    }

    public Integer get(int index) {
        return content.get(index);
    }

    public void add(Integer element) {
        content.add(element);
    }

    public List<Integer> getContent() {
        return content;
    }

    public Integer getSize() {
        return size;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder("[");

        for (int i = 0; i < size - 1; i++)
            string.append(content.get(i)).append(", ");

        string.append(content.get(size - 1));
        string.append("]");

        return string.toString();
    }

    public int removeFirst() {
        int value = content.get(0);
        content.remove(0);
        size --;

        return value;
    }
}

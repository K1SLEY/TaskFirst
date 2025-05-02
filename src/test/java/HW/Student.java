package HW;

import groovy.transform.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString
public class Student {
    public List<Integer> getMarks() {
        return marks;
    }

    public void setMarks(List<Integer> marks) {
        this.marks = marks;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    Integer id;
    String name;
    List<Integer> marks = new ArrayList<>();
}

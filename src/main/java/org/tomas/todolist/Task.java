package org.tomas.todolist;

public class Task {

    //contador estatico para asignar IDs unicos
    private static int nextId = 1;
    private int id;
    private String name;
    private String description;
    private boolean completed;


    public Task(String name) {
        this.id = nextId++;
        this.name = name;
        this.description = null;
        this.completed = false;
    }

    public Task(String name, String description) {
        this.id = nextId++;
        this.name = name;
        this.description = description;
        this.completed = false;
    }

    public Task(int id, String name, String description, boolean completed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean idDone) {
        this.completed = idDone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

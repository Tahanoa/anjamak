package org.example.anjamak.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.anjamak.model.Task;

public class TaskDTO {
    private int id;
    private String title;
    private String description;
    private boolean completed = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public Task connvertToTask() {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);
        return task;
    }
}

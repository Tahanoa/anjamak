package org.example.anjamak.service;

import org.example.anjamak.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    void addTask(Task task);
    void updateTask(Task task);
    void deleteTask(Task task);
    Task getTask(int id);
    Page<Task> getTasks(Pageable pageable);
    Page<Task> findByTitle(String title, Pageable pageable);
    Page<Task> findByCompleted(boolean completed, Pageable pageable);

}

package org.example.anjamak.service;

import org.example.anjamak.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    void addTask(Task task);
    Task updateTask(Task task);
    void deleteTask(int id);
    Task getTask(int id);
    Page<Task> getTasks(Pageable pageable);
    Page<Task> findByTitle(String title, Pageable pageable);
    Page<Task> findByCompleted(boolean completed, Pageable pageable);

}

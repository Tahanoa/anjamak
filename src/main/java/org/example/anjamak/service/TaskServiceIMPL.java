package org.example.anjamak.service;

import org.example.anjamak.model.Task;
import org.example.anjamak.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceIMPL implements TaskService {
    private final TaskRepository taskRepository;
    public TaskServiceIMPL(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void addTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(int id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Task getTask(int id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task.not.found"));
    }

    @Override
    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public Page<Task> findByTitle(String title, Pageable pageable) {
        return taskRepository.findByTitleContaining(title, pageable);
    }

    @Override
    public Page<Task> findByCompleted(boolean completed, Pageable pageable) {
        return taskRepository.findByCompleted(completed, pageable);
    }

    @Override
    public void changeCompleted(int id, boolean completed) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task.not.found"));
        task.setCompleted(completed);
        taskRepository.save(task);
    }
}

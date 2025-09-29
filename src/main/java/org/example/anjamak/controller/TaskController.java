package org.example.anjamak.controller;

import jakarta.validation.Valid;
import org.example.anjamak.dto.TaskDTO;
import org.example.anjamak.model.Task;
import org.example.anjamak.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(allowedHeaders = "*", origins = "*")
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/addTask")
    public ResponseEntity<Object> addTask(@RequestBody @Valid TaskDTO task) {
        try {
            taskService.addTask(task.connvertToTask());
            return ResponseEntity.ok().body("Task.added.successfully");
        }catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }


    @GetMapping("/findAll")
    public ResponseEntity<Page<Task>>  findAll(Pageable pageable) {
        try {
            return ResponseEntity.ok().body( taskService.getTasks(pageable));
        }catch (RuntimeException e) {
            return ResponseEntity.status(409).body(null);
        }
    }

    @GetMapping("/findAllByTitle/{title}")
    public ResponseEntity<Object> findAllByTitle(@PathVariable String title, Pageable pageable) {
        try {
            Page<Task> task = taskService.findByTitle(title, pageable);
            return ResponseEntity.ok(task);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getTaskById/{id}")
            public ResponseEntity<Task> getEventById(@PathVariable int id) {
        try {
            return ResponseEntity.ok().body(taskService.getTask(id));
        }catch (RuntimeException e) {
            return ResponseEntity.status(409).body(null);
        }
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody @Valid TaskDTO taskDTO) {
        try {
            Task task = taskService.getTask(taskDTO.getId());
            if (task == null) {
                return ResponseEntity.status(404).body("Task.not.found");
            }
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setCompleted(taskDTO.isCompleted());
            taskService.updateTask(task);
            return ResponseEntity.ok().body("Task.updated.successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body("Task.not.found");
        }
    }

    }

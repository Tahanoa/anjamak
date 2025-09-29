package org.example.anjamak.controller;

import jakarta.validation.Valid;
import org.example.anjamak.dto.TaskDTO;
import org.example.anjamak.model.Task;
import org.example.anjamak.service.TaskService;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(allowedHeaders = "*", origins = "*")
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final MessageSourceAccessor messageSource;
    public TaskController(TaskService taskService, MessageSourceAccessor messageSource) {
        this.taskService = taskService;
        this.messageSource = messageSource;
    }


    @PostMapping("/addTask")
    public ResponseEntity<Object> addTask(@RequestBody @Valid TaskDTO task) {
        try {
            taskService.addTask(task.convertToTask());
            return ResponseEntity.ok().body(messageSource.getMessage("Task.added.successfully" ,"Task.added.successfully"  ));
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
            return ResponseEntity.status(404).body("Task.not.found");
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
            return ResponseEntity.ok().body(messageSource.getMessage("Task.deleted.successfully" , "Task.deleted.successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @RequestBody @Valid TaskDTO taskDTO) {
            Task existingTask = taskService.getTask(id);
            if (existingTask == null) {
                return ResponseEntity.status(404).body("Task.not.found");
            }

            existingTask.setTitle(taskDTO.title());
            existingTask.setDescription(taskDTO.description());

            Task updatedTask = taskService.updateTask(existingTask);
            return ResponseEntity.ok().body(messageSource.getMessage("Task.updated.successfully", "Task.updated.successfully"));
    }

    @PutMapping("/{id}/{completed}")
    public ResponseEntity<Object> changeCompleted(@PathVariable int id, @PathVariable boolean completed) {
        try {
            taskService.changeCompleted(id, completed);
            return ResponseEntity.ok().body(messageSource.getMessage("Task.updated.successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    }

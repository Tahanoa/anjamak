package org.example.anjamak.repository;

import org.example.anjamak.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findByTitleContaining(String title, Pageable pageable);
    Page<Task> findByCompleted(boolean completed, Pageable pageable);

}

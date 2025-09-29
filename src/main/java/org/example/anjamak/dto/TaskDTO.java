package org.example.anjamak.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.anjamak.model.Task;

public record TaskDTO(
        @Size(min = 3, max = 60, message = "title.size")
        @NotBlank(message = "title.null") String title,
        @Size(max = 500, message = "description.size") String description
) {
    public Task convertToTask() {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(false);
        return task;
    }
}

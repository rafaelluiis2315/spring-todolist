package com.rafael.todolist.task;

import com.rafael.todolist.util.Logger;
import com.rafael.todolist.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    Logger logger;
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        Object idUser = request.getAttribute("idUser");

        taskModel.setIdUser((UUID) idUser);

        LocalDateTime currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest().body("The start date|end date must be greater than the current date.");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest().body("The start date must be less than the end date.");
        }

        TaskModel taskSaved = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskSaved);
    }
    @GetMapping("/")
    public List<TaskModel> getAllTasks (HttpServletRequest request) {
        Object idUser = request.getAttribute("idUser");
        return this.taskRepository.findByIdUser((UUID) idUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity update (@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        TaskModel task = this.taskRepository.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity.badRequest().body("Task not found.");
        }

        Object idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)){
            return ResponseEntity.badRequest().body("User does not have permission to change this task");
        }

        Utils.copyNonNullProperties(taskModel, task);

        TaskModel taskUpdated = this.taskRepository.save(task);

        return ResponseEntity.ok().body(taskUpdated);
    }
}

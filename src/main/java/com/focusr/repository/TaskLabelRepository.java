package com.focusr.repository;

import com.focusr.entity.Task;
import com.focusr.entity.TaskLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskLabelRepository extends JpaRepository<TaskLabel, UUID> {
    List<TaskLabel> findByTask(Task task);
    void deleteByTask(Task task);
}

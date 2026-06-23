package com.stage.appToDo;

import java.util.List;

public record TaskBoardDto(
        List<TaskDto> todo,
        List<TaskDto> doing,
        List<TaskDto> done
) {
}

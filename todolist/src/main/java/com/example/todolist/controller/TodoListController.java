package com.example.todolist.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import com.example.todolist.entity.Todo;
import com.example.todolist.repository.TodoRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class TodoListController {

  // @Autowired は省略可のため省略
  private final TodoRepository todoRepository;

  @GetMapping("/todo")
  public ModelAndView showTodoList(ModelAndView mv) {
    // 一覧を検索して表示する
    mv.setViewName("todoList");
    List<Todo> todoList = todoRepository.findAll(); // .findAll() は SQL の "SELECT * FROM todo" に相当
    mv.addObject("todoList", todoList);
    return mv;
  }
}

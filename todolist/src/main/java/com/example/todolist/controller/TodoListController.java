package com.example.todolist.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoData;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.service.TodoService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class TodoListController {

  // @Autowired は省略可のため省略
  private final TodoRepository todoRepository;
  private final TodoService todoService;

  @GetMapping("/todo")
  public ModelAndView showTodoList(ModelAndView mv) {
    // 一覧を検索して表示する
    mv.setViewName("todoList");
    List<Todo> todoList = todoRepository.findAll(); // .findAll() は SQL の "SELECT * FROM todo" に相当
    mv.addObject("todoList", todoList);
    return mv;
  }

  // ToDo 入力フォーム表示
  // todoList.html で新規追加がクリックされたとき
  @GetMapping("/todo/create")
  public ModelAndView createTodo(ModelAndView mv) {
    mv.setViewName("todoForm");
    mv.addObject("todoData", new TodoData());
    return mv;
  }

  // todoForm.html で登録がクリックされたとき
  // 上述の createTodo() のオーバーロード
  @PostMapping("/todo/create")
  public ModelAndView createTodo(
      @ModelAttribute @Validated TodoData todoData,
      BindingResult result, // todoData のチェック結果が格納される
      ModelAndView mv) {

    // エラーチェック
    boolean isValid = todoService.isValid(todoData, result);
    if (!result.hasErrors() && isValid) {
      // エラーなし
      Todo todo = todoData.toEntity();
      todoRepository.saveAndFlush(todo); // SQL の INSERT文に相当
      return showTodoList(mv);
    } else {
      // エラーあり
      mv.setViewName("todoForm");
      return mv;
    }
  }

  // ToDo入力画面でキャンセル登録がクリックされたとき
  @PostMapping("/todo/cancel")
  public String cancel() {
    return "redirect:/todo";
  }
}
